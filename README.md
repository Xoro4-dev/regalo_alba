# Horas con Amor

Aplicación Android nativa en Kotlin y Jetpack Compose para registrar las horas extra
por día. Este repositorio contiene el primer MVP local; no incluye login, pantalla de
acceso ni cifrado de datos.

## Alcance implementado

- Contador diario con botones para sumar o restar minutos.
- Intervalo configurable de 15, 30 o 60 minutos; comienza en 30 minutos.
- Saldo diario nunca negativo y acción «Deshacer» después de un cambio.
- Total de la semana laboral, de lunes a viernes; sábados y domingos no se registran.
- Historial semanal con navegación a semanas anteriores y corrección de días.
- Reacción al sumar tiempo: sacudida breve, destello rojo y frase humorística.
- Ajustes con medidor de enfado y probabilidad de cerveza; medidor del amor fijo al 100 %.
- Persistencia local con Room y preferencias locales para el intervalo y medidores.
- Interfaz en español con tema futurista Material 3 oscuro/neón.
- Frases humorísticas de historial según el total semanal: menos de 2 h, de 2 a 5 h y más de 5 h.

Los datos se guardan en el dispositivo. El historial previo del MVP se conserva; los
sábados y domingos dejan de mostrarse y no se incluyen en los totales laborales.
Login y cifrado siguen fuera de alcance; minijuegos e incorporación de medios
personales pueden hacerse en iteraciones posteriores.

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
formato de duración, sumas y el límite inferior de cero. La compilación comprueba la
integración de Room y Compose.

## Pruebas manuales en emulador o dispositivo

1. Confirma que la app abre en el tema oscuro/neón y que Inicio muestra el estado de turno.
2. En un día laborable, pulsa `+30 min`: el contador debe temblar, destellar en rojo brevemente, actualizar el total y mostrar una frase.
3. Pulsa «Deshacer» en el mensaje: ambos totales vuelven al valor previo.
4. Resta tiempo hasta cero y confirma que no puede quedar un saldo negativo.
5. En Historial, confirma que aparecen exactamente lunes, martes, miércoles, jueves y viernes; no deben aparecer sábado ni domingo.
6. Cambia el total semanal a 119, 120, 300 y 301 minutos y confirma que la frase cambia en los umbrales de 2 h y 5 h.
7. Prueba la navegación a semanas anteriores, la edición de un día pasado y que no se puede editar fechas futuras.
8. En Ajustes, cambia el enfado y la probabilidad de cerveza con las barras; confirma que la barra de amor permanece al 100 % y no se puede mover.
9. Selecciona `15 min`, cierra la app y ábrela de nuevo; tanto el intervalo como las barras deben conservar sus valores.
10. Simula sábado y domingo cambiando la fecha del emulador: el contador debe indicar descanso y los botones de suma/resta deben quedar desactivados.

## Estructura

- `data/local`: entidad, DAO y base de datos Room.
- `data/repository`: acceso a los registros locales.
- `domain`: reglas de calendario laboral, umbrales y presentación de duración.
- `ui`: estado compartido y pantallas de Inicio, Historial y Ajustes.
- `navigation`: navegación inferior.
- `ui/theme`: tema Material 3 personalizado.
