package com.example.evaluacionnacional.ui.home;

// Esta clase representa un mensaje con contenido, remitente y marca temporal (timestamp).
public class Message {

    // Atributos privados de la clase
    private String content;   // El contenido del mensaje (texto del mensaje).
    private String sender;    // El remitente del mensaje (quién envió el mensaje).
    private long timestamp;   // La marca temporal del mensaje (fecha y hora de envío).

    // Constructor para inicializar un mensaje con los parámetros proporcionados
    public Message(String content, String sender, long timestamp) {
        this.content = content;   // Asigna el contenido del mensaje
        this.sender = sender;     // Asigna el remitente del mensaje
        this.timestamp = timestamp; // Asigna la marca temporal
    }

    // Método para obtener el contenido del mensaje
    public String getContent() {
        return content;  // Devuelve el contenido del mensaje
    }

    // Método para obtener el remitente del mensaje
    public String getSender() {
        return sender;   // Devuelve el remitente del mensaje
    }

    // Método para obtener la marca temporal del mensaje
    public long getTimestamp() {
        return timestamp; // Devuelve la marca temporal
    }
}
