# Horas con Amor

Aplicación Android nativa en Kotlin y Jetpack Compose para registrar las horas extra
por día. Este repositorio contiene el primer MVP local; no incluye login, pantalla de
acceso ni cifrado de datos.

## Alcance implementado

- Contador diario con botones para sumar o restar minutos.
- Intervalo configurable de 15, 30 o 60 minutos; comienza en 30 minutos.
- Saldo diario nunca negativo y acción «Deshacer» después de un cambio.
- Total de la semana, de lunes a domingo.
- Historial semanal con navegación a semanas anteriores y corrección de días.
- Persistencia local con Room y preferencias locales para el intervalo.
- Interfaz en español con tema Material 3 claro y oscuro.
- Frase humorística al añadir tiempo.

Los datos se guardan en el dispositivo. El siguiente bloque podrá añadir historial
mensual, ajustes de reacciones y medios personales; los minijuegos quedan fuera del
primer MVP.

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

Las pruebas unitarias cubren los límites del cálculo semanal, formato de duración,
sumas y el límite inferior de cero. La compilación comprueba la integración de Room y
Compose.

## Pruebas manuales en emulador o dispositivo

1. En una instalación nueva, confirma que Inicio muestra `0 min` y que `−30 min` está desactivado.
2. Pulsa `+30 min`: el contador diario y el total semanal deben mostrar `30 min` y aparecer una frase.
3. Pulsa «Deshacer» en el mensaje: ambos totales vuelven al valor previo.
4. Resta 30 minutos: ambos totales vuelven a cero; no es posible obtener un saldo negativo.
5. En Ajustes, selecciona `15 min`, vuelve a Inicio y confirma que el botón suma 15 minutos.
6. Cierra la aplicación desde Recientes y vuelve a abrirla: el total del día y el ajuste deben mantenerse.
7. En Historial, confirma que la semana empieza en lunes, navega a la anterior y vuelve a la actual.
8. Añade o resta tiempo en un día pasado: el valor diario y el total semanal deben actualizarse.
9. Comprueba que no se puede navegar a semanas futuras ni editar fechas futuras.
10. Repite una suma/resta tras cambiar la fecha del sistema a otro día y confirma que cada día conserva su propio total.

## Estructura

- `data/local`: entidad, DAO y base de datos Room.
- `data/repository`: acceso a los registros locales.
- `domain`: reglas de calendario y presentación de duración.
- `ui`: estado compartido y pantallas de Inicio, Historial y Ajustes.
- `navigation`: navegación inferior.
- `ui/theme`: tema Material 3 personalizado.
