import java.awt.image.BufferedImage;

/**
 * Una animación del modelo, con su propio hilo que va cambiando de fotograma.
 * El ritmo depende del tiempo que pasa, no de lo rápido que se pinte.
 */
public class Sprite implements Runnable {

    private static final long TICK_MS = 5;   // cada cuánto mira el hilo si toca cambiar de fotograma

    private final String name;
    private final BufferedImage[] frames;

    // Estos campos los escribe un hilo y los lee otro (el de pintado).
    // volatile hace que cada hilo vea siempre el último valor escrito.
    private volatile int frameIndex;   // lo cambia el hilo del sprite
    private volatile int fps;          // lo cambia la rueda del ratón
    private volatile int x;            // x e y: los cambia el arrastre
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
    public BufferedImage frame() { return frames[frameIndex]; }
    public int frameIndex() { return frameIndex; }
    public int width() { return frames[0].getWidth(); }
    public int height() { return frames[0].getHeight(); }
    public int fps() { return fps; }
    public void setFps(int fps) { this.fps = fps; }
    public int x() { return x; }
    public int y() { return y; }

    /** Mueve el sprite. Si se pinta justo entre las dos líneas, se verá a medio mover
     *  durante un fotograma: nada grave, así que no hace falta sincronizar. */
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
        if (thread == null) {
            return;   // nunca se arrancó
        }
        thread.interrupt();            // lo despierta si está dormido en sleep
        try {
            thread.join();             // espera a que salga de run()
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();   // conserva el aviso de interrupción
        }
    }

    /** Lo que hace el hilo del sprite: cada poco, avanza los fotogramas que tocan por el tiempo pasado. */
    @Override
    public void run() {
        long previous = System.nanoTime();
        double pending = 0;            // fotogramas que tocaba avanzar y aún no se han avanzado
        while (running) {
            long now = System.nanoTime();
            double seconds = (now - previous) / 1_000_000_000.0;
            pending += seconds * fps;
            previous = now;
            while (pending >= 1) {
                frameIndex = (frameIndex + 1) % frames.length;   // tras el último, vuelve al primero
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
