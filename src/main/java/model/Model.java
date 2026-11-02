package model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import contract.ModelContract;

/** El modelo de MVP: el estado de la aplicación, los sprites. No sabe que existe una ventana. */
public class Model implements ModelContract {

    private final List<Sprite> sprites = new ArrayList<>();

    public Model() {}   // nace vacío: main le pide las animaciones con addSprite

    /**
     * Crea un sprite a partir de su hoja y lo añade. No es del contrato: solo lo usa main,
     * y así el presentador no puede crear animaciones (no compilaría).
     */
    public void addSprite(String name, String sheet, int frameWidth, int frameHeight, int scale,
                          int fps, int x, int y) {
        BufferedImage[] frames = SpriteSheet.load(sheet, frameWidth, frameHeight, scale);
        this.sprites.add(new Sprite(name, frames, fps, x, y));
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

    @Override
    public Sprite sprite(int index) {
        return this.sprites.get(index);
    }
}
