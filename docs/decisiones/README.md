# Decisiones

Decisiones tomadas por el propietario, con su porqué.

**Cómo se registra una decisión.** Un archivo por decisión en esta carpeta, `NNN-AAAA-MM-DD-titulo.md`: el número de orden total (el siguiente al más alto), la fecha del origen y el título sin tildes ni signos. El nombre lo da `indice_decisiones.py docs/decisiones --nombre "Título"`. Empieza por `# Título` y lleva:

- **Origen:** proyecto, fecha y palabras del propietario cuando las hay.
- **Contexto:** el problema.
- **Opciones:** las que se valoraron, si las hubo.
- **Decisión:** lo decidido y por qué.
- **Consecuencias:** lo que cambia y lo que queda pendiente.
- **Aplicada en:** los documentos, skills o piezas que la recogen.

Si una decisión cambia otra anterior, la nueva lo dice («Sustituye a…») y la antigua se marca, sin borrarla, con una línea bajo el título: «**Sustituida** el AAAA-MM-DD por «…»» (o **Sustituida en parte**).

Este índice no se escribe a mano: se regenera con `python -I -X utf8 tools/indice_decisiones.py docs/decisiones` (`tools/` del plugin `sf`) al añadir una decisión y al juntar ramas. Lo que hay antes de «Índice» se conserva.

## Índice

| Nº | Fecha | Decisión |
| --- | --- | --- |
| 19 | 2026-11-02 | [Las interfaces de la demo, en un contrato](019-2026-11-02-las-interfaces-de-la-demo-en-un-contrato.md) |
| 18 | 2026-11-02 | [Sufijo Interface en las interfaces de la demo](018-2026-11-02-sufijo-interface-en-las-interfaces-de-la-demo.md) |
| 17 | 2026-11-02 | [El presentador recibe el modelo y la vista por sus interfaces](017-2026-11-02-el-presentador-recibe-el-modelo-y-la-vista-por-sus-interfaces.md) |
| 16 | 2026-11-02 | [El constructor del presentador conecta y start() solo arranca](016-2026-11-02-el-constructor-del-presentador-conecta-y-start-solo-arranca.md) |
| 15 | 2026-11-02 | [main pide los sprites al modelo, no el presentador](015-2026-11-02-main-pide-los-sprites-al-modelo-no-el-presentador.md) |
| 14 | 2026-11-02 | [Los sprites se crean en el modelo y el presentador arranca la aplicación](014-2026-11-02-los-sprites-se-crean-en-el-modelo-y-el-presentador-arranca-la-aplicacion.md) |
| 13 | 2026-11-02 | [Viñetas para mostrar el montaje paso a paso](013-2026-11-02-vinetas-para-mostrar-el-montaje-paso-a-paso.md) |
| 12 | 2026-10-31 | [Interfaces y constructores de la demo](012-2026-10-31-interfaces-y-constructores-de-la-demo.md) |
| 11 | 2026-10-30 | [Para segundo de DAM: videojuegos y Servicios y Procesos](011-2026-10-30-para-segundo-de-dam-videojuegos-y-servicios-y-procesos.md) |
| 10 | 2026-10-30 | [Dos hojas de sprites: pato y explosión](010-2026-10-30-dos-hojas-de-sprites-pato-y-explosion.md) |
| 9 | 2026-10-30 | [Clases de la demo con Viewer](009-2026-10-30-clases-de-la-demo-con-viewer.md) |
| 8 | 2026-10-30 | [Arrastrar los sprites con el ratón](008-2026-10-30-arrastrar-los-sprites-con-el-raton.md) |
| 7 | 2026-10-30 | [HUD de velocidades ajustable con la rueda del ratón](007-2026-10-30-hud-de-velocidades-ajustable-con-la-rueda-del-raton.md) |
| 6 | 2026-10-30 | [Velocidad de renderización frente a velocidad de animación](006-2026-10-30-velocidad-de-renderizacion-frente-a-velocidad-de-animacion.md) |
| 5 | 2026-10-30 | [Clases de la demo e interfaces MVP](005-2026-10-30-clases-de-la-demo-e-interfaces-mvp.md) (sustituida) |
| 4 | 2026-10-30 | [Ejemplo mínimo de clases y código](004-2026-10-30-ejemplo-minimo-de-clases-y-codigo.md) |
| 3 | 2026-10-30 | [Un hilo por sprite y un hilo de pintado](003-2026-10-30-un-hilo-por-sprite-y-un-hilo-de-pintado.md) |
| 2 | 2026-10-30 | [Swing y Java2D como librería gráfica](002-2026-10-30-swing-y-java2d-como-libreria-grafica.md) |
| 1 | 2026-10-30 | [Material de consulta sobre sprites y MVP para FP](001-2026-10-30-material-de-consulta-sobre-sprites-y-mvp-para-fp.md) (sustituida) |
