import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/** Carga una hoja de sprites y la recorta en fotogramas. */
public final class SpriteSheet {

    private SpriteSheet() {}   // solo tiene métodos estáticos: no se crean objetos

    /**
     * Recorta la hoja en fotogramas de frameWidth × frameHeight y los amplía scale veces.
     * Recorre la hoja por filas, de izquierda a derecha y de arriba abajo, así que sirve
     * igual para una hoja en columna (el pato, 16×64) que en fila (la explosión, 384×48).
     *
     * @param resource ruta del recurso dentro del classpath, por ejemplo "/sprites/pato.png"
     */
    public static BufferedImage[] load(String resource, int frameWidth, int frameHeight, int scale) {
        BufferedImage sheet = read(resource);
        int columns = sheet.getWidth() / frameWidth;
        int rows = sheet.getHeight() / frameHeight;
        BufferedImage[] frames = new BufferedImage[columns * rows];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                BufferedImage cut = sheet.getSubimage(column * frameWidth, row * frameHeight,
                                                      frameWidth, frameHeight);
                frames[row * columns + column] = enlarge(cut, scale);
            }
        }
        return frames;
    }

    private static BufferedImage read(String resource) {
        try (InputStream in = SpriteSheet.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalArgumentException("No se encuentra la hoja " + resource);
            }
            return ImageIO.read(in);
        } catch (IOException e) {
            throw new IllegalStateException("No se puede leer la hoja " + resource, e);
        }
    }

    /** Amplía sin suavizar, para que se vean los píxeles (pixel art). */
    private static BufferedImage enlarge(BufferedImage frame, int scale) {
        int width = frame.getWidth() * scale;
        int height = frame.getHeight() * scale;
        BufferedImage big = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = big.createGraphics();
        g.drawImage(frame, 0, 0, width, height, null);   // sin interpolación: vecino más próximo
        g.dispose();
        return big;
    }
}
