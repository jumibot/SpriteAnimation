package model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import contract.ModelContract;

/**
 * El modelo de MVP: el estado de la aplicación, los sprites. No sabe que existe una ventana.
 * No deja salir nada suyo: hacia fuera da números, textos y copias ampliadas de los fotogramas.
 */
public class Model implements ModelContract {

    private final List<Sprite> sprites = new ArrayList<>();

    public Model() {}   // nace vacío: main le pide las animaciones con addSprite

    /**
     * Crea un sprite a partir de su hoja y lo añade. No es del contrato: solo lo usa main,
     * y así el presentador no puede crear animaciones (no compilaría).
     */
    public void addSprite(String name, String sheet, int frameWidth, int frameHeight, int scale,
                          int fps, int x, int y) {
        BufferedImage[] frames = SpriteSheet.load(sheet, frameWidth, frameHeight);   // a su tamaño original
        this.sprites.add(new Sprite(name, frames, scale, fps, x, y));
    }

    @Override
    public void start() {
        for (Sprite sprite : this.sprites) {
            sprite.start();
        }
    }

    @Override
    public int spriteCount() {
        return this.sprites.size();
    }

    // Los datos de cada sprite, por su número. El Sprite no sale de aquí.

    /**
     * El fotograma que toca ahora, en una copia ya ampliada a su escala:
     * quien lo reciba no puede cambiar el del modelo.
     */
    @Override
    public BufferedImage frame(int index) {
        Sprite sprite = this.sprites.get(index);
        return enlargedCopy(sprite.frame(), sprite.scale());
    }

    @Override public int x(int index) { return this.sprites.get(index).x(); }
    @Override public int y(int index) { return this.sprites.get(index).y(); }
    @Override public String name(int index) { return this.sprites.get(index).name(); }
    @Override public int fps(int index) { return this.sprites.get(index).fps(); }
    @Override public void setFps(int index, int fps) { this.sprites.get(index).setFps(fps); }
    @Override public int scale(int index) { return this.sprites.get(index).scale(); }
    @Override public void setScale(int index, int scale) { this.sprites.get(index).setScale(scale); }
    @Override public void moveTo(int index, int x, int y) { this.sprites.get(index).moveTo(x, y); }

    /** Una imagen nueva, scale veces más grande que la original. Sin suavizar, para que se vean los píxeles (pixel art). */
    private static BufferedImage enlargedCopy(BufferedImage image, int scale) {
        int width = image.getWidth() * scale;
        int height = image.getHeight() * scale;

        BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copy.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);   // sin interpolación: vecino más próximo
        g.dispose();
        return copy;
    }
}
