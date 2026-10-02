# Decisiones del primer MVP

- **Plataforma:** Android nativo con Kotlin, Jetpack Compose y Material 3.
- **Datos:** almacenamiento local con Room; un registro consolidado por fecha local,
  con minutos enteros como unidad.
- **Calendario:** las semanas empiezan el lunes. La fecha se almacena como ISO
  (`YYYY-MM-DD`) para que los periodos semanales se consulten sin conversiones de zona
  horaria.
- **Edición:** sumar/restar usa el intervalo configurado (15, 30 o 60 minutos); el
  mínimo es cero. Los cambios desde Inicio se pueden deshacer.
- **Preferencias:** el intervalo se guarda en preferencias privadas de la aplicación.
- **Fuera de alcance explícito:** login, pantalla de acceso, cifrado de datos,
  autenticación biométrica, recursos personales (audio/foto/GIF), minijuegos,
  sincronización en la nube y publicación en Google Play.
- **Privacidad actual:** la app no requiere conexión de red para registrar ni consultar
  horas. Los datos no se sincronizan con un servidor.

## Verificación funcional mínima

En Android Studio, ejecutar `:app:testDebugUnitTest` y `:app:assembleDebug`. Después,
probar manualmente suma, resta sin saldo negativo, deshacer, persistencia tras cerrar
la aplicación, cambio del intervalo, navegación semanal y edición de días pasados en
un emulador o dispositivo API 26+.

## Próximo bloque

Completar historial mensual y ampliar las pruebas de almacenamiento en dispositivo.
Las reacciones con medios, el acceso y el cifrado requieren decisiones y material
adicional; no deben añadirse al primer MVP.
