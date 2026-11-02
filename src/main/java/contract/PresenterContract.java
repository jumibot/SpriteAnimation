package contract;

import java.util.List;

/** El contrato del presentador: lo que la vista puede pedirle. */
public interface PresenterContract {
    List<SpriteState> sprites();                  // la foto de todos los sprites
    void changeRenderSpeed(int delta);            // subir o bajar el ritmo de pintado
    void changeSpriteSpeed(int sprite, int delta); // lo mismo con la velocidad de un sprite
    int spriteAt(int x, int y);                   // qué sprite hay en un punto (-1 si ninguno)
    void moveSprite(int sprite, int x, int y);    // llevar un sprite a otro sitio
    void start();                                 // poner en marcha la aplicación (lo llama main)
    void close();                                 // pararlo todo al cerrar la ventana
}
