# Grupo_607
## Trabajo Practico de Android

**SenDroid** es una aplicación que se encarga principalmente de detectar los eventos de determinados sensores y registrarlos en un servidor. Para usarla, el usuario debe estar previamente registrado. 
Es compatible con las versiones 4.0.3 (Ice Cream Sandwich) o superior de Android. 
Está compuesta de 4 activities:

✓ **WelcomeActivity:** Se muestra el logo de la aplicación como bienvenida.

✓ **MainActivity:** En esta Activity el usuario debe ingresar los datos para registrarse o loguearse. 
En caso de que el dispositivo no tenga conexión a Internet, se mostrará en pantalla el mensaje correspondiente.

✓ **HomeActivity:** La aplicación realiza diferentes acciones según el sensor que detecte y muestra la información recibida. Los eventos son:
- Acelerómetro: Al agitar el celular (Shake) la interfaz cambia de color.
- Sensor de proximidad: Al pasar un objeto por el sensor (habitualmente se encuentra en la parte superior del dispositivo), se abre la aplicación cámara para poder capturar una imagen. .
Ambos eventos se registran en el servidor, mostrando el estado en pantalla.

✓ **ListEvents:** Muestra un listado de todos los eventos de sensores registrados hasta el momento con su correspondiente fecha y hora. 

Otros eventos que se informan son el inicio de sesión y batería baja.
Al iniciar la aplicación por primera vez, se le pedirá permisos para acceder a la cámara.

