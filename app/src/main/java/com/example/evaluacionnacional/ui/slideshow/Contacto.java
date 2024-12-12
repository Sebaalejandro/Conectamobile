package com.example.evaluacionnacional.ui.slideshow;

public class Contacto {
    private String name;      // Nombre del contacto
    private String email;     // Correo electrónico del contacto
    private String photoUrl;  // URL de la foto del contacto

    // Constructor vacío necesario para Firestore (base de datos)
    public Contacto() {
        // Firestore requiere un constructor vacío para deserializar los datos
    }

    // Constructor que inicializa un contacto con nombre, correo y URL de foto
    public Contacto(String name, String email, String photoUrl) {
        this.name = name;       // Asigna el nombre del contacto
        this.email = email;     // Asigna el correo del contacto
        this.photoUrl = photoUrl; // Asigna la URL de la foto del contacto
    }

    // Getter para obtener el nombre del contacto
    public String getName() {
        return name;  // Devuelve el nombre del contacto
    }

    // Setter para modificar el nombre del contacto
    public void setName(String name) {
        this.name = name;  // Asigna un nuevo nombre al contacto
    }

    // Getter para obtener el correo del contacto
    public String getEmail() {
        return email;  // Devuelve el correo electrónico del contacto
    }

    // Setter para modificar el correo del contacto
    public void setEmail(String email) {
        this.email = email;  // Asigna un nuevo correo electrónico al contacto
    }

    // Getter para obtener la URL de la foto del contacto
    public String getPhotoUrl() {
        return photoUrl;  // Devuelve la URL de la foto del contacto
    }

    // Setter para modificar la URL de la foto del contacto
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;  // Asigna una nueva URL de foto al contacto
    }
}
