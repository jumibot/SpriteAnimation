package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
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
    private volatile int renderFps = 60;

    public View() {
        super("SpriteAnimation");   // el título de la ventana
        viewer = new Viewer();   // parte interna de la vista: la crea ella
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);   // al pulsar la X, termina la aplicación
        add(viewer);
        setResizable(false);

        viewer.addMouseListener(mouse);
        viewer.addMouseMotionListener(mouse);
        viewer.addMouseWheelListener(mouse);

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
        // invokeLater le pasa el método showWindow; él lo hará en cuanto pueda.
        SwingUtilities.invokeLater(this::showWindow);
    }

    /** Lo hace el hilo de eventos de Swing: enseña la ventana y arranca el hilo de pintado. */
    private void showWindow() {
        setVisible(true);
        viewer.createBufferStrategy(2);   // dos búferes: se pinta en uno mientras se ve el otro

        // Ya hay donde pintar: arranca el hilo de pintado.
        Thread renderThread = new Thread(this::renderLoop, "Render");
        renderThread.start();
    }

    // ---------------------------------------------------------------- pintado

    /** El hilo de pintado: en cada vuelta pide qué pintar, pinta y espera hasta la siguiente. */
    private void renderLoop() {
        BufferStrategy buffers = viewer.getBufferStrategy();

        while (true) {
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
                    return;   // nadie interrumpe este hilo; si lo hiciera, terminaría
                }
            }
        }
    }

    private void paintScene(Graphics2D g, List<SpriteState> sprites) {
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, viewer.getWidth(), viewer.getHeight());

        for (SpriteState state : sprites) {
            g.drawImage(state.frame(), state.x(), state.y(), null);
        }

        hud.paint(g, renderFps, sprites);
    }
}
