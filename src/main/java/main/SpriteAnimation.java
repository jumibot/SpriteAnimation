package main;

import contract.AnimationContract;
import model.Model;
import presenter.Presenter;
import view.View;

/** La clase principal: monta la aplicación y la arranca. Es el único sitio que crea las capas. */
public class SpriteAnimation {

    public static void main(String[] args) {
        Model model = new Model();
        // nombre, hoja, ancho y alto del fotograma, escala, fotogramas por segundo, x, y
        model.addSprite("Pato", "/sprites/pato.png", 16, 16, 4, 8, 120, 220);
        model.addSprite("Explosión", "/sprites/explosion.png", 48, 48, 3, 12, 360, 160);
        AnimationContract.View view = new View();
        AnimationContract.Presenter presenter = new Presenter(model, view);
        presenter.start();
        // main termina aquí; la aplicación sigue en los hilos que ha arrancado start()
    }
}
