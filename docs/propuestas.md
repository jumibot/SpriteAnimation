# Propuestas

Lo que aún no está decidido en este proyecto, también las mejoras de la SF que se detecten aquí (decisión 194). Cada propuesta va en «Propuestas pendientes» como `### P<nn> <título>`, con el primer número libre, y lleva su origen, el cambio propuesto y su estado («- **Estado:** propuesta, pendiente de aprobación.»). El panel muestra las pendientes en su sección Propuestas; «A Pendientes» pasa una a tarea de la cola.

## Propuestas pendientes

### P01 Hablar de cerrar o revertir las tareas de Testing, no de darlas por probadas

- **Origen:** al preguntar por la tarea «Poner el proyecto al día con la SF 1.3.6» (5), en Testing, Claude ofreció «darla por probada». El propietario lo corrigió: las tareas de Testing se cierran o se revierten.
- **Cambio propuesto:** en `sf:orquestacion` («Probar y cerrar») y en `sf:flujo_tareas` (tarjeta 22), nombrar las salidas de Testing como «Cerrar» y «Revertir». Las opciones de la pregunta por las tareas de Testing serían Cerrar, Revertir y Probar ahora, en lugar de Probada, Pendiente y Probar ahora. Valorar si la orden `cola.py probada` debe llamarse también `cerrar` cuando la tarea está en Testing.
- **Estado:** propuesta, pendiente de aprobación.
