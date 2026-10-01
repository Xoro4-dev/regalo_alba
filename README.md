# Horas con Amor

Aplicacion Android nativa en Kotlin y Jetpack Compose para llevar un contador de horas.
Esta primera fase deja preparada la estructura y la navegacion entre Inicio, Historial
y Ajustes; todavia no incluye persistencia ni logica de negocio.

## Requisitos

- Android Studio Quail 3 o posterior.
- JDK 17.
- SDK de Android API 37.
- Un emulador o dispositivo con Android 8.0 (API 26) o posterior.

## Ejecutar

1. Abre esta carpeta desde Android Studio.
2. Acepta la sincronizacion de Gradle e instala Android SDK Platform 37 si se solicita.
3. Crea o selecciona un emulador con API 26 o posterior.
4. Selecciona la configuracion `app` y pulsa **Run**.

En Windows, tambien puedes compilar desde la raiz con:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Estructura

- `app/src/main/java/com/hugodev/horasconamor/ui/theme`: tema Material 3.
- `app/src/main/java/com/hugodev/horasconamor/navigation`: navegacion y barra inferior.
- `app/src/main/java/com/hugodev/horasconamor/ui/counter`: pantalla Inicio.
- `app/src/main/java/com/hugodev/horasconamor/ui/history`: pantalla Historial.
- `app/src/main/java/com/hugodev/horasconamor/ui/settings`: pantalla Ajustes.
