package com.example.evaluacionnacional;

public class User {

    // Atributos privados para almacenar la información del usuario
    private String name;  // Nombre del usuario
    private String email;  // Correo electrónico del usuario
    private String photoUrl;  // URL de la foto de perfil del usuario

    // Constructor para inicializar los atributos del usuario
    public User(String name, String email, String photoUrl) {
        this.name = name;  // Asignar el nombre proporcionado
        this.email = email;  // Asignar el correo proporcionado
        this.photoUrl = photoUrl;  // Asignar la URL de la foto proporcionada
    }

    // Getters y setters para acceder y modificar los atributos del usuario

    // Obtener el nombre del usuario
    public String getName() {
        return name;
    }

    // Establecer el nombre del usuario
    public void setName(String name) {
        this.name = name;
    }

    // Obtener el correo electrónico del usuario
    public String getEmail() {
        return email;
    }

    // Establecer el correo electrónico del usuario
    public void setEmail(String email) {
        this.email = email;
    }

    // Obtener la URL de la foto del usuario
    public String getPhotoUrl() {
        return photoUrl;
    }

    // Establecer la URL de la foto del usuario
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
