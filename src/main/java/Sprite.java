import java.awt.image.BufferedImage;

/**
 * Una animación con su propio hilo. El hilo avanza el fotograma según el tiempo que ha pasado,
 * así que el ritmo es el mismo en cualquier equipo y no depende de lo rápido que se pinte.
 *
 * Los campos que escribe un hilo y lee otro son volatile, para que cada hilo vea el último
 * valor escrito: el fotograma lo escribe el hilo del sprite; la velocidad y la posición,
 * el hilo de eventos de Swing (rueda y arrastre); y todos los lee el hilo de pintado.
 */
public class Sprite implements Runnable {

    private static final long TICK_MS = 5;   // cada cuánto mira el hilo si toca cambiar de fotograma

    private final String name;
    private final BufferedImage[] frames;
    private volatile int frame;
    private volatile int fps;
    private volatile int x;
    private volatile int y;
    private volatile boolean running;
    private Thread thread;

    public Sprite(String name, BufferedImage[] frames, int fps, int x, int y) {
        this.name = name;
        this.frames = frames;
        this.fps = fps;
        this.x = x;
        this.y = y;
    }

    public String name() { return name; }
    public BufferedImage frame() { return frames[frame]; }
    public int frameIndex() { return frame; }
    public int frameCount() { return frames.length; }
    public int width() { return frames[0].getWidth(); }
    public int height() { return frames[0].getHeight(); }
    public int fps() { return fps; }
    public void setFps(int fps) { this.fps = fps; }
    public int x() { return x; }
    public int y() { return y; }

    /** Mueve el sprite. Si el hilo de pintado lee entre las dos escrituras, verá un paso a medias:
     *  durante un fotograma, nada grave. Por eso basta con volatile y no hace falta sincronizar. */
    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Crea y arranca el hilo del sprite. */
    public void start() {
        running = true;
        thread = new Thread(this, "Sprite " + name);
        thread.start();
    }

    /** Para el hilo y espera a que termine. */
    public void stop() {
        running = false;
        if (thread == null) return;
        thread.interrupt();            // lo despierta si está dormido en sleep
        try {
            thread.join();             // espera a que salga de run()
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        long previous = System.nanoTime();
        double pending = 0;            // fotogramas que tocaba avanzar y aún no se han avanzado
        while (running) {
            long now = System.nanoTime();
            pending += (now - previous) / 1_000_000_000.0 * fps;
            previous = now;
            while (pending >= 1) {
                frame = (frame + 1) % frames.length;
                pending--;
            }
            try {
                Thread.sleep(TICK_MS);
            } catch (InterruptedException e) {
                return;                // nos piden parar
            }
        }
    }
}
