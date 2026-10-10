# Publicación en Google Play

## Estado

- Identificador Android: `com.hugodev.horasconamor`. Google Play lo fija al crear
  la ficha: confirmarlo antes de la primera subida.
- Versión preparada: `versionCode 1`, `versionName 1.0`.
- `targetSdk`: 37. Cumple el mínimo actual de Play, API 36, requerido para nuevas
  apps y actualizaciones desde el 31 de agosto de 2026.
- La app no solicita permiso de Internet, no contiene SDK de anuncios ni analítica
  y guarda el historial y las preferencias en almacenamiento privado local.
- Las copias automáticas de Android están desactivadas para que el historial de
  horas y los ajustes no se suban al servicio de backup del dispositivo.
- Icono adaptativo de Android: `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`.
- Icono de ficha de Play (512 × 512 PNG): `play-store/assets/app-icon-512.png`.
- Gráfico de funciones de Play (1024 × 500 PNG): `play-store/assets/feature-graphic.png`
  (fuente SVG en el mismo directorio).
- Capturas reales de teléfono: `play-store/assets/screenshots/`.
- Política de privacidad: se publicará en
  `https://xoro4-dev.github.io/regalo_alba/` mediante GitHub Pages, cuando se completen
  los datos de contacto, se fusione el workflow a `main` y se habilite Pages.
- No se ha creado ni guardado una clave de subida; la clave y las credenciales deben
  generarse y custodiarse por la persona responsable de publicar.

## Firma y generación del Android App Bundle

1. Genera una clave de subida fuera del repositorio. `keytool` solicitará la
   contraseña de forma interactiva:

   ```bash
   mkdir -p "$HOME/.android"
   keytool -genkeypair -v \
     -keystore "$HOME/.android/horas-con-amor-upload.jks" \
     -alias horas-con-amor-upload \
     -keyalg RSA -keysize 2048 -validity 10000
   ```

2. Expón estas cuatro propiedades al proceso de Gradle mediante variables de
   entorno o el archivo de propiedades **global del usuario**
   `~/.gradle/gradle.properties`. No las guardes en este repositorio ni subas la
   clave `.jks`:

   ```properties
   PLAY_UPLOAD_STORE_FILE=/ruta/absoluta/a/horas-con-amor-upload.jks
   PLAY_UPLOAD_STORE_PASSWORD=...
   PLAY_UPLOAD_KEY_ALIAS=horas-con-amor-upload
   PLAY_UPLOAD_KEY_PASSWORD=...
   ```

   El `.gitignore` ya excluye `*.jks`, `*.keystore` y `keystore.properties`.
   Guarda también una copia segura de la clave y sus contraseñas: Google Play App
   Signing usa esta clave para verificar las subidas. No es la clave de firma final
   que Google administra para distribuir la app.

3. Desde la raíz del proyecto, ejecuta:

   ```bash
   ./gradlew :app:testDebugUnitTest :app:bundleRelease
   ```

   El bundle firmado queda en
   `app/build/outputs/bundle/release/app-release.aab`. La tarea `bundleRelease`
   falla de forma explícita si no se configuraron las cuatro propiedades de firma.
   No subas el APK de depuración a producción.

4. Activa **Play App Signing** en Play Console durante la primera subida y conserva
   de forma segura la clave de subida. Incrementa `versionCode` para cada bundle
   nuevo que subas.

## Borrador de ficha (español)

- **Nombre** (15/30): `Horas con Amor`
- **Descripción breve** (71/80):
  `Registra tus horas extra y reserva tiempo para vivir fuera del trabajo.`
- **Categoría sugerida**: Productividad.
- **Anuncios**: no contiene anuncios.
- **Acceso para revisión**: no hace falta cuenta ni inicio de sesión.
- **Icono de ficha**: `play-store/assets/app-icon-512.png`.
- **Capturas de teléfono**: cuatro imágenes de la versión candidata están en
  `play-store/assets/screenshots/`. Revisarlas antes de subir. Play acepta entre 2
  y 8 capturas de teléfono; cada lado debe medir entre 320 y 3.840 px.
- **Gráfico promocional**: `play-store/assets/feature-graphic.png` (1.024 × 500 px).
- **Política de privacidad**: `docs/privacy-policy.html` es un borrador con campos
  de contacto completados. El workflow puede publicarlo en la URL HTTPS indicada
  después de fusionarse a `main` y habilitar GitHub Pages.

**Descripción larga propuesta:**

> Lleva el control de tus horas extra y recuerda que tu tiempo también importa.
>
> • Registra y corrige las horas extra de cada día laborable.  
> • Consulta el total semanal y revisa semanas anteriores.  
> • Ajusta tus preferencias desde una pantalla de ajustes con un toque de humor.  
> • Tómate un descanso con un minijuego: mueve una caja para atrapar latas que caen.  
>
> Horas con Amor no requiere cuenta ni conexión a Internet. El historial y las
> preferencias se guardan en este dispositivo. Al desinstalar la app, se eliminan
> sus datos locales.

## Play Console: tareas de la persona titular de la cuenta

1. Crear la app en Play Console y comprobar que `com.hugodev.horasconamor` esté
   disponible. El identificador queda fijo al crearla. Usar español como idioma
   predeterminado y seleccionar la categoría.
2. Completar la ficha, añadir el gráfico promocional y capturas propias, y publicar
   la política de privacidad ya completada.
3. Completar **App content**: clasificación de contenido, público objetivo, anuncios,
   seguridad de datos y cualquier declaración adicional que solicite la consola.
   Responder sobre la temática de cerveza del minijuego de forma fiel al contenido.
4. Revisar la sección **Data safety** contra el bundle final y todos sus SDK. La
   implementación actual almacena horas y preferencias solo localmente, no solicita
   Internet y desactiva Android Auto Backup; no declara servidores propios,
   publicidad ni analítica.
5. Subir el AAB firmado primero a una pista de pruebas y verificarlo en dispositivos
   reales. La cuenta personal de publicación se creó antes del 13 de noviembre de
   2023, por lo que el requisito específico de prueba cerrada de 12 personas durante
   14 días para cuentas personales nuevas no debería aplicar. Seguir cualquier otro
   requisito que Play Console muestre para esa cuenta.
6. Cuando Play Console dé acceso a producción, revisar países, precio (gratis o de
   pago), revisión final y enviar el lanzamiento.

Para activar la política después de completar sus campos, fusionar este workflow a
`main` y seleccionar **Settings > Pages > Build and deployment > GitHub Actions**.
El workflow rechaza publicar mientras queden marcadores de contacto.

Antes de publicar, comprobar la disponibilidad del identificador y completar la
ficha en la cuenta real. El titular de la cuenta Play debe iniciar sesión y crear
la app; no compartir contraseñas ni códigos de acceso. Google puede cambiar
requisitos; confirmar los avisos de Play Console justo antes de subir.

## Referencias oficiales

- [Requisitos del nivel de API objetivo](https://support.google.com/googleplay/android-developer/answer/11926878)
- [Pruebas para cuentas personales nuevas](https://support.google.com/googleplay/android-developer/answer/14151465)
- [Declaración de seguridad de datos](https://support.google.com/googleplay/android-developer/answer/10787469)
- [Contenido de la app y políticas de Play Console](https://support.google.com/googleplay/android-developer/answer/9859455)
- [Recursos gráficos de ficha](https://support.google.com/googleplay/android-developer/answer/9866151)
