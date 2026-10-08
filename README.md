# Horas con Amor

Aplicación Android nativa en Kotlin y Jetpack Compose para registrar las horas extra
por día. Este repositorio contiene el primer MVP local; no incluye login, pantalla de
acceso ni cifrado de datos.

## Alcance implementado

- Contador diario con botones para sumar o restar minutos.
- Intervalo configurable de 15, 30 o 60 minutos; comienza en 30 minutos.
- Saldo diario nunca negativo, con corrección directa desde el contador y el historial.
- Total de la semana laboral, de lunes a viernes; sábados y domingos no se registran.
- Historial semanal con navegación a semanas anteriores y corrección de días.
- Ajustes con medidor de enfado y probabilidad de cerveza; el medidor del amor vuelve al 100 % con un rebote.
- Persistencia local con Room y preferencias locales para el intervalo y medidores.
- Interfaz en español con tema futurista Material 3 oscuro/neón, fondo animado sutil de cuadrícula, órbitas y luces difuminadas.
- Al sumar tiempo, el contador se tiñe brevemente y expulsa vapor por los laterales con un efecto sonoro original tipo tren a vapor; no aparecen avisos flotantes.
- Frases humorísticas de historial según el total semanal: menos de 2 h, de 2 a 5 h y más de 5 h.
- Minijuego de reflejos original: mueve una caja con el dedo para atrapar cervezas que caen del cielo. Cada captura suma un punto; las cervezas caen cada vez más rápido y la partida termina tras tres fallos.

Los datos del contador se guardan en el dispositivo. El historial previo del MVP se
conserva; los sábados y domingos dejan de mostrarse y no se incluyen en los totales
laborales. Login y cifrado siguen fuera de alcance. El minijuego no necesita conexión
ni medios externos.

## Requisitos

- Android Studio con soporte para Gradle 9.5 y Kotlin 2.4.20.
- JDK 21 (Android Studio puede usar su JBR).
- Android SDK Platform 37.
- Emulador o dispositivo con Android 8.0 (API 26) o posterior.

## Abrir y ejecutar en Android Studio

1. Abre la raíz del repositorio en Android Studio.
2. Acepta la sincronización de Gradle y la instalación de componentes del SDK que solicite.
3. Selecciona un emulador o dispositivo con API 26 o posterior.
4. Ejecuta la configuración `app`.

## Comprobaciones automatizadas

Desde la raíz del proyecto:

```bash
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

Si el wrapper no tiene permiso de ejecución en el entorno, usa:

```bash
bash gradlew :app:testDebugUnitTest :app:assembleDebug
```

Las pruebas unitarias cubren días laborables, límites de los mensajes semanales,
formato de duración, sumas, el límite inferior de cero y las reglas del minijuego:
movimiento de la caja, capturas, fallos y reinicio.

## Pruebas manuales en emulador o dispositivo

1. Confirma que la app abre en el tema oscuro/neón con una cuadrícula y luces de fondo que se mueven lentamente.
2. En un día laborable, pulsa `+30 min`: debe salir vapor por los laterales del contador, sonar un breve «chu-chu» y actualizarse el total sin mostrar avisos en la parte inferior.
3. Resta tiempo desde el botón `−30 min` y confirma que el total se actualiza sin mostrar avisos.
4. Resta tiempo hasta cero y confirma que no puede quedar un saldo negativo.
5. En Historial, confirma que aparecen exactamente lunes, martes, miércoles, jueves y viernes; no deben aparecer sábado ni domingo.
6. Cambia el total semanal a 119, 120, 300 y 301 minutos y confirma que la frase cambia en los umbrales de 2 h y 5 h.
7. Prueba la navegación a semanas anteriores, la edición de un día pasado y que no se puede editar fechas futuras.
8. En Ajustes, cambia el enfado y la probabilidad de cerveza con las barras; mueve la barra del amor y suéltala: debe rebotar de vuelta al 100 %.
9. Selecciona `15 min`, cierra la app y ábrela de nuevo; tanto el intervalo como las barras deben conservar sus valores.
10. Simula sábado y domingo cambiando la fecha del emulador: el contador debe indicar descanso y los botones de suma/resta deben quedar desactivados.
11. Abre **Minijuego** y desliza la caja hacia los lados para atrapar las cervezas que caen. Confirma que cada captura suma un punto, que dejar caer tres cervezas termina la partida y que `Jugar otra vez` reinicia la puntuación.

## Estructura

- `data/local`: entidad, DAO y base de datos Room.
- `data/repository`: acceso a los registros locales.
- `domain`: reglas de calendario laboral, umbrales y presentación de duración.
- `ui`: estado compartido y pantallas de Inicio, Historial y Ajustes.
- `ui/game`: motor de juego de atrapar cervezas y escena original dibujada con Compose Canvas.
- `navigation`: navegación inferior.
- `ui/theme`: tema Material 3 personalizado.
