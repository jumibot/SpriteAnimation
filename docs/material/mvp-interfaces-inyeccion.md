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
<text x="148" y="290">Por dos interfaces</text>
<text x="148" y="400">Solo SpriteAnimation.main</text>
</g>
<g font-size="15" style="fill:var(--texto-suave)">
<text x="148" y="94">y las propias: Sprite, SpriteSheet, Viewer</text>
<text x="148" y="204">Presenter aplica las reglas entre los dos</text>
<text x="148" y="314">AnimationView y AnimationPresenter</text>
<text x="148" y="424">crea las piezas y las conecta</text>
</g>
</svg>
<figcaption>Con MVP no te paras en ninguna curva: cada pregunta trae su respuesta, y en la demo da estas clases.</figcaption>
</figure>

La estructura, además, se nota después. Otra hoja de sprites solo afecta a `SpriteSheet`, y otra ventana solo a la vista. También se puede probar el presentador sin abrir ninguna ventana, como verás en «El contrato: las interfaces».

## MVP | Las tres piezas de MVP {#mvp}

MVP significa modelo, vista y presentador (en inglés, *Model-View-Presenter*). Cada pieza tiene un papel, y lo que define el patrón es tanto lo que cada una sabe como lo que no sabe.

El **modelo** es el estado del programa: en la demo, los sprites con su fotograma actual, su velocidad y su posición. No sabe que existe una ventana. La **vista** es lo que el usuario ve y toca: la ventana, el pintado y el ratón. No toma decisiones; solo pinta lo que le dan y avisa de lo que hace el usuario. El **presentador** está en medio: traduce el modelo a lo que la vista necesita y convierte los gestos del usuario en cambios del modelo.

::: tarjetas 3

### Model

Sabe
:   El estado: qué fotograma toca, a qué velocidad y dónde está cada sprite.

No sabe
:   Que existe una vista, ni un presentador.

### View

Sabe
:   Pintar y escuchar el ratón.

No sabe
:   Qué es un `Sprite`, ni qué reglas tiene el programa (por ejemplo, la velocidad máxima).

### Presenter

Sabe
:   Qué datos necesita la vista y qué reglas se aplican a cada gesto.

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

## Interfaces | El contrato: las interfaces {#interfaces}

La vista y el presentador no se conocen por su clase, sino por una interfaz: un contrato que dice qué se pueden pedir el uno al otro, sin decir cómo se hace. El presentador habla con un `AnimationView`, no con la clase `View`. La vista habla con un `AnimationPresenter`, no con la clase `Presenter`.

Estas son las dos interfaces de la demo. Son cortas a propósito: cada método es algo que la otra parte necesita de verdad.

```java
/** Lo que el presentador puede pedirle a la vista. */
public interface AnimationView {
    void setPresenter(AnimationPresenter presenter);
    int renderFps();
    void setRenderFps(int fps);
    void start();
    void stop();
}
```

```java
/** Lo que la vista puede pedirle al presentador. */
public interface AnimationPresenter {

    /** Una foto fija de un sprite: lo que la vista necesita para pintarlo. */
    record SpriteState(String name, BufferedImage frame, int x, int y, int fps) {}

    List<SpriteState> sprites();
    void changeRenderSpeed(int delta);
    void changeSpriteSpeed(int sprite, int delta);
    int spriteAt(int x, int y);
    void moveSprite(int sprite, int x, int y);
    void close();
}
```

`SpriteState` es un `record`: un objeto de datos que no se puede modificar. Va dentro de la interfaz porque es parte del contrato. Gracias a él, la vista pinta con datos que nadie le cambiará a medio pintar, aunque los hilos de los sprites sigan avanzando.

Lo que se gana con las interfaces es poder cambiar una pieza por otra que cumpla el mismo contrato. El presentador funciona igual con la vista de Swing, con una vista de texto o con una vista falsa que solo sirve para probar:

<figure class="vertical">
<svg viewBox="0 0 560 400" role="img" style="font-family:var(--fuente-texto);display:block">
<title>El presentador usa la interfaz AnimationView, que pueden implementar la vista de Swing, una vista de texto o una vista falsa para pruebas</title>
<defs><marker id="m3" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="8" markerHeight="8" orient="auto"><path d="M0 0 L10 5 L0 10 z" style="fill:var(--texto-suave)"/></marker></defs>
<rect x="160" y="16" width="240" height="60" rx="10" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="280" y="44" font-size="18" font-weight="700" text-anchor="middle" style="fill:var(--primario)">Presenter</text>
<text x="280" y="66" font-size="15" text-anchor="middle" style="fill:var(--texto-suave)">solo conoce el contrato</text>
<line x1="280" y1="78" x2="280" y2="134" style="stroke:var(--texto-suave)" stroke-width="2" marker-end="url(#m3)"/>
<text x="292" y="112" font-size="15" style="fill:var(--texto-suave)">usa</text>
<rect x="130" y="138" width="300" height="66" rx="10" fill="none" style="stroke:var(--oscuro)" stroke-width="3"/>
<text x="280" y="164" font-size="15" text-anchor="middle" style="fill:var(--texto-suave)">«interface»</text>
<text x="280" y="190" font-size="18" font-weight="700" text-anchor="middle" style="fill:var(--oscuro)">AnimationView</text>
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

