package presenter;

import java.util.ArrayList;
import java.util.List;

import contract.ModelContract;
import contract.SpriteView;
import contract.PresenterContract;
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
        this.minFps = min;
        this.maxFps = max;
    }

    @Override
    public void start() {          // solo arranca
        this.model.start();
        this.view.start();
    }

    /** De cada sprite, solo lo que la vista enseña: su imagen, dónde va, y su nombre y velocidad. */
    @Override
    public List<SpriteView> spriteViews() {
        List<SpriteView> spriteViews = new ArrayList<>();
        for (int i = 0; i < this.model.spriteCount(); i++) {
            spriteViews.add(new SpriteView(this.model.frame(i), this.model.x(i), this.model.y(i),
                                     this.model.name(i), this.model.fps(i)));
        }

        return List.copyOf(spriteViews);   // una lista que nadie puede cambiar
    }

    @Override
    public int spriteCount() {
        return this.model.spriteCount();
    }

    @Override
    public void changeRenderSpeed(int delta) {
        this.view.setRenderFps(this.limit(this.view.renderFps() + delta));
    }

    @Override
    public void changeSpriteSpeed(int index, int delta) {
        this.model.setFps(index, this.limit(this.model.fps(index) + delta));   // entre minFps y maxFps
    }

    @Override
    public void moveSprite(int index, int x, int y) {
        this.model.moveTo(index, x, y);
    }

    /** Deja la velocidad entre minFps y maxFps. */
    private int limit(int fps) {
        if (fps < this.minFps) {
            return this.minFps;
        }
        if (fps > this.maxFps) {
            return this.maxFps;
        }
        return fps;
    }
}
