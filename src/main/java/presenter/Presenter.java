package presenter;

import java.util.ArrayList;
import java.util.List;

import contract.ModelContract;
import contract.PresenterContract;
import contract.SpriteContract;
import contract.SpriteState;
import contract.ViewContract;

/** El presentador de MVP: traduce el modelo a lo que la vista pinta y aplica las reglas a cada gesto. */
public class Presenter implements PresenterContract {

    private final ModelContract model;
    private final ViewContract view;

    // Entre qué velocidades se mueve la rueda, la de pintado y la de los sprites. main los cambia con setFpsLimits.
    private int minFps = 1;
    private int maxFps = 120;

    public Presenter(ModelContract model, ViewContract view) {   // las recibe, no las crea
        this.model = model;
        this.view = view;

        view.setPresenter(this);   // se conecta con la vista: lo verás en «Conectar antes de arrancar»
    }

    /** Los límites de velocidad. No es del contrato: solo lo usa main, al montar la aplicación. */
    public void setFpsLimits(int min, int max) {
        minFps = min;
        maxFps = max;
    }

    @Override
    public void start() {          // solo arranca
        model.start();
        view.start();
    }

    @Override
    public List<SpriteState> sprites() {
        List<SpriteState> states = new ArrayList<>();
        for (int i = 0; i < model.spriteCount(); i++) {
            SpriteContract sprite = model.sprite(i);
            states.add(new SpriteState(sprite.name(), sprite.frame(), sprite.x(), sprite.y(), sprite.fps()));
        }

        return states;
    }

    @Override
    public void changeRenderSpeed(int delta) {
        view.setRenderFps(limit(view.renderFps() + delta));
    }

    @Override
    public void changeSpriteSpeed(int index, int delta) {
        SpriteContract sprite = model.sprite(index);
        sprite.setFps(limit(sprite.fps() + delta));   // entre minFps y maxFps
    }

    /** Busca de arriba abajo: se pinta en orden, así que el último es el que queda encima. */
    @Override
    public int spriteAt(int x, int y) {
        for (int i = model.spriteCount() - 1; i >= 0; i--) {
            SpriteContract sprite = model.sprite(i);
            boolean inside = x >= sprite.x() && x < sprite.x() + sprite.width()
                          && y >= sprite.y() && y < sprite.y() + sprite.height();
            if (inside) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void moveSprite(int index, int x, int y) {
        model.sprite(index).moveTo(x, y);
    }

    /** Deja la velocidad entre minFps y maxFps. */
    private int limit(int fps) {
        if (fps < minFps) {
            return minFps;
        }
        if (fps > maxFps) {
            return maxFps;
        }
        return fps;
    }
}