Por ejemplo, una vista falsa de unas pocas líneas basta para comprobar que el presentador no deja pasar de 120 repintados por segundo, sin abrir ninguna ventana:

```java
class FakeView implements AnimationView {
    int fps = 60;
    public void setPresenter(AnimationPresenter presenter) {}
    public int renderFps() { return fps; }
    public void setRenderFps(int fps) { this.fps = fps; }
    public void start() {}
    public void stop() {}
}

FakeView view = new FakeView();
Presenter presenter = new Presenter(new Model(List.of()), view);
presenter.changeRenderSpeed(+500);
System.out.println(view.renderFps());   // 120: el presentador aplica el límite
```

## Inyección | Quién crea a quién: la inyección de dependencias {#inyeccion}

Si el presentador hiciera `new View()` dentro de su constructor, quedaría atado para siempre a esa vista, y la `FakeView` no serviría de nada. La **inyección de dependencias** es la solución: cada clase recibe lo que necesita por su constructor, y no lo crea ella.

```java
public class Presenter implements AnimationPresenter {

    private final Model model;
    private final AnimationView view;

    public Presenter(Model model, AnimationView view) {   // las recibe, no las crea
        this.model = model;
        this.view = view;
    }
    // …
}
```

Alguien tiene que crear las piezas, y en la demo ese sitio es uno solo: el método `main` de `SpriteAnimation`. Allí se crean los objetos en orden, de abajo arriba, y cada uno se pasa al que lo necesita. Es el único sitio del programa que hace `new` de las capas.

```java
public static void main(String[] args) {
    Sprite duck = new Sprite("Pato", SpriteSheet.load("/sprites/pato.png", 16, 16, 4), 8, 120, 220);
    Sprite explosion = new Sprite("Explosión", SpriteSheet.load("/sprites/explosion.png", 48, 48, 3), 12, 360, 160);

    Model model = new Model(List.of(duck, explosion));
    AnimationView view = new View();
    AnimationPresenter presenter = new Presenter(model, view);
    view.setPresenter(presenter);

    model.start();
    view.start();
}
```

Así se monta, paso a paso:

