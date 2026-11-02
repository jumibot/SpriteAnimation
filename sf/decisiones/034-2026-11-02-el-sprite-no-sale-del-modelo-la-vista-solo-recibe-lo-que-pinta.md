# El sprite no sale del modelo: la vista solo recibe lo que pinta

Ningún objeto sprite sale del paquete model. Desaparece SpriteContract: sustituye por completo a la decisión de que el contrato exponga una interfaz Sprite (025) y cambia en parte la 012 y la 017.

- Modelo: ModelContract responde por número de sprite: frame(i), x(i), y(i), name(i), fps(i), setFps(i, fps), moveTo(i, x, y), spriteCount(), start().
- Pintado: la vista pide al presentador una List<Picture>, con record Picture(BufferedImage image, int x, int y) en contract, que sustituye a SpriteState. La vista pinta cada Picture y no sabe qué es un sprite.
- HUD: el presentador no escribe textos. Le pasa a la vista el nombre y la velocidad de cada sprite llamando a un método de la vista (view.showSprite(i, name, fps)) al arrancar y cada vez que cambia una velocidad. La vista los guarda y monta el texto del HUD.
- Ratón: la vista se encarga de la interacción con el usuario. Sabe qué sprite hay bajo el ratón con las Picture que ha pintado (el de encima gana); spriteAt sale del presentador. Al arrastrar, manda la posición nueva con presenter.moveSprite(i, x, y). Las acciones las decide el presentador.

Por qué: separación clara de MVP para alumnos de 2º DAM. El modelo guarda sus objetos, el presentador traduce y decide, y la vista pinta e interactúa con el usuario sin conocer el modelo.
