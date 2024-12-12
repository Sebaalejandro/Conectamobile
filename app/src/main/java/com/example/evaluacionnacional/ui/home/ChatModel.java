package com.example.evaluacionnacional.ui.home;

/**
 * Modelo que representa un mensaje de chat.
 * Contiene información sobre el contenido del mensaje, el remitente y la marca de tiempo.
 */
public class ChatModel {

    private String messageContent; // Contenido del mensaje de chat.
    private String senderEmail;    // Correo electrónico del remitente del mensaje.
    private String timestamp;      // Marca de tiempo del mensaje.

    /**
     * Constructor para inicializar el modelo de chat.
     *
     * @param messageContent Contenido del mensaje.
     * @param senderEmail    Correo electrónico del remitente.
     * @param timestamp      Marca de tiempo del mensaje.
     */
    public ChatModel(String messageContent, String senderEmail, String timestamp) {
        this.messageContent = messageContent;
        this.senderEmail = senderEmail;
        this.timestamp = timestamp;
    }

    /**
     * Obtener el contenido del mensaje.
     *
     * @return El contenido del mensaje.
     */
    public String getMessageContent() {
        return messageContent;
    }

    /**
     * Obtener el correo electrónico del remitente.
     *
     * @return El correo electrónico del remitente.
     */
    public String getSenderEmail() {
        return senderEmail;
    }

    /**
     * Obtener la marca de tiempo del mensaje.
     *
     * @return La marca de tiempo del mensaje.
     */
    public String getTimestamp() {
        return timestamp;
    }
}
