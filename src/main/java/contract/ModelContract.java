package contract;

/** El contrato del modelo: lo que el presentador puede pedirle. */
public interface ModelContract {
    void start();                      // pone en marcha las animaciones (un hilo por sprite)
    int spriteCount();                 // cuántos sprites hay
    SpriteContract sprite(int index);  // uno de los sprites, por su número
}
