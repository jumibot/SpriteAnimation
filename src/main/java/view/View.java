package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import contract.PresenterContract;
import contract.SpriteState;
import contract.ViewContract;

/**
 * La vista de MVP: es la ventana. Pinta lo que le da el presentador y le avisa de lo que hace el usuario.
 * No toma decisiones.
 *
 * La usan tres hilos: el de main, que la construye; el de pintado (renderLoop);
 * y el de eventos de Swing, que trae el ratón y el cierre de la ventana.
 */
public class View extends JFrame implements ViewContract {

    private static final Color BACKGROUND = new Color(0x1e2a30);

    private final Viewer viewer;
    private final Hud hud = new Hud();   // el recuadro de las velocidades: lo pinta él
    private final MouseHandler mouse = new MouseHandler(hud);   // el que escucha el ratón
    private PresenterContract presenter;

    // Lo escribe el hilo de eventos (rueda) y lo lee el de pintado: volatile.
    private volatile int renderFps;   // el valor inicial lo pone main con setRenderFps
    private volatile boolean running;
    private Thread renderThread;

    public View() {
        super("SpriteAnimation");   // el título de la ventana
        viewer = new Viewer();   // parte interna de la vista: la crea ella
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);   // cerramos nosotros, en orden
        add(viewer);
        setResizable(false);

        viewer.addMouseListener(mouse);
        viewer.addMouseMotionListener(mouse);
        viewer.addMouseWheelListener(mouse);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                presenter.close();
            }
        });
        // El tamaño y la velocidad de pintado los pone main, con setCanvasSize y setRenderFps.
        // La ventana aún no se ve: la muestra start(), cuando todo está conectado.
    }

    /** El tamaño del lienzo. No es del contrato: solo lo usa main, al montar la aplicación. */
    public void setCanvasSize(int width, int height) {
        viewer.setPreferredSize(new Dimension(width, height));
        pack();                        // la ventana se ajusta al lienzo
        setLocationRelativeTo(null);   // centrada en la pantalla
    }

    @Override
    public void setPresenter(PresenterContract presenter) {
        this.presenter = presenter;   // solo guarda la referencia
        mouse.setPresenter(presenter);   // y se la pasa al que escucha el ratón
    }

    @Override
    public int renderFps() {
        return renderFps;
    }

    @Override
    public void setRenderFps(int fps) {
        renderFps = fps;
    }

    @Override
    public void start() {
        // Lo que toca la ventana debe hacerlo el hilo de eventos de Swing.
        // invokeLater le pasa la tarea; él la hará en cuanto pueda.
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            viewer.createBufferStrategy(2);   // dos búferes: se pinta en uno mientras se ve el otro
            // Ya hay donde pintar: arranca el hilo de pintado.
            running = true;
            renderThread = new Thread(() -> renderLoop(), "Render");
            renderThread.start();
        });
    }

    /** Llega por el hilo de eventos de Swing, al cerrar la ventana. */
    @Override
    public void stop() {
        running = false;
        if (renderThread != null) {
            renderThread.interrupt();
            try {
                renderThread.join();   // que no pinte en una ventana cerrada
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();   // conserva el aviso de interrupción
            }
        }
        dispose();   // al cerrar la última ventana, Swing termina
    }

    // ---------------------------------------------------------------- pintado

    /** El hilo de pintado: en cada vuelta pide qué pintar, pinta y espera hasta la siguiente. */
    private void renderLoop() {
        BufferStrategy buffers = viewer.getBufferStrategy();
        while (running) {
            long start = System.nanoTime();

            List<SpriteState> sprites = presenter.sprites();   // la foto fija
            Graphics2D g = (Graphics2D) buffers.getDrawGraphics();   // el búfer que no se ve
            paintScene(g, sprites);
            g.dispose();
            buffers.show();                       // ahora se ve lo que acabamos de pintar
            Toolkit.getDefaultToolkit().sync();   // en algunos sistemas, que se vea ya

            // Espera lo que falte para que cada vuelta dure 1/renderFps segundos.
            long periodNs = 1_000_000_000L / renderFps;
            long elapsedNs = System.nanoTime() - start;
            long waitMs = (periodNs - elapsedNs) / 1_000_000;
            if (waitMs > 0) {
                try {
                    Thread.sleep(waitMs);
                } catch (InterruptedException e) {
                    return;   // nos piden parar
                }
            }
        }
    }

    private void paintScene(Graphics2D g, List<SpriteState> sprites) {
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, viewer.getWidth(), viewer.getHeight());
        for (SpriteState s : sprites) {
            g.drawImage(s.frame(), s.x(), s.y(), null);
        }
        hud.paint(g, renderFps, sprites);
    }
}