<figure class="vertical">
<svg viewBox="0 0 560 520" role="img" style="font-family:var(--fuente-texto);display:block">
<title>Animación: main crea los sprites, el modelo, la vista y el presentador, y conecta el presentador con la vista</title>
<style>
.a2-1{animation:a2-1 12s ease-in-out infinite}.a2-2{animation:a2-2 12s ease-in-out infinite}.a2-3{animation:a2-3 12s ease-in-out infinite}.a2-4{animation:a2-4 12s ease-in-out infinite}.a2-5{animation:a2-5 12s ease-in-out infinite}
.a2-c1{animation:a2-c1 12s ease-in-out infinite}.a2-c2{animation:a2-c2 12s ease-in-out infinite}.a2-c3{animation:a2-c3 12s ease-in-out infinite}.a2-c4{animation:a2-c4 12s ease-in-out infinite}.a2-c5{animation:a2-c5 12s ease-in-out infinite}
@keyframes a2-1{0%,2%{opacity:0}8%,96%{opacity:1}100%{opacity:0}}
@keyframes a2-2{0%,18%{opacity:0}24%,96%{opacity:1}100%{opacity:0}}
@keyframes a2-3{0%,34%{opacity:0}40%,96%{opacity:1}100%{opacity:0}}
@keyframes a2-4{0%,50%{opacity:0}56%,96%{opacity:1}100%{opacity:0}}
@keyframes a2-5{0%,66%{opacity:0}72%,96%{opacity:1}100%{opacity:0}}
@keyframes a2-c1{0%,2%{opacity:0}6%,16%{opacity:1}18%,100%{opacity:0}}
@keyframes a2-c2{0%,18%{opacity:0}22%,32%{opacity:1}34%,100%{opacity:0}}
@keyframes a2-c3{0%,34%{opacity:0}38%,48%{opacity:1}50%,100%{opacity:0}}
@keyframes a2-c4{0%,50%{opacity:0}54%,64%{opacity:1}66%,100%{opacity:0}}
@keyframes a2-c5{0%,66%{opacity:0}70%,96%{opacity:1}100%{opacity:0}}
@media (prefers-reduced-motion:reduce){[class^="a2-"]{animation:none}}
@media print{[class^="a2-"]{animation:none}}
</style>
<defs><marker id="m2" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="8" markerHeight="8" orient="auto"><path d="M0 0 L10 5 L0 10 z" style="fill:var(--oscuro)"/></marker></defs>
<rect x="40" y="10" width="480" height="48" rx="10" fill="none" style="stroke:var(--oscuro)" stroke-width="2"/>
<text x="280" y="40" font-size="18" font-weight="700" text-anchor="middle" style="fill:var(--oscuro)">SpriteAnimation.main</text>
<g class="a2-1">
<rect x="40" y="90" width="230" height="52" rx="8" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="155" y="122" font-size="18" font-weight="700" text-anchor="middle" style="fill:var(--primario)">Sprite pato</text>
<rect x="290" y="90" width="230" height="52" rx="8" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="405" y="122" font-size="18" font-weight="700" text-anchor="middle" style="fill:var(--primario)">Sprite explosión</text>
</g>
<g class="a2-2">
<g style="stroke:var(--oscuro)" stroke-width="2"><line x1="155" y1="144" x2="155" y2="186" marker-end="url(#m2)"/><line x1="405" y1="144" x2="405" y2="186" marker-end="url(#m2)"/></g>
<rect x="40" y="190" width="480" height="52" rx="8" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="280" y="222" font-size="18" font-weight="700" text-anchor="middle" style="fill:var(--primario)">Model</text>
</g>
<g class="a2-3">
<rect x="310" y="290" width="210" height="52" rx="8" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="415" y="322" font-size="18" font-weight="700" text-anchor="middle" style="fill:var(--primario)">View</text>
</g>
<g class="a2-4">
<line x1="130" y1="244" x2="130" y2="386" style="stroke:var(--oscuro)" stroke-width="2" marker-end="url(#m2)"/>
<text x="142" y="320" font-size="15" style="fill:var(--oscuro)">model</text>
<line x1="350" y1="344" x2="262" y2="390" style="stroke:var(--oscuro)" stroke-width="2" marker-end="url(#m2)"/>
<text x="318" y="384" font-size="15" style="fill:var(--oscuro)">view</text>
<rect x="40" y="390" width="220" height="52" rx="8" fill="none" style="stroke:var(--primario)" stroke-width="2"/>
<text x="150" y="422" font-size="18" font-weight="700" text-anchor="middle" style="fill:var(--primario)">Presenter</text>
</g>
<g class="a2-5">
<path d="M262 408 C 470 420, 500 380, 470 346" fill="none" style="stroke:var(--primario)" stroke-width="2.5" marker-end="url(#m2)"/>
<text x="400" y="432" font-size="15" font-weight="700" style="fill:var(--primario)">setPresenter</text>
</g>
<g font-size="15" font-family="monospace" style="fill:var(--texto)">
<text class="a2-c1" x="40" y="490" opacity="0">1 · new Sprite(…) para cada hoja</text>
<text class="a2-c2" x="40" y="490" opacity="0">2 · new Model(List.of(duck, explosion))</text>
<text class="a2-c3" x="40" y="490" opacity="0">3 · new View()</text>
<text class="a2-c4" x="40" y="490" opacity="0">4 · new Presenter(model, view)</text>
<text class="a2-c5" x="40" y="490">5 · view.setPresenter(presenter)</text>
</g>
</svg>
<figcaption>Cada objeto aparece cuando se crea, y cada flecha es algo que se le pasa al siguiente. El último paso cierra el círculo entre vista y presentador.</figcaption>
</figure>

El último paso merece una explicación. El presentador necesita la vista y la vista necesita el presentador, así que no pueden recibirse los dos por constructor: uno de los dos tiene que existir primero. La demo crea la vista, se la pasa al presentador y, al final, le da el presentador a la vista con `setPresenter`. Es la única dependencia que no va por constructor, y está a la vista en `main`.

No todo se inyecta. `View` crea su propio `Viewer`, porque el lienzo es una parte interna de la vista y nadie más lo necesita ni lo va a cambiar. Se inyecta lo que conecta capas o lo que querrías sustituir, como la vista falsa de las pruebas.

## Hilos | MVP con hilos {#hilos}

En la demo trabajan tres clases de hilos a la vez, y cada uno vive en una capa. Separar capas es lo que evita que se pisen: cada hilo sabe qué puede tocar y qué solo puede leer.

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

Cada `Sprite` tiene su hilo, que avanza el fotograma midiendo el tiempo que ha pasado, no contando vueltas. Por eso su ritmo es el mismo en cualquier equipo, y no depende de lo rápido que se pinte.

