---
antetitulo: SpriteAnimation · Material para segundo de DAM
lema: Cada pieza en su sitio, // y un solo sitio que las une.
subtitulo: Cómo se organiza un programa en Java, explicado con la demo de animación de sprites.
tipo: Documento explicativo
version: Versión 0.1 · 31/10/2026
estado: Borrador · Material didáctico
portada: oscura
pie: Material de consulta de SpriteAnimation para segundo de DAM.
destino: proyecto
herramientas: buscador lectura
---
# MVP, interfaces e inyección de dependencias

## Por qué | Por qué hace falta una estructura {#estructura}

Ya sabes crear clases, y no escribirías un programa entero en una sola. Pero cada programa nuevo te obliga a decidir qué clases hacen falta, qué hace cada una y cómo se hablan entre ellas. Ese diseño lo haces desde cero cada vez: gasta tiempo y casi nunca sale tan bien como algo ya pensado y probado.

El ejemplo de este documento es SpriteAnimation, una ventana con un pato y una explosión animados. Arriba, un pequeño HUD (las cifras que flotan sobre la escena) muestra tres velocidades que se cambian con la rueda del ratón, y los sprites se arrastran sin dejar de animarse. Es pequeño, pero tiene todo lo que complica un programa de verdad: estado que cambia, dibujo, ratón y varios hilos.

Sin un patrón, cada programa es una carretera de curvas. En cada curva tienes que pararte a decidir algo, y la respuesta es distinta cada vez:

<figure class="vertical">
<svg viewBox="0 0 560 520" role="img" style="font-family:var(--fuente-texto);display:block">
<title>Sin un patrón, el diseño es una carretera de curvas: en cada curva hay que decidir qué clases hacen falta, quién pinta y quién guarda el estado, cómo se avisan y quién crea a quién</title>
<text x="300" y="24" font-size="15" style="fill:var(--texto-suave)">programa nuevo</text>
<path d="M280 20 C 80 60, 80 120, 280 140 S 480 220, 280 260 S 80 340, 280 380 S 480 440, 280 470" fill="none" style="stroke:var(--texto-suave)" stroke-width="10" stroke-linecap="round" opacity=".35"/>
<path d="M280 20 C 80 60, 80 120, 280 140 S 480 220, 280 260 S 80 340, 280 380 S 480 440, 280 470" fill="none" style="stroke:var(--texto-suave)" stroke-width="2"/>
<g style="fill:var(--oscuro)"><circle cx="130" cy="88" r="7"/><circle cx="430" cy="193" r="7"/><circle cx="130" cy="320" r="7"/><circle cx="430" cy="429" r="7"/></g>
<g font-size="15" style="fill:var(--oscuro)">
<text x="152" y="93">¿Qué clases hacen falta?</text>
<text x="410" y="198" text-anchor="end">¿Quién pinta? ¿Quién guarda el estado?</text>
<text x="152" y="325">¿Cómo se avisan entre ellas?</text>
<text x="410" y="434" text-anchor="end">¿Quién crea a quién?</text>
</g>
<text x="280" y="505" font-size="15" text-anchor="middle" style="fill:var(--texto-suave)">una estructura nueva, sin probar</text>
</svg>
<figcaption>Las mismas cuatro preguntas en cada programa, y cada vez una respuesta que nadie ha probado antes.</figcaption>
</figure>

Un patrón de arquitectura como MVP es una autopista: esas preguntas ya tienen respuesta, pensada y probada en muchísimos programas antes que el tuyo. Tú solo decides lo propio de tu programa, como qué es un sprite o cómo se recorta una hoja. Así quedan las respuestas en la demo:

<figure class="vertical">
<svg viewBox="0 0 560 470" role="img" style="font-family:var(--fuente-texto);display:block">
<title>Con MVP el diseño es una autopista recta: cada pregunta ya tiene respuesta, y en la demo da estas clases</title>
<rect x="40" y="10" width="52" height="450" rx="6" style="fill:var(--primario)" opacity=".15"/>
<line x1="66" y1="20" x2="66" y2="450" style="stroke:var(--primario)" stroke-width="3" stroke-dasharray="14 10"/>
<g style="stroke:var(--primario)" stroke-width="2"><line x1="92" y1="62" x2="130" y2="62"/><line x1="92" y1="172" x2="130" y2="172"/><line x1="92" y1="282" x2="130" y2="282"/><line x1="92" y1="392" x2="130" y2="392"/></g>
<g fill="none" style="stroke:var(--primario)" stroke-width="2"><rect x="130" y="20" width="390" height="86" rx="10"/><rect x="130" y="130" width="390" height="86" rx="10"/><rect x="130" y="240" width="390" height="86" rx="10"/><rect x="130" y="350" width="390" height="86" rx="10"/></g>
<g font-size="15" style="fill:var(--texto-suave)">
<text x="148" y="44">¿Qué clases hacen falta?</text>
<text x="148" y="154">¿Quién pinta? ¿Quién guarda el estado?</text>
<text x="148" y="264">¿Cómo se avisan entre ellas?</text>
<text x="148" y="374">¿Quién crea a quién?</text>
</g>
<g font-size="18" font-weight="700" style="fill:var(--primario)">
<text x="148" y="70">Model, View y Presenter</text>
<text x="148" y="180">View pinta y Model guarda</text>
<text x="148" y="290">Por un contrato con tres interfaces</text>
<text x="148" y="400">main crea, Presenter conecta</text>
</g>
<g font-size="15" style="fill:var(--texto-suave)">
<text x="148" y="94">y las propias: Sprite, SpriteSheet, Viewer</text>
<text x="148" y="204">Presenter aplica las reglas entre los dos</text>
<text x="148" y="314">AnimationContract: Model, View y Presenter</text>
<text x="148" y="424">y Presenter.start() lo pone en marcha</text>
</g>
</svg>
<figcaption>Con MVP no te paras en ninguna curva: cada pregunta trae su respuesta, y en la demo da estas clases.</figcaption>
</figure>

La estructura, además, se nota después. Otra hoja de sprites solo afecta a `SpriteSheet`, y otra ventana solo a la vista. También se puede probar el presentador sin abrir ninguna ventana, como verás en «Para practicar».

## MVP | Las tres piezas de MVP {#mvp}

