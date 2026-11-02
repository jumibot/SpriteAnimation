package contract;

import java.awt.image.BufferedImage;

/** El contrato de un sprite del modelo: lo que el presentador puede pedirle. */
public interface SpriteContract {
    String name();
    BufferedImage frame();     // el fotograma que toca ahora
    int width();
    int height();
    int x();
    int y();
    int fps();
    void setFps(int fps);
    void moveTo(int x, int y);
}
