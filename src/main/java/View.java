import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * La vista de MVP: es la ventana. Pinta lo que le da el presentador y le avisa de lo que hace el usuario.
 * No toma decisiones.
 *
 * La usan tres hilos: el de main, que la construye; el de pintado (renderLoop);
 * y el de eventos de Swing, que trae el ratón y el cierre de la ventana.
 */
public class View extends JFrame implements AnimationContract.View {

    // Tamaño del lienzo. (WIDTH y HEIGHT ya existen en JFrame: por eso estos nombres.)
    static final int CANVAS_WIDTH = 640;
    static final int CANVAS_HEIGHT = 400;

    // El HUD: una línea por velocidad. HUD_Y es la base de la primera línea (la de pintado).
    static final int HUD_X = 16;
    static final int HUD_Y = 40;
    static final int HUD_LINE = 24;
    static final int HUD_WIDTH = 320;

    private static final Color BACKGROUND = new Color(0x1e2a30);
    private static final Color HUD_BACKGROUND = new Color(255, 255, 255, 40);
    private static final Color HUD_LABEL = new Color(0x7fd1ff);
    private static final Color HUD_TEXT = Color.WHITE;
    private static final Font HUD_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
    private static final Font HUD_FONT_BOLD = HUD_FONT.deriveFont(Font.BOLD);

    private final Viewer viewer;
    private AnimationContract.Presenter presenter;

    // Lo escribe el hilo de eventos (rueda) y lo lee el de pintado: volatile.
    private volatile int renderFps = 60;
    private volatile boolean running;
    private Thread renderThread;

    // Estado del arrastre: solo lo usa el hilo de eventos, así que no necesita volatile.
    private int dragged = -1;   // qué sprite se arrastra (-1: ninguno)
    private int grabX;          // dónde se ha cogido, medido desde la esquina del sprite
    private int grabY;

    public View() {
        super("SpriteAnimation");   // el título de la ventana
        viewer = new Viewer(CANVAS_WIDTH, CANVAS_HEIGHT);   // parte interna de la vista: la crea ella
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);   // cerramos nosotros, en orden
        add(viewer);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);   // centrada en la pantalla

        MouseHandler mouse = new MouseHandler();
        viewer.addMouseListener(mouse);
        viewer.addMouseMotionListener(mouse);
        viewer.addMouseWheelListener(mouse);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                presenter.close();
            }
        });
        // La ventana aún no se ve: la muestra start(), cuando todo está conectado.
    }

    @Override
    public void setPresenter(AnimationContract.Presenter presenter) {
        this.presenter = presenter;   // solo guarda la referencia
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

            List<AnimationContract.Presenter.SpriteState> sprites = presenter.sprites();   // la foto fija
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

    private void paintScene(Graphics2D g, List<AnimationContract.Presenter.SpriteState> sprites) {
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        for (AnimationContract.Presenter.SpriteState s : sprites) {
            g.drawImage(s.frame(), s.x(), s.y(), null);
        }
        paintHud(g, sprites);
    }

    /** El HUD: la velocidad de pintado y la de cada sprite, una por línea. */
    private void paintHud(Graphics2D g, List<AnimationContract.Presenter.SpriteState> sprites) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int lines = 1 + sprites.size();
        g.setColor(HUD_BACKGROUND);
        g.fillRoundRect(HUD_X - 8, HUD_Y - HUD_LINE, HUD_WIDTH, lines * HUD_LINE + 8, 10, 10);
        hudLine(g, 0, "Pintado", renderFps + " veces por segundo");
        for (int i = 0; i < sprites.size(); i++) {
            AnimationContract.Presenter.SpriteState s = sprites.get(i);
            hudLine(g, i + 1, s.name(), s.fps() + " fotogramas por segundo");
        }
    }

    private void hudLine(Graphics2D g, int line, String label, String value) {
        int y = HUD_Y + line * HUD_LINE - 6;
        g.setFont(HUD_FONT_BOLD);
        g.setColor(HUD_LABEL);
        g.drawString(label, HUD_X, y);
        int x = HUD_X + g.getFontMetrics().stringWidth(label);   // el valor, justo detrás de la etiqueta
        g.setFont(HUD_FONT);
        g.setColor(HUD_TEXT);
        g.drawString(" · " + value, x, y);
    }

    // ---------------------------------------------------------------- ratón

    /** Escucha el ratón sobre el lienzo. Swing llama a estos métodos desde su hilo de eventos. */
    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int line = (e.getY() - HUD_Y + HUD_LINE) / HUD_LINE;   // 0: pintado; 1, 2…: sprites
            int delta = -e.getWheelRotation();                      // rueda hacia arriba: sube
            if (!insideHud(e.getX(), e.getY(), line)) {
                return;                                              // fuera del HUD, no hace nada
            }
            if (line == 0) {
                presenter.changeRenderSpeed(delta);
            } else {
                presenter.changeSpriteSpeed(line - 1, delta);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            dragged = presenter.spriteAt(e.getX(), e.getY());
            if (dragged < 0) {
                return;   // no hay sprite bajo el puntero
            }
            // Guardamos dónde se ha cogido el sprite, para que no salte al puntero.
            AnimationContract.Presenter.SpriteState s = presenter.sprites().get(dragged);
            grabX = e.getX() - s.x();
            grabY = e.getY() - s.y();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (dragged >= 0) {
                presenter.moveSprite(dragged, e.getX() - grabX, e.getY() - grabY);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            dragged = -1;
        }

        /** Si el punto (x, y) cae sobre alguna línea del HUD. */
        private boolean insideHud(int x, int y, int line) {
            int left = HUD_X - 8;
            int top = HUD_Y - HUD_LINE;
            int lastLine = presenter.sprites().size();
            return x >= left && x < left + HUD_WIDTH
                && y >= top && line <= lastLine;
        }
    }
}