```java
public void run() {
    long last = System.nanoTime();
    double pending = 0;                        // fotogramas que tocaba avanzar y aún no
    while (running) {
        long now = System.nanoTime();
        pending += (now - last) / 1e9 * fps;   // mide el tiempo, no cuenta vueltas
        last = now;
        while (pending >= 1) {
            frame = (frame + 1) % frames.length;
            pending--;
        }
        // … duerme 5 ms y vuelve a empezar
    }
}
```

Los campos que escribe un hilo y lee otro son `volatile`, para que cada hilo vea el último valor escrito. Es la solución mínima, y basta porque cada campo se escribe entero de una vez. La vista, además, no lee los sprites directamente: recibe una lista de `SpriteState` que no cambia mientras pinta.

MVP ayuda aquí de una forma muy concreta. Como la vista solo habla con el presentador, el hilo de pintado nunca toca un `Sprite`, y como el modelo no conoce la vista, los hilos de los sprites nunca tocan Swing. Cada frontera entre capas es también una frontera entre hilos.

## Un gesto | Un gesto, de principio a fin {#gesto}

Para ver todas las piezas juntas, sigue un gesto: giras la rueda del ratón sobre la cifra del pato en el HUD. El gesto baja por las capas hasta el modelo, y el cambio aparece en el siguiente pintado sin que nadie avise a la vista.

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
    Sprite s = model.sprites().get(sprite);
    s.setFps(limit(s.fps() + delta));                       // entre 1 y 120
}
```

Arrastrar un sprite sigue el mismo camino. La vista pregunta al presentador qué sprite hay bajo el puntero (`spriteAt`) y, mientras arrastras, le pide que lo mueva (`moveSprite`). El pato sigue aleteando todo el rato, porque su hilo no sabe nada del ratón.

## Errores | Errores frecuentes {#errores}

Estos son los atajos que más se repiten al empezar con MVP. Todos funcionan el primer día, y todos complican el segundo.

::: tarjetas 2

### La vista lee el modelo

Cómo se ve
:   `View` recibe el `Model` y recorre sus `Sprite` para pintarlos.

Por qué es un problema
:   La vista queda atada a las clases del modelo, y el hilo de pintado toca objetos que otros hilos están cambiando.

Cómo se arregla
:   La vista solo conoce `AnimationPresenter` y pinta `SpriteState`.

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

### La interfaz que lo expone todo

Cómo se ve
:   `AnimationPresenter` tiene veinte métodos, uno por cada campo del modelo.

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
:   El modelo no conoce a nadie; la vista pide los datos cuando le toca pintar.

### Contar vueltas en lugar de medir el tiempo

Cómo se ve
:   El fotograma avanza uno en cada vuelta del bucle.

Por qué es un problema
:   La animación va más rápida en un equipo rápido y más lenta en uno lento.

Cómo se arregla
:   Medir el tiempo transcurrido con `System.nanoTime()` y avanzar según los fotogramas por segundo.

:::

## Practicar | Para practicar {#practicar}

Cada ejercicio cambia una sola cosa de la demo. Antes de empezar, intenta adivinar qué clases tendrás que tocar: si la estructura está bien hecha, son muy pocas.

::: tarjetas 2

### Una tercera animación

Qué tienes que hacer
:   Busca una hoja de sprites y añade un tercer sprite a la demo.

Qué clases cambian
:   Solo `SpriteAnimation`. El HUD muestra la nueva línea sin tocar la vista.

Qué aprendes
:   Que montar las piezas en un solo sitio hace que añadir sea fácil.

### Una vista de texto

Qué tienes que hacer
:   Escribe una `TextView` que implemente `AnimationView` y escriba en consola, una vez por segundo, el fotograma y la posición de cada sprite.

Qué clases cambian
:   Ninguna: añades `TextView` y cambias una línea de `main`.

Qué aprendes
:   Para qué sirve el contrato: el presentador no nota la diferencia.

### Una prueba sin ventana

Qué tienes que hacer
:   Con la `FakeView`, comprueba que la velocidad no baja de 1 ni sube de 120, para el pintado y para cada sprite.

Qué clases cambian
:   Ninguna: la prueba es un programa aparte.

Qué aprendes
:   Que la inyección de dependencias hace que el presentador se pueda probar solo.

### Pausar un sprite

Qué tienes que hacer
:   Con un doble clic sobre un sprite, su animación se para o vuelve a empezar.

Qué clases cambian
:   `View` (el gesto), `AnimationPresenter` y `Presenter` (el método nuevo) y `Sprite` (la pausa).

Qué aprendes
:   Cómo atraviesa una funcionalidad nueva las tres capas, y cómo se para y se reanuda un hilo.

:::
