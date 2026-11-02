import java.util.ArrayList;
import java.util.List;

/** El intermediario: traduce el modelo a lo que la vista pinta y aplica las reglas a cada gesto. */
public class Presenter implements AnimationContract.Presenter {

    static final int MIN_FPS = 1;
    static final int MAX_FPS = 120;

    private final AnimationContract.Model model;
    private final AnimationContract.View view;

    public Presenter(AnimationContract.Model model, AnimationContract.View view) {   // las recibe, no las crea
        this.model = model;
        this.view = view;
        view.setPresenter(this);   // se conecta con la vista: lo verás en «Conectar antes de arrancar»
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
            Sprite s = model.sprite(i);
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
        Sprite s = model.sprite(sprite);
        s.setFps(limit(s.fps() + delta));                       // entre 1 y 120
    }

    /** Busca de arriba abajo: se pinta en orden, así que el último es el que queda encima. */
    @Override
    public int spriteAt(int x, int y) {
        for (int i = model.spriteCount() - 1; i >= 0; i--) {
            Sprite s = model.sprite(i);
            boolean inside = x >= s.x() && x < s.x() + s.width()
                          && y >= s.y() && y < s.y() + s.height();
            if (inside) return i;
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

    private static int limit(int fps) {
        return Math.max(MIN_FPS, Math.min(MAX_FPS, fps));
    }
}
