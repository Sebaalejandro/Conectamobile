package com.example.evaluacionnacional.ui.home;

public class Chat {
    private String partnerName; // Nombre de la persona con la que se chatea
    private String topic; // Tema del chat
    private String currentUserEmail; // Correo del usuario actual
    private String partnerEmail; // Correo de la persona con la que se chatea

    // Constructor
    public Chat(String partnerName, String topic, String currentUserEmail, String partnerEmail) {
        this.partnerName = partnerName; // Asigna el nombre del compañero
        this.topic = topic; // Asigna el tema del chat
        this.currentUserEmail = currentUserEmail; // Asigna el correo del usuario actual
        this.partnerEmail = partnerEmail; // Asigna el correo del compañero
    }

    // Métodos Getter y Setter
    public String getPartnerName() {
        return partnerName; // Obtiene el nombre del compañero
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName; // Establece el nombre del compañero
    }

    public String getTopic() {
        return topic; // Obtiene el tema del chat
    }

    public void setTopic(String topic) {
        this.topic = topic; // Establece el tema del chat
    }

    public String getCurrentUserEmail() {
        return currentUserEmail; // Obtiene el correo del usuario actual
    }

    public void setCurrentUserEmail(String currentUserEmail) {
        this.currentUserEmail = currentUserEmail; // Establece el correo del usuario actual
    }

    public String getPartnerEmail() {
        return partnerEmail; // Obtiene el correo del compañero
    }

    public void setPartnerEmail(String partnerEmail) {
        this.partnerEmail = partnerEmail; // Establece el correo del compañero
    }
}
