package presenter;

import java.awt.image.BufferedImage;

import contract.ModelContract;

/** Un modelo falso: un solo sprite, el pato, sin hilos y sin hojas de sprites. Da igual el número que pidan. */
class FakeModel implements ModelContract {

    final BufferedImage frame = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
    int fps = 8;
    int scale = 4;
    int x = 100;
    int y = 200;
    boolean started;

    @Override public void start() { this.started = true; }   // no arranca ningún hilo
    @Override public int spriteCount() { return 1; }
    @Override public BufferedImage frame(int index) { return this.frame; }
    @Override public int x(int index) { return this.x; }
    @Override public int y(int index) { return this.y; }
    @Override public String name(int index) { return "Pato"; }
    @Override public int fps(int index) { return this.fps; }
    @Override public void setFps(int index, int fps) { this.fps = fps; }
    @Override public int scale(int index) { return this.scale; }
    @Override public void setScale(int index, int scale) { this.scale = scale; }

    @Override
    public void moveTo(int index, int x, int y) {
        this.x = x;
        this.y = y;
    }
}
