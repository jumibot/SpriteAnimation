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

    // Entre qué velocidades se mueve la rueda, la de pintado y la de los sprites. Los fija main.
    private int minFps;
    private int maxFps;

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
            SpriteContract s = model.sprite(i);
            states.add(new SpriteState(s.name(), s.frame(), s.x(), s.y(), s.fps()));
        }
        return states;
    }

    @Override
    public void changeRenderSpeed(int delta) {
        view.setRenderFps(limit(view.renderFps() + delta));
    }

    @Override
    public void changeSpriteSpeed(int sprite, int delta) {
        SpriteContract s = model.sprite(sprite);
        s.setFps(limit(s.fps() + delta));                       // entre minFps y maxFps
    }

    /** Busca de arriba abajo: se pinta en orden, así que el último es el que queda encima. */
    @Override
    public int spriteAt(int x, int y) {
        for (int i = model.spriteCount() - 1; i >= 0; i--) {
            SpriteContract s = model.sprite(i);
            boolean inside = x >= s.x() && x < s.x() + s.width()
                          && y >= s.y() && y < s.y() + s.height();
            if (inside) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void moveSprite(int sprite, int x, int y) {
        model.sprite(sprite).moveTo(x, y);
    }

    @Override
    public void close() {
        model.stop();   // primero los hilos de los sprites
        view.stop();    // luego el de pintado y la ventana
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
