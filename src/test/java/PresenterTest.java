import java.util.List;

/**
 * Prueba el presentador sin ventana, con FakeView y FakeModel.
 * Es el ejercicio «Una prueba sin ventana» del documento, resuelto.
 */
public class PresenterTest {

    public static void main(String[] args) {
        System.out.println("PresenterTest (sin ventana)");
        connects();
        startAndClose();
        renderSpeedStaysBetween1And120();
        spriteSpeedStaysBetween1And120();
        spritesGivesAPhoto();
        spriteAtAndMove();
        Check.finish("PresenterTest");
    }

    static void connects() {
        FakeView view = new FakeView();
        Presenter presenter = new Presenter(new FakeModel(), view);
        Check.that("el constructor se presenta a la vista", view.presenter == presenter);
    }

    static void startAndClose() {
        FakeModel model = new FakeModel();
        FakeView view = new FakeView();
        Presenter presenter = new Presenter(model, view);
        Check.that("antes de start() no hay nada arrancado", !model.started && !view.started);
        presenter.start();
        Check.that("start() arranca el modelo y la vista", model.started && view.started);
        presenter.close();
        Check.that("close() para el modelo y la vista", model.stopped && view.stopped);
    }

    static void renderSpeedStaysBetween1And120() {
        FakeView view = new FakeView();
        Presenter presenter = new Presenter(new FakeModel(), view);
        presenter.changeRenderSpeed(+1);
        Check.equal("la rueda sube el pintado de 60 a 61", 61, view.renderFps());
        presenter.changeRenderSpeed(-2);
        Check.equal("y lo baja a 59", 59, view.renderFps());
        presenter.changeRenderSpeed(+500);
        Check.equal("el pintado no pasa de 120", 120, view.renderFps());
        presenter.changeRenderSpeed(-500);
        Check.equal("el pintado no baja de 1", 1, view.renderFps());
    }

    static void spriteSpeedStaysBetween1And120() {
        FakeModel model = new FakeModel();
        Presenter presenter = new Presenter(model, new FakeView());
        presenter.changeSpriteSpeed(0, +1);
        Check.equal("la rueda sube el pato de 8 a 9", 9, model.duck.fps());
        presenter.changeSpriteSpeed(0, +500);
        Check.equal("el pato no pasa de 120", 120, model.duck.fps());
        presenter.changeSpriteSpeed(0, -500);
        Check.equal("el pato no baja de 1", 1, model.duck.fps());
    }

    static void spritesGivesAPhoto() {
        FakeModel model = new FakeModel();
        Presenter presenter = new Presenter(model, new FakeView());
        List<AnimationContract.Presenter.SpriteState> photo = presenter.sprites();
        Check.equal("sprites() da un SpriteState por sprite", 1, photo.size());
        AnimationContract.Presenter.SpriteState duck = photo.get(0);
        Check.that("con su nombre, posición y velocidad",
                   duck.name().equals("Pato") && duck.x() == 100 && duck.y() == 200 && duck.fps() == 8);
        model.duck.moveTo(0, 0);
        Check.that("la foto no cambia aunque cambie el modelo", duck.x() == 100 && duck.y() == 200);
    }

    static void spriteAtAndMove() {
        FakeModel model = new FakeModel();   // el pato está en (100, 200) y mide 64×64
        Presenter presenter = new Presenter(model, new FakeView());
        Check.equal("spriteAt encuentra el pato en su esquina", 0, presenter.spriteAt(100, 200));
        Check.equal("y dentro", 0, presenter.spriteAt(163, 263));
        Check.equal("fuera no hay sprite", -1, presenter.spriteAt(164, 200));
        presenter.moveSprite(0, 10, 20);
        Check.that("moveSprite lleva el pato a (10, 20)", model.duck.x() == 10 && model.duck.y() == 20);
        Check.equal("y ya no está donde estaba", -1, presenter.spriteAt(100, 200));
    }
}
