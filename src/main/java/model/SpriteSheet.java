package model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/** Ayuda del modelo: carga una hoja de sprites y la recorta en fotogramas. */
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
        BufferedImage sheet = loadImage(resource);
        int columns = sheet.getWidth() / frameWidth;    // cuántos fotogramas caben a lo ancho
        int rows = sheet.getHeight() / frameHeight;     // y cuántos a lo alto

        BufferedImage[] frames = new BufferedImage[columns * rows];
        int next = 0;                                   // posición del siguiente fotograma en el array
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                // La esquina del fotograma en la hoja: tantos anchos y altos como columnas y filas haya antes.
                int x = column * frameWidth;
                int y = row * frameHeight;
                BufferedImage cut = sheet.getSubimage(x, y, frameWidth, frameHeight);
                frames[next] = enlarge(cut, scale);
                next++;
            }
        }

        return frames;
    }

    /** Lee la imagen de la hoja, que va dentro del programa, junto a las clases. */
    private static BufferedImage loadImage(String resource) {
        URL url = SpriteSheet.class.getResource(resource);   // dónde está el archivo (null si no está)
        if (url == null) {
            throw new IllegalArgumentException("No se encuentra la hoja " + resource);
        }

        try {
            return ImageIO.read(url);
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