MVP significa modelo, vista y presentador (en inglés, *Model-View-Presenter*) [[R1]](#src-R1 "Wikipedia, Modelo–vista–presentador"){: .ref}. Cada pieza tiene un papel, y lo que define el patrón es tanto lo que cada una sabe como lo que no sabe.

El **modelo** es el estado del programa: en la demo, los sprites con su fotograma actual, su velocidad y su posición. No sabe que existe una ventana. La **vista** es lo que el usuario ve y toca: la ventana, el pintado y el ratón. No toma decisiones; solo pinta lo que le dan y avisa de lo que hace el usuario. El **presentador** está en medio: traduce el modelo a lo que la vista necesita y convierte los gestos del usuario en cambios del modelo.

::: tarjetas 3

### Model

Sabe
:   El estado: qué fotograma toca, a qué velocidad y dónde está cada sprite. También crea sus sprites a partir de cada hoja.

No sabe
:   Que existe una vista. Al presentador, como mucho, lo conoce por su interfaz, y en la demo ni eso.

### View

Sabe
:   Pintar y escuchar el ratón.

No sabe
:   Qué es un `Sprite`, ni qué reglas tiene el programa (por ejemplo, la velocidad máxima).

### Presenter

Sabe
:   Qué datos necesita la vista, qué reglas se aplican a cada gesto y cómo se pone en marcha la aplicación.

No sabe
:   Pintar. No usa `Graphics` ni nada de Swing.

:::

En la demo, la vista toma la iniciativa para pintar: su hilo le pide al presentador, en cada vuelta, lo que tiene que dibujar. El presentador lo lee del modelo y se lo devuelve como una lista de `SpriteState`, una foto fija de cada sprite. Así se ve una vuelta de pintado:

<figure class="vertical">
<svg viewBox="0 0 560 440" role="img" style="font-family:var(--fuente-texto);display:block">
<title>Animación: la vista pide qué pintar, el presentador lee el modelo y devuelve una lista de SpriteState, y la vista pinta</title>
<style>
.a1-ida{animation:a1-ida 6s ease-in-out infinite}
.a1-vuelta{animation:a1-vuelta 6s ease-in-out infinite}
.a1-pinta{animation:a1-pinta 6s ease-in-out infinite}
@keyframes a1-ida{0%{transform:translateY(0);opacity:1}30%{transform:translateY(250px);opacity:1}34%,100%{transform:translateY(250px);opacity:0}}
@keyframes a1-vuelta{0%,38%{transform:translateY(0);opacity:0}40%{transform:translateY(0);opacity:1}74%{transform:translateY(-250px);opacity:1}78%,100%{transform:translateY(-250px);opacity:0}}
@keyframes a1-pinta{0%,76%{opacity:0}80%,94%{opacity:1}100%{opacity:0}}
@media (prefers-reduced-motion:reduce){.a1-ida,.a1-vuelta,.a1-pinta{animation:none}}
@media print{.a1-ida,.a1-vuelta,.a1-pinta{animation:none}}
</style>
<rect x="40" y="20" width="480" height="70" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="60" y="50" font-size="18" font-weight="700" style="fill:var(--primario)">View</text>
<text x="60" y="74" font-size="15" style="fill:var(--texto-suave)">su hilo pide qué pintar</text>
<text class="a1-pinta" x="500" y="62" font-size="18" font-weight="700" text-anchor="end" style="fill:var(--primario)">pinta</text>
<rect x="40" y="185" width="480" height="70" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="60" y="215" font-size="18" font-weight="700" style="fill:var(--primario)">Presenter</text>
<text x="60" y="239" font-size="15" style="fill:var(--texto-suave)">traduce cada Sprite a un SpriteState</text>
<rect x="40" y="350" width="480" height="70" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="60" y="380" font-size="18" font-weight="700" style="fill:var(--primario)">Model</text>
<text x="60" y="404" font-size="15" style="fill:var(--texto-suave)">Sprite pato · Sprite explosión</text>
<g style="stroke:var(--linea)" stroke-width="2"><line x1="110" y1="92" x2="110" y2="183"/><line x1="110" y1="257" x2="110" y2="348"/><line x1="450" y1="92" x2="450" y2="183"/><line x1="450" y1="257" x2="450" y2="348"/></g>
<text x="122" y="142" font-size="15" style="fill:var(--texto-suave)">sprites()</text>
<text x="122" y="307" font-size="15" style="fill:var(--texto-suave)">lee cada Sprite</text>
<text x="438" y="142" font-size="15" text-anchor="end" style="fill:var(--texto-suave)">List&lt;SpriteState&gt;</text>
<text x="438" y="307" font-size="15" text-anchor="end" style="fill:var(--texto-suave)">fotograma, posición</text>
<circle class="a1-ida" cx="110" cy="96" r="9" style="fill:var(--primario)"/>
<rect class="a1-vuelta" x="438" y="330" width="24" height="18" rx="3" opacity="0" style="fill:var(--oscuro)"/>
</svg>
<figcaption>Una vuelta del hilo de pintado: baja la petición por la izquierda y sube la respuesta por la derecha. La vista nunca toca el modelo.</figcaption>
</figure>

Fíjate en que la flecha que baja la mueve siempre la vista. Nadie empuja datos hacia ella: los pide cuando le toca pintar. Esa decisión simplifica el resto, porque el modelo puede cambiar tantas veces como quiera sin avisar a nadie.

El presentador hace de intermediario: la vista y el modelo no se hablan nunca, y todo lo que uno necesita del otro pasa por él. Es la idea del patrón Mediator [[R2]](#src-R2 "Refactoring.Guru, Mediator"){: .ref}, que quita dependencias entre objetos obligándolos a colaborar a través de uno solo.

## Interfaces | El contrato: las interfaces {#interfaces}

En MVP, las piezas no se conocen por su clase, sino por una interfaz: una promesa de qué se pueden pedir, sin decir cómo se hace. Si una pieza conociera la clase de otra, dependería de cómo está hecha por dentro, y cada cambio en esa clase la obligaría a cambiar también. Con interfaces, cada pieza se puede cambiar o sustituir sin tocar las demás, se puede programar por separado, y el compilador no deja pedir nada que no esté prometido.

Hay tres promesas: lo que la vista puede pedirle al presentador, y lo que el presentador puede pedirles a la vista y al modelo. Si hace falta, el modelo también puede pedirle algo al presentador, como avisarle de que sus datos han cambiado: para eso recibe la interfaz del presentador, igual que la vista.

Las tres promesas juntas forman el **contrato** de la aplicación:

<figure class="vertical">
<svg viewBox="0 0 560 480" role="img" style="font-family:var(--fuente-texto);display:block">
<title>El contrato de MVP: la vista usa la interfaz del presentador, y el presentador usa las interfaces de la vista y del modelo; si hace falta, el modelo usa la del presentador para avisar de cambios</title>
<defs><marker id="ctr-f" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto"><path d="M0 0 L10 5 L0 10 z" style="fill:var(--primario)"/></marker></defs>
<rect x="40" y="20" width="480" height="60" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/><text x="60" y="47" font-size="18" font-weight="700" style="fill:var(--primario)">Vista</text><text x="60" y="69" font-size="15" style="fill:var(--texto-suave)">pinta y escucha al usuario</text>
<rect x="40" y="210" width="480" height="60" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/><text x="60" y="237" font-size="18" font-weight="700" style="fill:var(--primario)">Presentador</text><text x="60" y="259" font-size="15" style="fill:var(--texto-suave)">aplica las reglas y traduce los datos</text>
<rect x="40" y="400" width="480" height="60" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/><text x="60" y="427" font-size="18" font-weight="700" style="fill:var(--primario)">Modelo</text><text x="60" y="449" font-size="15" style="fill:var(--texto-suave)">guarda el estado</text>
<line x1="110" y1="82" x2="110" y2="206" style="stroke:var(--primario)" stroke-width="2.2" marker-end="url(#ctr-f)"/>
<rect x="44.0" y="117" width="132" height="30" rx="7" style="fill:var(--fondo);stroke:var(--oscuro)" stroke-width="2.5"/><text x="110" y="137" font-size="15" font-weight="700" text-anchor="middle" style="fill:var(--oscuro)">«Presentador»</text>
<text x="186" y="128" font-size="15" text-anchor="start" style="fill:var(--texto-suave)">qué pintar, gestos del usuario</text>
<line x1="450" y1="208" x2="450" y2="84" style="stroke:var(--primario)" stroke-width="2.2" marker-end="url(#ctr-f)"/>
<rect x="410.0" y="143" width="80" height="30" rx="7" style="fill:var(--fondo);stroke:var(--oscuro)" stroke-width="2.5"/><text x="450" y="163" font-size="15" font-weight="700" text-anchor="middle" style="fill:var(--oscuro)">«Vista»</text>
<text x="400" y="164" font-size="15" text-anchor="end" style="fill:var(--texto-suave)">arrancar, ritmo de pintado</text>
<line x1="110" y1="272" x2="110" y2="396" style="stroke:var(--primario)" stroke-width="2.2" marker-end="url(#ctr-f)"/>
<rect x="62.0" y="299" width="96" height="30" rx="7" style="fill:var(--fondo);stroke:var(--oscuro)" stroke-width="2.5"/><text x="110" y="319" font-size="15" font-weight="700" text-anchor="middle" style="fill:var(--oscuro)">«Modelo»</text>
<text x="170" y="319" font-size="15" text-anchor="start" style="fill:var(--texto-suave)">arrancar, dar sus datos</text>
<line x1="450" y1="398" x2="450" y2="274" style="stroke:var(--primario)" stroke-width="2.2" marker-end="url(#ctr-f)" stroke-dasharray="6 5"/>
<rect x="384.0" y="345" width="132" height="30" rx="7" style="fill:var(--fondo);stroke:var(--oscuro)" stroke-width="2.5" stroke-dasharray="5 4"/><text x="450" y="365" font-size="15" font-weight="700" text-anchor="middle" style="fill:var(--oscuro)">«Presentador»</text>
<text x="376" y="365" font-size="15" text-anchor="end" style="fill:var(--texto-suave)">si hace falta: avisar de cambios</text>
</svg>
<figcaption>Cada flecha va de quien pide a quien responde, y pasa por la interfaz que lo permite. En discontinuo, lo opcional: el modelo solo le pide algo al presentador si tiene que avisarle de cambios. Las tres interfaces, juntas, son el contrato.</figcaption>
</figure>

En la demo, el modelo no pide nada: la vista pregunta al presentador cada vez que pinta, así que nadie tiene que avisarla. Por eso `AnimationContract.Model` no recibe al presentador. El contrato es un solo archivo, `AnimationContract`, con las tres interfaces dentro. Son cortas a propósito: cada método es algo que la otra parte necesita de verdad.

### `AnimationContract.Model`: lo que el presentador puede pedirle al modelo

::: tarjetas 2

#### `void start()`

Poner en marcha las animaciones.

#### `void stop()`

Pararlas cuando se cierra la aplicación.

#### `int spriteCount()`

Decir cuántos sprites hay.

#### `Sprite sprite(int index)`

Dar uno de los sprites, por su número.

:::

### `AnimationContract.View`: lo que el presentador puede pedirle a la vista

::: tarjetas 2

#### `void setPresenter(Presenter presenter)`

Guardar a qué presentador tiene que pedirle qué pintar.

#### `void start()`

Empezar a pintar.

#### `void stop()`

Dejar de pintar.

#### `int renderFps()`

Decir cuántas veces por segundo pinta.

#### `void setRenderFps(int fps)`

Cambiar ese ritmo.

:::

### `AnimationContract.Presenter`: lo que la vista puede pedirle al presentador

::: tarjetas 2

#### `record SpriteState(…)`

No es un método: es la foto fija de un sprite para pintarlo, con su nombre, su fotograma, su posición y su velocidad. Nadie la cambia mientras se pinta.

#### `List<SpriteState> sprites()`

Dar la foto de todos los sprites, para pintarlos.

#### `void changeRenderSpeed(int delta)`

Subir o bajar el ritmo de pintado, sin salirse de los límites.

#### `void changeSpriteSpeed(int sprite, int delta)`

Lo mismo con la velocidad de un sprite.

#### `int spriteAt(int x, int y)`

Decir qué sprite hay en un punto de la ventana.

#### `void moveSprite(int sprite, int x, int y)`

Llevar un sprite a otro sitio.

#### `void start()`

Poner en marcha la aplicación. Este no lo pide la vista: lo llama `main`.

#### `void close()`

Pararlo todo cuando se cierra la ventana.

:::

En Java, el contrato se escribe así, y cada clase implementa su parte:

```java
public interface AnimationContract {
    interface Model { … }
    interface View { … }
    interface Presenter { … }
}

public class Model implements AnimationContract.Model { … }
public class View implements AnimationContract.View { … }
public class Presenter implements AnimationContract.Presenter { … }
```

A `AnimationContract.Model` le falta algo a propósito: `addSprite`. Decidir qué animaciones hay es cosa de `main`, así que ese método es de la clase `Model`, pero no del contrato. Sacar a una interfaz solo la parte de una clase que otra necesita es una técnica de refactorización con nombre propio: *extraer interfaz* [[R3]](#src-R3 "Refactoring.Guru, Extract Interface"){: .ref}.

Gracias al contrato, el presentador funciona igual con cualquier vista que lo cumpla: la de Swing, una de texto o una falsa para probar sin ventana.

<figure class="vertical">
<svg viewBox="0 0 560 400" role="img" style="font-family:var(--fuente-texto);display:block">
<title>El presentador usa la interfaz AnimationContract.View, que pueden implementar la vista de Swing, una vista de texto o una vista falsa para pruebas</title>
<defs><marker id="m3" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="8" markerHeight="8" orient="auto"><path d="M0 0 L10 5 L0 10 z" style="fill:var(--texto-suave)"/></marker></defs>
<rect x="160" y="16" width="240" height="60" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="280" y="44" font-size="18" font-weight="700" text-anchor="middle" style="fill:var(--primario)">Presenter</text>
<text x="280" y="66" font-size="15" text-anchor="middle" style="fill:var(--texto-suave)">solo conoce el contrato</text>
<line x1="280" y1="78" x2="280" y2="134" style="stroke:var(--texto-suave)" stroke-width="2" marker-end="url(#m3)"/>
<text x="292" y="112" font-size="15" style="fill:var(--texto-suave)">usa</text>
<rect x="130" y="138" width="300" height="66" rx="10" fill="none" style="stroke:var(--oscuro)" stroke-width="3"/>
<text x="280" y="164" font-size="15" text-anchor="middle" style="fill:var(--texto-suave)">«interface»</text>
<text x="280" y="190" font-size="18" font-weight="700" text-anchor="middle" style="fill:var(--oscuro)">AnimationContract.View</text>
<g style="stroke:var(--texto-suave)" stroke-width="2"><line x1="110" y1="290" x2="200" y2="208" marker-end="url(#m3)"/><line x1="280" y1="290" x2="280" y2="208" marker-end="url(#m3)"/><line x1="450" y1="290" x2="360" y2="208" marker-end="url(#m3)"/></g>
<text x="300" y="256" font-size="15" style="fill:var(--texto-suave)">implementan</text>
<rect x="20" y="292" width="170" height="80" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="105" y="322" font-size="18" font-weight="700" text-anchor="middle" style="fill:var(--primario)">View</text>
<text x="105" y="346" font-size="15" text-anchor="middle" style="fill:var(--texto-suave)">Swing y Canvas</text>
<text x="105" y="364" font-size="15" text-anchor="middle" style="fill:var(--texto-suave)">la de la demo</text>
<rect x="195" y="292" width="170" height="80" rx="10" fill="none" style="stroke:var(--texto-suave)" stroke-width="2" stroke-dasharray="6 4"/>
<text x="280" y="322" font-size="18" font-weight="700" text-anchor="middle" style="fill:var(--texto-suave)">TextView</text>
<text x="280" y="346" font-size="15" text-anchor="middle" style="fill:var(--texto-suave)">escribe en consola</text>
<text x="280" y="364" font-size="15" text-anchor="middle" style="fill:var(--texto-suave)">ejercicio</text>
<rect x="370" y="292" width="170" height="80" rx="10" fill="none" style="stroke:var(--texto-suave)" stroke-width="2" stroke-dasharray="6 4"/>
<text x="455" y="322" font-size="18" font-weight="700" text-anchor="middle" style="fill:var(--texto-suave)">FakeView</text>
<text x="455" y="346" font-size="15" text-anchor="middle" style="fill:var(--texto-suave)">para probar</text>
<text x="455" y="364" font-size="15" text-anchor="middle" style="fill:var(--texto-suave)">ejercicio</text>
</svg>
<figcaption>En trazo continuo, la vista que trae la demo; en discontinuo, las que puedes escribir tú sin tocar el presentador.</figcaption>
</figure>

## Inyección | Quién crea a quién: la inyección de dependencias {#inyeccion}

Una clase *depende* de otra cuando la necesita para trabajar. Puede conseguirla de dos formas: crearla ella misma con `new`, o recibirla de fuera. La **inyección de dependencias** es la segunda [[R4]](#src-R4 "Wikipedia, Inyección de dependencias"){: .ref}: cada clase recibe por su constructor lo que necesita, y no lo crea ella. Así no queda atada a una clase concreta, y se le puede pasar cualquier otra que cumpla la misma interfaz, por ejemplo una falsa para probar.

En la demo, si el presentador hiciera `new View()` dentro de su constructor, quedaría atado para siempre a esa vista, y no se podría probar con una vista falsa. Por eso recibe el modelo y la vista:

```java
public class Presenter implements AnimationContract.Presenter {

    private final AnimationContract.Model model;
    private final AnimationContract.View view;

    public Presenter(AnimationContract.Model model, AnimationContract.View view) {   // las recibe, no las crea
        this.model = model;
        this.view = view;
        view.setPresenter(this);   // se conecta con la vista: lo verás en «Cómo se monta»
    }
    // …
}
```

Fíjate en el tipo de los dos parámetros: son interfaces, no clases. Eso se llama **programar contra interfaces**. El presentador solo ve los métodos del contrato: aunque reciba un `Model` de verdad, no puede llamar a `addSprite`, porque el compilador no le deja. Así, que solo `main` decida qué animaciones hay no es una norma que haya que recordar: lo garantiza el compilador.

No todo se inyecta. Lo que es parte interna de una pieza lo crea ella: la vista crea su lienzo (`Viewer`) y el modelo, sus sprites. Se inyecta lo que conecta capas o lo que querrías sustituir.

## Montaje | Cómo se monta la aplicación {#montaje}

Si cada clase recibe lo que necesita y no lo crea, alguien tiene que crear las piezas. En MVP ese sitio es uno solo: el punto de entrada del programa, el único que hace `new` de las capas. La receta es siempre la misma y tiene dos fases: construir y conectar, todo en un solo hilo; y arrancar, cuando nacen los demás hilos. Así se monta, en tres pasos:

<figure class="vertical">
<style>
figure ol.vin{--vin-main:#2c6e9b;--vin-start:#b23a2a;list-style:none;margin:0 auto;padding:0;max-width:560px;display:grid;gap:.75rem}
figure ol.vin>li{margin:0;border:1.5px solid var(--borde);border-radius:10px;padding:.75rem .9rem .8rem}
figure ol.vin>li::before{content:none}
.vin-cab{display:flex;gap:.65rem;align-items:center}
.vin-num{flex:none;width:1.7rem;height:1.7rem;border-radius:50%;background:var(--vin-main);color:#fff;font-weight:700;font-size:.95rem;display:grid;place-items:center}
.vin-fase{display:block;font-size:.78rem;color:var(--vin-main);font-weight:650;text-transform:uppercase;letter-spacing:.04em;line-height:1.3}
.vin-tit{display:block;font-weight:700;line-height:1.3}
.vin-start .vin-num{background:var(--vin-start)}.vin-start .vin-fase{color:var(--vin-start)}
figure ol.vin ol.vin-acc{margin:.55rem 0 .7rem 2.35rem;padding:0;list-style:none;display:grid;gap:.2rem}
figure ol.vin ol.vin-acc>li{margin:0;padding:0 0 0 1.1rem;position:relative;line-height:1.45}
figure ol.vin ol.vin-acc>li::before{content:"→";position:absolute;left:0;top:0;color:var(--vin-main);font-weight:700;background:none;width:auto;height:auto}
.vin-start ol.vin-acc>li::before{color:var(--vin-start)!important}
.vin svg{display:block;width:100%;max-width:500px;height:auto;margin:0}
@media (max-width:480px){figure ol.vin ol.vin-acc{margin-left:0}}
.vin.en-marcha>li{animation:vin-paso 2s ease-in-out both}
.vin.en-marcha>li:nth-child(2){animation-delay:2s}
.vin.en-marcha>li:nth-child(3){animation-name:vin-ultimo;animation-delay:4s}
@keyframes vin-paso{0%,100%{border-color:var(--borde);box-shadow:none}15%,85%{border-color:var(--primario);box-shadow:0 0 0 1.5px var(--primario)}}
@keyframes vin-ultimo{0%{border-color:var(--borde);box-shadow:none}15%,100%{border-color:var(--primario);box-shadow:0 0 0 1.5px var(--primario)}}
@media (prefers-reduced-motion:reduce){.vin.en-marcha>li{animation:none}}
@media print{.vin.en-marcha>li{animation:none}}
</style>
<svg width="0" height="0" style="position:absolute;width:0;height:0;overflow:hidden" aria-hidden="true"><defs><marker id="vin-fv" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0 0 L10 5 L0 10 z" style="fill:var(--texto-suave)"/></marker><marker id="vin-fn" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0 0 L10 5 L0 10 z" style="fill:var(--primario)"/></marker></defs></svg>
<ol class="vin">
<li><div class="vin-cab"><span class="vin-num">1</span><div><span class="vin-fase">Construir y conectar</span><span class="vin-tit">Preparar el modelo y la vista</span></div></div>
<ol class="vin-acc"><li>Se crea el modelo, todavía vacío.</li><li>Después se configura: se le dan sus datos (aquí, las animaciones).</li><li>Luego se crea la vista. Aún no tiene presentador: no sabe a quién pedir qué pintar.</li></ol>
<svg viewBox="0 0 500 42" role="img" style="font-family:var(--fuente-texto)"><title>Nuevos: el modelo con sus datos y la vista, todavía sin presentador</title><rect x="2" y="4" width="162" height="34" rx="6" style="fill:var(--fondo-medio);stroke:var(--primario)" stroke-width="2.5"/><text x="83.0" y="26" font-size="15" text-anchor="middle" font-weight="700" style="fill:var(--primario)">datos del modelo</text><rect x="190" y="4" width="86" height="34" rx="6" style="fill:var(--fondo-medio);stroke:var(--primario)" stroke-width="2.5"/><text x="233.0" y="26" font-size="15" text-anchor="middle" font-weight="700" style="fill:var(--primario)">Modelo</text><rect x="302" y="4" width="110" height="34" rx="6" fill="none" style="stroke:var(--tenue)" stroke-width="1.2" stroke-dasharray="4 3"/><text x="357.0" y="26" font-size="15" text-anchor="middle" style="fill:var(--texto-tenue)">Presentador</text><rect x="436" y="4" width="62" height="34" rx="6" style="fill:var(--fondo-medio);stroke:var(--primario)" stroke-width="2.5"/><text x="467.0" y="26" font-size="15" text-anchor="middle" font-weight="700" style="fill:var(--primario)">Vista</text><line x1="188" y1="21" x2="166" y2="21" style="stroke:var(--primario)" stroke-width="2.2" marker-end="url(#vin-fn)"/></svg></li>
<li><div class="vin-cab"><span class="vin-num">2</span><div><span class="vin-fase">Construir y conectar</span><span class="vin-tit">Construir el presentador</span></div></div>
<ol class="vin-acc"><li>Se crea el presentador, que recibe el modelo y la vista.</li><li>Los guarda para usarlos después.</li><li>Se presenta a la vista, que lo guarda: se cierra el ciclo.</li></ol>
<svg viewBox="0 0 500 42" role="img" style="font-family:var(--fuente-texto)"><title>Nuevo: el presentador, que apunta al modelo y a la vista, y la vista, que apunta al presentador</title><rect x="2" y="4" width="162" height="34" rx="6" fill="none" style="stroke:var(--texto-suave)" stroke-width="1.5"/><text x="83.0" y="26" font-size="15" text-anchor="middle" style="fill:var(--texto)">datos del modelo</text><rect x="190" y="4" width="86" height="34" rx="6" fill="none" style="stroke:var(--texto-suave)" stroke-width="1.5"/><text x="233.0" y="26" font-size="15" text-anchor="middle" style="fill:var(--texto)">Modelo</text><rect x="302" y="4" width="110" height="34" rx="6" style="fill:var(--fondo-medio);stroke:var(--primario)" stroke-width="2.5"/><text x="357.0" y="26" font-size="15" text-anchor="middle" font-weight="700" style="fill:var(--primario)">Presentador</text><rect x="436" y="4" width="62" height="34" rx="6" fill="none" style="stroke:var(--texto-suave)" stroke-width="1.5"/><text x="467.0" y="26" font-size="15" text-anchor="middle" style="fill:var(--texto)">Vista</text><line x1="188" y1="21" x2="166" y2="21" style="stroke:var(--texto-suave)" stroke-width="1.5" marker-end="url(#vin-fv)"/><line x1="300" y1="21" x2="278" y2="21" style="stroke:var(--primario)" stroke-width="2.2" marker-end="url(#vin-fn)"/><line x1="413" y1="15" x2="434" y2="15" style="stroke:var(--primario)" stroke-width="2.2" marker-end="url(#vin-fn)"/><line x1="434" y1="27" x2="413" y2="27" style="stroke:var(--primario)" stroke-width="2.2" marker-end="url(#vin-fn)"/></svg></li>
<li class="vin-start"><div class="vin-cab"><span class="vin-num">3</span><div><span class="vin-fase">Arrancar</span><span class="vin-tit">Arrancar todo</span></div></div>
<ol class="vin-acc"><li>Se pone en marcha el modelo (aquí, un hilo por animación).</li><li>Se pone en marcha la vista, con su hilo de pintado.</li><li>El programa principal termina; la aplicación sigue en los hilos nuevos.</li></ol>
<svg viewBox="0 0 500 42" role="img" style="font-family:var(--fuente-texto)"><title>Los datos del modelo y la vista, con sus hilos en marcha</title><rect x="2" y="4" width="162" height="34" rx="6" fill="none" style="stroke:var(--acento)" stroke-width="2.5"/><text x="83.0" y="26" font-size="14" text-anchor="middle" font-weight="700" style="fill:var(--acento)">▶ datos del modelo</text><rect x="190" y="4" width="86" height="34" rx="6" fill="none" style="stroke:var(--texto-suave)" stroke-width="1.5"/><text x="233.0" y="26" font-size="15" text-anchor="middle" style="fill:var(--texto)">Modelo</text><rect x="302" y="4" width="110" height="34" rx="6" fill="none" style="stroke:var(--texto-suave)" stroke-width="1.5"/><text x="357.0" y="26" font-size="15" text-anchor="middle" style="fill:var(--texto)">Presentador</text><rect x="436" y="4" width="62" height="34" rx="6" fill="none" style="stroke:var(--acento)" stroke-width="2.5"/><text x="467.0" y="26" font-size="14" text-anchor="middle" font-weight="700" style="fill:var(--acento)">▶ Vista</text><line x1="188" y1="21" x2="166" y2="21" style="stroke:var(--texto-suave)" stroke-width="1.5" marker-end="url(#vin-fv)"/><line x1="300" y1="21" x2="278" y2="21" style="stroke:var(--texto-suave)" stroke-width="1.5" marker-end="url(#vin-fv)"/><line x1="413" y1="15" x2="434" y2="15" style="stroke:var(--texto-suave)" stroke-width="1.5" marker-end="url(#vin-fv)"/><line x1="434" y1="27" x2="413" y2="27" style="stroke:var(--texto-suave)" stroke-width="1.5" marker-end="url(#vin-fv)"/></svg></li>
</ol>
<script>(function(){var l=document.querySelector('.vin');if(!l)return;function mira(){var r=l.getBoundingClientRect();if(r.height>0&&r.top<innerHeight*.7&&r.bottom>0){l.classList.add('en-marcha');removeEventListener('scroll',mira);removeEventListener('resize',mira);document.removeEventListener('toggle',mira,true);}}addEventListener('scroll',mira,{passive:true});addEventListener('resize',mira);document.addEventListener('toggle',mira,true);mira();})();</script>
<figcaption>La receta de montaje de cualquier MVP, en el orden en que se ejecuta. Debajo de cada paso, las piezas tal como quedan al terminarlo. El color del número marca la fase: en azul, construir y conectar (1 y 2); en terracota, arrancar (3). Hasta el paso 3 solo trabaja un hilo, el del programa principal. Lo nuevo de cada paso va resaltado y lo discontinuo aún no existe. Las flechas son lo que cada pieza guarda; la doble entre el presentador y la vista es el ciclo que cierra el paso 2. En dorado, lo que tiene un hilo en marcha.</figcaption>
</figure>

En la demo, el punto de entrada es el método `main` de `SpriteAnimation`, y cada línea es una acción de las viñetas: crea el modelo, lo configura con sus animaciones, crea la vista y el presentador, y lo arranca todo.

```java
public static void main(String[] args) {
    Model model = new Model();
    // nombre, hoja, ancho y alto del fotograma, escala, fotogramas por segundo, x, y
    model.addSprite("Pato", "/sprites/pato.png", 16, 16, 4, 8, 120, 220);
    model.addSprite("Explosión", "/sprites/explosion.png", 48, 48, 3, 12, 360, 160);
    AnimationContract.View view = new View();
    AnimationContract.Presenter presenter = new Presenter(model, view);
    presenter.start();
}
```

`main` declara el modelo como `Model` porque necesita `addSprite`, que crea cada sprite a partir de su hoja. El presentador recibe ese mismo objeto como `AnimationContract.Model`, sin `addSprite`.

El presentador hace de intermediario entre las capas [[R2]](#src-R2 "Refactoring.Guru, Mediator"){: .ref}, y en el montaje separa las dos fases. Su constructor, que ya has visto, guarda el modelo y la vista y se presenta a la vista: los conecta. Su método `start()` solo arranca:

```java
@Override
public void start() {          // solo arranca
    model.start();
    view.start();
}
```

Fíjate en que el presentador no sabe qué animaciones hay: trabaja con los sprites que tenga el modelo. Por eso sirve igual para dos sprites que para veinte, y cambiar las animaciones no lo toca.

El paso 2 de las viñetas merece una explicación. El presentador necesita la vista y la vista necesita el presentador, así que no pueden recibirse los dos por constructor: uno de los dos tiene que existir primero. La demo crea la vista, se la pasa al presentador, y el constructor del presentador, en su última línea, se presenta a la vista con `view.setPresenter(this)`. Es la única dependencia que no va por constructor.

Pasar `this` desde un constructor se llama *dejar escapar* `this`, y suele ser mala práctica: otro hilo podría usar el objeto antes de que esté construido. Aquí no hay problema: es la última línea del constructor y todavía solo existe el hilo de `main`.

Por eso arrancar va aparte, en el paso 3. Los hilos solo nacen cuando todo está construido y conectado: si el hilo de pintado empezara antes del paso 2, le pediría qué pintar a un presentador que la vista aún no tiene.

## Hilos | MVP con hilos {#hilos}

Una aplicación con ventana suele tener varios hilos a la vez: uno atiende al usuario, otro pinta y otros trabajan de fondo. Si dos hilos tocan los mismos datos, pueden pisarse. MVP ayuda porque cada hilo vive en una capa. El hilo de pintado está en la vista y solo habla con el presentador; los hilos de fondo están en el modelo y no conocen la vista. Cada frontera entre capas es también una frontera entre hilos.

En la demo trabajan tres clases de hilos, y cada uno sabe qué puede tocar y qué solo puede leer:

<figure class="vertical">
<svg viewBox="0 0 560 400" role="img" style="font-family:var(--fuente-texto);display:block">
<title>Los tres hilos de la demo y qué capa toca cada uno</title>
<defs><marker id="m5" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="8" markerHeight="8" orient="auto"><path d="M0 0 L10 5 L0 10 z" style="fill:var(--oscuro)"/></marker></defs>
<rect x="40" y="16" width="360" height="64" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="60" y="44" font-size="18" font-weight="700" style="fill:var(--primario)">Hilo de pintado · en View</text>
<text x="60" y="68" font-size="15" style="fill:var(--texto-suave)">pide sprites() y pinta: solo lee</text>
<rect x="40" y="112" width="360" height="64" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="60" y="140" font-size="18" font-weight="700" style="fill:var(--primario)">Hilo de eventos de Swing</text>
<text x="60" y="164" font-size="15" style="fill:var(--texto-suave)">rueda y arrastre: escribe fps, x, y</text>
<rect x="40" y="208" width="360" height="64" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="60" y="236" font-size="18" font-weight="700" style="fill:var(--primario)">Un hilo por sprite · en Model</text>
<text x="60" y="260" font-size="15" style="fill:var(--texto-suave)">avanza su fotograma: escribe el fotograma</text>
<g fill="none" style="stroke:var(--oscuro)" stroke-width="2"><polyline points="402,48 500,48 500,318" marker-end="url(#m5)"/><polyline points="402,144 466,144 466,318" marker-end="url(#m5)"/><polyline points="402,240 432,240 432,318" marker-end="url(#m5)"/></g>
<rect x="40" y="322" width="480" height="64" rx="10" fill="none" style="stroke:var(--oscuro)" stroke-width="3"/>
<text x="60" y="350" font-size="18" font-weight="700" style="fill:var(--oscuro)">Cada Sprite</text>
<text x="60" y="374" font-size="15" style="fill:var(--texto-suave)">frame, fps, x, y: campos volatile</text>
</svg>
<figcaption>Los tres hilos acaban en los mismos datos de cada sprite. Por eso esos campos son <code>volatile</code>.</figcaption>
</figure>

Cada `Sprite` tiene su hilo, que avanza el fotograma según el tiempo que ha pasado. Por eso su ritmo es el mismo en cualquier equipo, y no depende de lo rápido que se pinte.

Los campos que escribe un hilo y lee otro son `volatile`, para que cada hilo vea el último valor escrito. Y la vista no lee los sprites: recibe una lista de `SpriteState` que no cambia mientras pinta.

Así se cumple la idea del principio: el hilo de pintado nunca toca un `Sprite`, y los hilos de los sprites nunca tocan Swing.

## Un gesto | Un gesto, de principio a fin {#gesto}

En MVP, cualquier gesto del usuario recorre el mismo camino. La vista lo recibe y avisa al presentador. El presentador aplica las reglas y cambia el modelo. La vista se entera del cambio de una de dos formas: lo ve la próxima vez que pide qué pintar, o el modelo avisa al presentador y este, a la vista. En la demo es la primera: nadie avisa a la vista.

En la demo, sigue un gesto: giras la rueda del ratón sobre la cifra del pato en el HUD.

<figure class="vertical">
<svg viewBox="0 0 560 500" role="img" style="font-family:var(--fuente-texto);display:block">
<title>Animación: la rueda del ratón sube la velocidad del pato de 8 a 9; el gesto baja de la vista al presentador y al modelo, y el siguiente pintado muestra 9</title>
<style>
.a3-gesto{animation:a3-gesto 9s ease-in-out infinite}
.a3-vuelta{animation:a3-vuelta 9s ease-in-out infinite}
.a3-ocho{animation:a3-ocho 9s ease-in-out infinite}
.a3-nueve{animation:a3-nueve 9s ease-in-out infinite}
.a3-m8{animation:a3-m8 9s ease-in-out infinite}
.a3-m9{animation:a3-m9 9s ease-in-out infinite}
@keyframes a3-gesto{0%,4%{transform:translateY(0);opacity:0}6%{opacity:1}40%{transform:translateY(300px);opacity:1}44%,100%{transform:translateY(300px);opacity:0}}
@keyframes a3-m8{0%,40%{opacity:1}44%,100%{opacity:0}}
@keyframes a3-m9{0%,40%{opacity:0}44%,96%{opacity:1}100%{opacity:0}}
@keyframes a3-vuelta{0%,52%{transform:translateY(0);opacity:0}54%{opacity:1}82%{transform:translateY(-300px);opacity:1}85%,100%{transform:translateY(-300px);opacity:0}}
@keyframes a3-ocho{0%,84%{opacity:1}86%,100%{opacity:0}}
@keyframes a3-nueve{0%,84%{opacity:0}86%,96%{opacity:1}100%{opacity:0}}
@media (prefers-reduced-motion:reduce){[class^="a3-"]{animation:none}}
@media print{[class^="a3-"]{animation:none}}
</style>
<rect x="40" y="14" width="480" height="56" rx="10" fill="none" style="stroke:var(--oscuro)" stroke-width="2"/>
<text x="60" y="48" font-size="18" style="fill:var(--oscuro)"><tspan font-weight="700">HUD</tspan> · Pato ·</text>
<text class="a3-ocho" x="176" y="48" font-size="18" font-weight="700" opacity="0" style="fill:var(--oscuro)">8</text>
<text class="a3-nueve" x="176" y="48" font-size="18" font-weight="700" style="fill:var(--primario)">9</text>
<text x="194" y="48" font-size="18" style="fill:var(--oscuro)">fotogramas por segundo</text>
<rect x="40" y="110" width="480" height="64" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="60" y="138" font-size="18" font-weight="700" style="fill:var(--primario)">View</text>
<text x="60" y="162" font-size="15" style="fill:var(--texto-suave)">mouseWheelMoved: ¿sobre qué línea del HUD?</text>
<rect x="40" y="250" width="480" height="64" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="60" y="278" font-size="18" font-weight="700" style="fill:var(--primario)">Presenter</text>
<text x="60" y="302" font-size="15" style="fill:var(--texto-suave)">changeSpriteSpeed(0, +1): aplica el límite</text>
<rect x="40" y="390" width="480" height="64" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="60" y="418" font-size="18" font-weight="700" style="fill:var(--primario)">Model</text>
<text class="a3-m8" x="60" y="442" font-size="15" opacity="0" style="fill:var(--texto-suave)">Sprite pato · fps = 8</text>
<text class="a3-m9" x="60" y="442" font-size="15" style="fill:var(--texto-suave)">Sprite pato · setFps(9)</text>
<g style="stroke:var(--linea)" stroke-width="2"><line x1="150" y1="72" x2="150" y2="108"/><line x1="150" y1="176" x2="150" y2="248"/><line x1="150" y1="316" x2="150" y2="388"/><line x1="410" y1="72" x2="410" y2="108"/><line x1="410" y1="176" x2="410" y2="248"/><line x1="410" y1="316" x2="410" y2="388"/></g>
<text x="162" y="218" font-size="15" style="fill:var(--texto-suave)">el gesto</text>
<text x="398" y="218" font-size="15" text-anchor="end" style="fill:var(--texto-suave)">siguiente pintado</text>
<circle class="a3-gesto" cx="150" cy="80" r="9" opacity="0" style="fill:var(--oscuro)"/>
<rect class="a3-vuelta" x="398" y="380" width="24" height="18" rx="3" opacity="0" style="fill:var(--primario)"/>
</svg>
<figcaption>El gesto baja por la izquierda hasta el modelo. La vista no cambia la cifra: la ve cambiada cuando vuelve a pedir qué pintar.</figcaption>
</figure>

En código, el recorrido es corto. La vista solo averigua sobre qué línea del HUD está el ratón y avisa; el presentador aplica la regla del límite y se lo pasa al modelo con `setFps`.

```java
// View: el evento llega por el hilo de eventos de Swing
public void mouseWheelMoved(MouseWheelEvent e) {
    int line = (e.getY() - HUD_Y + HUD_LINE) / HUD_LINE;   // 0: pintado; 1, 2…: sprites
    int delta = -e.getWheelRotation();
    // … si el ratón está fuera del HUD, no hace nada
    if (line == 0) presenter.changeRenderSpeed(delta);
    else presenter.changeSpriteSpeed(line - 1, delta);
}

// Presenter: aplica la regla
public void changeSpriteSpeed(int sprite, int delta) {
    Sprite s = model.sprite(sprite);
    s.setFps(limit(s.fps() + delta));                       // entre 1 y 120
}
```

Arrastrar un sprite sigue el mismo camino. La vista pregunta al presentador qué sprite hay bajo el puntero (`spriteAt`) y, mientras arrastras, le pide que lo mueva (`moveSprite`). El pato sigue aleteando todo el rato, porque su hilo no sabe nada del ratón.

Para enterarse de la rueda, la vista usa el patrón Observer [[R5]](#src-R5 "Refactoring.Guru, Observer"){: .ref}: se suscribe a los eventos del ratón de Swing, y Swing la avisa llamando a `mouseWheelMoved`. Entre el modelo y la vista, en cambio, la demo no lo usa: el modelo no avisa a nadie, y la vista pide los datos cuando le toca pintar. En otros MVP, el modelo sí avisa de sus cambios al presentador, y también suele hacerlo con Observer.

## Errores | Errores frecuentes {#errores}

Estos son los atajos que más se repiten al empezar con MVP. Todos funcionan el primer día, y todos complican el segundo.

::: tarjetas 2

### La vista lee el modelo

Cómo se ve
:   `View` recibe el `Model` y recorre sus `Sprite` para pintarlos.

Por qué es un problema
:   La vista queda atada a las clases del modelo, y el hilo de pintado toca objetos que otros hilos están cambiando.

Cómo se arregla
:   La vista solo conoce `AnimationContract.Presenter` y pinta `SpriteState`.

### El presentador pinta

Cómo se ve
:   `Presenter` recibe un `Graphics2D` y dibuja.

Por qué es un problema
:   El presentador deja de poder probarse sin ventana, y la vista se queda sin trabajo.

Cómo se arregla
:   El presentador devuelve datos; pintar es cosa de la vista.

### El `new` escondido

Cómo se ve
:   `Presenter` hace `new View()` en su constructor.

Por qué es un problema
:   No se puede cambiar la vista por otra, ni probar con una `FakeView`.

Cómo se arregla
:   Inyección por constructor: `new Presenter(model, view)` en `main`.

### Recibir la clase en lugar de la interfaz

Cómo se ve
:   `Presenter(Model model, View view)`: los parámetros son las clases, no sus interfaces.

Por qué es un problema
:   El presentador puede llamar a cualquier método, también a `addSprite`, y ya no acepta una `FakeView` ni un modelo falso.

Cómo se arregla
:   Programar contra interfaces: `Presenter(AnimationContract.Model model, AnimationContract.View view)`.

### La interfaz que lo expone todo

Cómo se ve
:   `AnimationContract.Presenter` tiene veinte métodos, uno por cada campo del modelo.

Por qué es un problema
:   El contrato deja de proteger nada: la vista vuelve a depender de cómo es el modelo.

Cómo se arregla
:   Un método por cada cosa que la otra parte necesita de verdad, no por cada dato.

### El modelo que avisa a la vista

Cómo se ve
:   `Sprite` llama a `repaint()` cada vez que cambia de fotograma.

Por qué es un problema
:   El modelo depende de Swing, y el ritmo de pintado vuelve a atarse al de la animación.

Cómo se arregla
:   El modelo no conoce la vista. Si tiene que avisar, avisa al presentador por su interfaz; en la demo ni eso: la vista pide los datos cuando le toca pintar.


:::

## Practicar | Para practicar {#practicar}

Cada ejercicio cambia una sola cosa de la demo. Antes de empezar, intenta adivinar qué clases tendrás que tocar: si la estructura está bien hecha, son muy pocas.

::: tarjetas 2

### Una tercera animación

Qué tienes que hacer
:   Busca una hoja de sprites y añade un tercer sprite a la demo.

Qué clases cambian
:   Solo `SpriteAnimation`: una llamada más a `model.addSprite` en `main`. El presentador y la vista no cambian, y el HUD muestra la nueva línea solo.

Qué aprendes
:   Que si cada pieza tiene su papel, añadir algo es cambiar una línea en el sitio que toca.

### Una vista de texto

Qué tienes que hacer
:   Escribe una `TextView` que implemente `AnimationContract.View` y escriba en consola, una vez por segundo, el fotograma y la posición de cada sprite.

Qué clases cambian
:   Ninguna: añades `TextView` y cambias una línea de `main`.

Qué aprendes
:   Para qué sirve el contrato: el presentador no nota la diferencia.

### Una prueba sin ventana

Qué tienes que hacer
:   Escribe una `FakeView` que implemente `AnimationContract.View` sin abrir ninguna ventana, y un `FakeModel` con un sprite fijo. Con ellos, comprueba que la velocidad no baja de 1 ni sube de 120, para el pintado y para cada sprite.

Qué clases cambian
:   Ninguna: la prueba, la `FakeView` y el `FakeModel` son clases aparte.

Qué aprendes
:   Que inyectar por interfaces hace que el presentador se pueda probar solo, sin ventana y sin hojas de sprites.

### Pausar un sprite

Qué tienes que hacer
:   Con un doble clic sobre un sprite, su animación se para o vuelve a empezar.

Qué clases cambian
:   `View` (el gesto), `AnimationContract.Presenter` y `Presenter` (el método nuevo) y `Sprite` (la pausa).

Qué aprendes
:   Cómo atraviesa una funcionalidad nueva las tres capas, y cómo se para y se reanuda un hilo.

:::

## Referencias | Para saber más {#referencias}

Para ampliar lo que cuenta este documento: la Wikipedia en español para las ideas generales, y Refactoring.Guru para los patrones de diseño y las técnicas de refactorización, con dibujos y ejemplos en Java. Los enlaces se revisaron el 2 de noviembre de 2026.

::: fuentes

**[R1] Wikipedia.** «Modelo–vista–presentador». Qué es MVP y qué papel tiene cada una de sus tres piezas. [Enlace](https://es.wikipedia.org/wiki/Modelo%E2%80%93vista%E2%80%93presentador)
{: #src-R1}

**[R2] Refactoring.Guru.** «Mediator». En la demo, el presentador es el intermediario entre la vista y el modelo, que nunca se hablan directamente. [Enlace](https://refactoring.guru/es/design-patterns/mediator)
{: #src-R2}

**[R3] Refactoring.Guru.** «Extract Interface» (en inglés). Cómo se saca a una interfaz la parte de una clase que otra necesita, como `AnimationContract.Model`. [Enlace](https://refactoring.guru/es/extract-interface)
{: #src-R3}

**[R4] Wikipedia.** «Inyección de dependencias». Por qué una clase recibe lo que necesita en lugar de crearlo ella. [Enlace](https://es.wikipedia.org/wiki/Inyecci%C3%B3n_de_dependencias)
{: #src-R4}

**[R5] Refactoring.Guru.** «Observer». En la demo, la vista se suscribe a los eventos del ratón de Swing; el modelo, en cambio, no avisa a nadie. [Enlace](https://refactoring.guru/es/design-patterns/observer)
{: #src-R5}

:::
