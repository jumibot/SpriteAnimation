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
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * La vista: crea la ventana, tiene el hilo de pintado y escucha el ratón sobre el Viewer.
 * No toma decisiones: pinta lo que le da el presentador y le avisa de lo que hace el usuario.
 *
 * Hay tres clases de hilos que tocan esta clase:
 *  - el de main, que la construye;
 *  - el de pintado (renderLoop), que pide al presentador qué pintar y pinta;
 *  - el de eventos de Swing, que trae la rueda, el arrastre y el cierre de la ventana.
 */
public class View implements AnimationContract.View {

    static final int WIDTH = 640;
    static final int HEIGHT = 400;

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

    private final JFrame frame;
    private final Viewer viewer;
    private AnimationContract.Presenter presenter;

    // Lo escribe el hilo de eventos (rueda) y lo lee el de pintado: volatile.
    private volatile int renderFps = 60;
    private volatile boolean running;
    private Thread renderThread;

    // Estado del arrastre: solo lo usa el hilo de eventos, así que no necesita volatile.
    private int dragged = -1;
    private int dragDx;
    private int dragDy;

    public View() {
        viewer = new Viewer(WIDTH, HEIGHT);   // parte interna de la vista: la crea ella
        frame = new JFrame("SpriteAnimation");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);   // cerramos nosotros, en orden
        frame.add(viewer);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        listenToMouse();
        frame.addWindowListener(new WindowAdapter() {
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
        // Mostrar la ventana es cosa del hilo de eventos de Swing: se lo pedimos y esperamos.
        onSwingThread(() -> {
            frame.setVisible(true);
            viewer.createBufferStrategy(2);   // dos búferes: se pinta en uno mientras se ve el otro
        });
        running = true;
        renderThread = new Thread(this::renderLoop, "Render");
        renderThread.start();
    }

    @Override
    public void stop() {
        running = false;
        if (renderThread != null && renderThread != Thread.currentThread()) {
            renderThread.interrupt();
            try {
                renderThread.join();   // que no pinte en una ventana cerrada
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        SwingUtilities.invokeLater(frame::dispose);   // al cerrar la última ventana, Swing termina
    }

    // ---------------------------------------------------------------- pintado

    /** El hilo de pintado: en cada vuelta pide qué pintar, pinta y espera hasta la siguiente. */
    private void renderLoop() {
        BufferStrategy buffers = viewer.getBufferStrategy();
        while (running) {
            long start = System.nanoTime();
            List<AnimationContract.Presenter.SpriteState> sprites = presenter.sprites();   // la foto fija
            do {
                do {
                    Graphics2D g = (Graphics2D) buffers.getDrawGraphics();
                    try {
                        paint(g, sprites);
                    } finally {
                        g.dispose();
                    }
                } while (buffers.contentsRestored());
                buffers.show();
            } while (buffers.contentsLost());
            Toolkit.getDefaultToolkit().sync();   // en algunos sistemas, que se vea ya

            long period = 1_000_000_000L / renderFps;
            long wait = (period - (System.nanoTime() - start)) / 1_000_000;
            if (wait > 0) {
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    return;   // nos piden parar
                }
            }
        }
    }

    private void paint(Graphics2D g, List<AnimationContract.Presenter.SpriteState> sprites) {
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, WIDTH, HEIGHT);
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
        int x = HUD_X + g.getFontMetrics().stringWidth(label);
        g.setFont(HUD_FONT);
        g.setColor(HUD_TEXT);
        g.drawString(" · " + value, x, y);
    }

    // ---------------------------------------------------------------- ratón

    /** Los eventos del ratón llegan por el hilo de eventos de Swing. La vista solo avisa. */
    private void listenToMouse() {
        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getX() < HUD_X - 8 || e.getX() >= HUD_X - 8 + HUD_WIDTH) return;   // fuera del HUD
                if (e.getY() < HUD_Y - HUD_LINE) return;
                int line = (e.getY() - HUD_Y + HUD_LINE) / HUD_LINE;   // 0: pintado; 1, 2…: sprites
                if (line > presenter.sprites().size()) return;
                int delta = -e.getWheelRotation();                      // rueda hacia arriba: sube
                if (line == 0) presenter.changeRenderSpeed(delta);
                else presenter.changeSpriteSpeed(line - 1, delta);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                dragged = presenter.spriteAt(e.getX(), e.getY());
                if (dragged < 0) return;
                // Guardamos dónde se ha cogido el sprite, para que no salte al puntero.
                AnimationContract.Presenter.SpriteState s = presenter.sprites().get(dragged);
                dragDx = e.getX() - s.x();
                dragDy = e.getY() - s.y();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragged >= 0) presenter.moveSprite(dragged, e.getX() - dragDx, e.getY() - dragDy);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragged = -1;
            }
        };
        viewer.addMouseListener(mouse);
        viewer.addMouseMotionListener(mouse);
        viewer.addMouseWheelListener(mouse);
    }

    /** Ejecuta la tarea en el hilo de eventos de Swing y espera a que termine. */
    private static void onSwingThread(Runnable task) {
        try {
            SwingUtilities.invokeAndWait(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getCause());
        }
    }
}
