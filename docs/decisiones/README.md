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
