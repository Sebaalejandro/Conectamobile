

# ConectaMobile
## Descripción:

La aplicación Evaluación Nacional permite a los usuarios registrarse utilizando su correo electrónico y una contraseña. Durante el proceso de registro, los usuarios deben ingresar un nombre de usuario y seleccionar una foto de perfil. La foto se sube a Firebase Storage y los datos del usuario, como el nombre, correo y URL de la foto de perfil, se almacenan en Firebase Firestore.

La aplicación también incluye validaciones para asegurar que el correo electrónico sea válido (solo Gmail) y que la contraseña cumpla con ciertos requisitos de seguridad, como contener al menos una letra mayúscula y un carácter especial.

Se utiliza Firebase Authentication para gestionar el registro de usuarios de forma segura.
