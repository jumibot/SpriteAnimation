package contract;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * El contrato de MVP: lo que cada pieza puede pedirle a las demás.
 * Una interfaz puede tener interfaces anidadas: aquí hay una por pieza y otra para los sprites.
 */
public interface AnimationContract {

    /** Lo que el presentador puede pedirle al modelo. */
    interface Model {
        void start();              // pone en marcha las animaciones (un hilo por sprite)
        void stop();               // las para al cerrar la aplicación
        int spriteCount();         // cuántos sprites hay
        Sprite sprite(int index);  // uno de los sprites, por su número
    }

    /** Lo que el presentador puede pedirle a un sprite del modelo. */
    interface Sprite {
        String name();
        BufferedImage frame();     // el fotograma que toca ahora
        int width();
        int height();
        int x();
        int y();
        int fps();
        void setFps(int fps);
        void moveTo(int x, int y);
    }

    /** Lo que el presentador puede pedirle a la vista. */
    interface View {
        void setPresenter(Presenter presenter);  // a quién pedirle qué pintar
        void start();                            // empezar a pintar
        void stop();                             // dejar de pintar y cerrar la ventana
        int renderFps();                         // cuántas veces por segundo pinta
        void setRenderFps(int fps);              // cambiar ese ritmo
    }

    /** Lo que la vista puede pedirle al presentador. */
    interface Presenter {

        /** Una foto fija de un sprite: lo que la vista necesita para pintarlo. Nadie la cambia. */
        record SpriteState(String name, BufferedImage frame, int x, int y, int fps) {}

        List<SpriteState> sprites();                  // la foto de todos los sprites
        void changeRenderSpeed(int delta);            // subir o bajar el ritmo de pintado
        void changeSpriteSpeed(int sprite, int delta); // lo mismo con la velocidad de un sprite
        int spriteAt(int x, int y);                   // qué sprite hay en un punto (-1 si ninguno)
        void moveSprite(int sprite, int x, int y);    // llevar un sprite a otro sitio
        void start();                                 // poner en marcha la aplicación (lo llama main)
        void close();                                 // pararlo todo al cerrar la ventana
    }
}
