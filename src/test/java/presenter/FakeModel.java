package presenter;

import java.awt.image.BufferedImage;

import contract.AnimationContract;
import model.Sprite;

/** Un modelo falso: un sprite fijo, sin hilos y sin hojas de sprites. */
class FakeModel implements AnimationContract.Model {

    final Sprite duck = new Sprite("Pato", blankFrames(4, 64, 64), 8, 100, 200);
    boolean started;
    boolean stopped;

    @Override public void start() { started = true; }   // no arranca el hilo del sprite
    @Override public void stop() { stopped = true; }
    @Override public int spriteCount() { return 1; }
    @Override public Sprite sprite(int index) { return duck; }

    private static BufferedImage[] blankFrames(int count, int width, int height) {
        BufferedImage[] frames = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            frames[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        return frames;
    }
}
