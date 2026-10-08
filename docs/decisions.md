# Decisiones del primer MVP

- **Plataforma:** Android nativo con Kotlin, Jetpack Compose y Material 3.
- **Datos:** almacenamiento local con Room; un registro consolidado por fecha local,
  con minutos enteros como unidad.
- **Calendario laboral:** la semana de trabajo va de lunes a viernes. No se permite
  registrar sábados ni domingos y no se muestran en el historial ni se incluyen en
  totales. La fecha se almacena como ISO (`YYYY-MM-DD`) para que los periodos semanales
  se consulten sin conversiones de zona horaria.
- **Edición:** sumar/restar usa el intervalo configurado (15, 30 o 60 minutos); el
  mínimo es cero. Inicio no muestra avisos flotantes tras los cambios.
- **Preferencias:** el intervalo, el medidor de enfado y la probabilidad de cerveza se
  guardan en preferencias privadas de la aplicación. El medidor del amor se puede
  mover como broma, rebota al 100 % y no se guarda.
- **Reacción visual y sonora:** al incrementar, el contador se tiñe brevemente y
  expulsa vapor por los laterales. Un efecto de tren a vapor original se reproduce
  sin permisos de audio ni recursos de terceros.
- **Fondo:** cuadrícula futurista y órbitas de luz con movimiento lento y continuo,
  usando Canvas de Compose sin recursos externos.
- **Medidor de amor:** el usuario puede arrastrar el marcador como broma. Al soltarlo,
  rebota con animación de muelle hacia el 100 %; el valor es temporal y no se guarda.
- **Mensajes semanales:** menos de 120 minutos, de 120 a 300 minutos inclusive y más
  de 300 minutos.
- **Tema:** estilo oscuro futurista con acentos neón como dirección visual por defecto.
- **Minijuego:** juego original de atrapar objetos dibujado con Compose Canvas, sin
  recursos de terceros. Cervezas caen desde la parte superior y se mueve una caja con
  el dedo para atraparlas; cada captura suma un punto. El ritmo de caída y aparición
  aumenta con la puntuación y la partida termina al dejar caer tres cervezas.
- **Fuera de alcance explícito:** login, pantalla de acceso, cifrado de datos,
  autenticación biométrica, sincronización en la nube y publicación en Google Play.
- **Privacidad actual:** la app no requiere conexión de red para registrar ni consultar
  horas. Los datos no se sincronizan con un servidor.

## Verificación funcional mínima

En Android Studio, ejecutar `:app:testDebugUnitTest` y `:app:assembleDebug`. Después,
probar manualmente el efecto de vapor y sonido al sumar, suma, resta sin saldo negativo,
persistencia tras cerrar la aplicación, cambios de medidores, que los días del
historial sean solo laborables, los umbrales de mensajes semanales, navegación semanal,
la vuelta animada del medidor de amor al 100 %, edición de días pasados y una partida
del minijuego en la que se atrapen cervezas con la caja, aumente la puntuación, se
alcancen tres fallos y se pruebe el reinicio en un emulador o dispositivo API 26+.

## Próximo bloque

Completar historial mensual y ampliar las pruebas de almacenamiento en dispositivo.
Las reacciones con medios, el acceso y el cifrado requieren decisiones y material
adicional; no deben añadirse al primer MVP.
