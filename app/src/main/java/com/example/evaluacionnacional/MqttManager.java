package com.example.evaluacionnacional;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

public class MqttManager {

    private static final String TAG = "MqttManager";  // Etiqueta para los logs
    private MqttClient client;  // Cliente MQTT para conectar al broker
    private String brokerUrl = "tcp://broker.hivemq.com:1883";  // URL del broker MQTT
    private String clientId = MqttClient.generateClientId();  // ID único del cliente MQTT
    private MqttConnectOptions options;  // Opciones para la conexión MQTT
    private Context context;  // Contexto de la aplicación

    // Constructor que recibe el contexto para inicializar el cliente MQTT
    public MqttManager(Context context) {
        this.context = context;
    }

    // Método para conectar al broker MQTT
    public void connect() {
        try {
            // Crear el cliente MQTT y establecer las opciones de conexión
            client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setCleanSession(false);  // Usar sesión persistente
            options.setConnectionTimeout(10);  // Establecer el tiempo de espera para la conexión (en segundos)
            options.setKeepAliveInterval(60);  // Intervalo para mantener la conexión viva (en segundos)

            // Establecer el callback para manejar los eventos de la conexión
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // Si la conexión se pierde, intentar reconectar
                    Log.d(TAG, "Conexión perdida: " + cause.getMessage());
                    reconnect();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Aquí se procesan los mensajes recibidos
                    // Puedes agregar código adicional para manejar el contenido del mensaje
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Este método se llama cuando el mensaje se ha entregado con éxito
                    Log.d(TAG, "Mensaje entregado con éxito: " + token.getMessageId());
                }
            });

            // Intentar conectar al broker MQTT
            client.connect(options);
            Log.d(TAG, "Conectado al broker MQTT");

        } catch (Exception e) {
            // Si ocurre un error durante la conexión, se muestra un mensaje de error
            Log.e(TAG, "Error al conectar con el broker MQTT", e);
        }
    }

    // Método para desconectar del broker MQTT
    public void disconnect() {
        try {
            // Desconectar solo si el cliente está conectado
            if (client != null && client.isConnected()) {
                client.disconnect();
                Log.d(TAG, "Desconectado del broker");
            }
        } catch (Exception e) {
            // Manejar errores durante la desconexión
            Log.e(TAG, "Error al desconectar del broker", e);
        }
    }

    // Método para suscribirse a un tópico MQTT
    public void subscribeToTopic(String topic) {
        try {
            // Suscribirse al tópico solo si el cliente está conectado
            if (client != null && client.isConnected()) {
                client.subscribe(topic);
                Log.d(TAG, "Suscrito al tópico: " + topic);
            }
        } catch (Exception e) {
            // Si ocurre un error al suscribirse, se muestra un mensaje de error
            Log.e(TAG, "Error al suscribirse al tópico " + topic, e);
        }
    }

    // Método para publicar un mensaje en un tópico MQTT
    public void publishMessage(String topic, String messageContent, String senderEmail, String timestamp) {
        try {
            // Verificar si el cliente está conectado antes de publicar el mensaje
            if (client != null && client.isConnected()) {
                // Crear el mensaje JSON con el contenido, el remitente y la marca de tiempo
                JSONObject message = new JSONObject();
                message.put("message", messageContent);
                message.put("sender", senderEmail);
                message.put("timestamp", timestamp);

                // Convertir el mensaje JSON a un array de bytes para enviarlo
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setPayload(message.toString().getBytes());

                Log.d(TAG, "Publicando mensaje: " + message.toString());
                // Publicar el mensaje en el tópico especificado
                client.publish(topic, mqttMessage);
            } else {
                // Si el cliente no está conectado, intentar reconectar y luego reintentar la publicación
                Log.e(TAG, "El cliente no está conectado. Intentando reconectar...");
                reconnect();
                publishMessage(topic, messageContent, senderEmail, timestamp);
            }
        } catch (Exception e) {
            // Manejar errores durante la publicación del mensaje
            Log.e(TAG, "Error al enviar el mensaje", e);
        }
    }

    // Método para intentar reconectar automáticamente si la conexión se pierde
    private void reconnect() {
        if (client != null && !client.isConnected()) {
            try {
                Log.d(TAG, "Intentando reconectar...");
                client.connect(options);  // Intentar reconectar al broker MQTT
                Log.d(TAG, "Reconectado al broker MQTT");
            } catch (Exception e) {
                // Si ocurre un error al intentar reconectar, se muestra un mensaje de error
                Log.e(TAG, "Error al reconectar con el broker MQTT", e);
            }
        }
    }

    // Método para configurar un callback personalizado que maneja los mensajes entrantes
    public void setCallback(MqttCallback callback) {
        if (client != null) {
            client.setCallback(callback);  // Establecer el callback para manejar mensajes
        }
    }

    // Implementación por defecto del callback para manejar los mensajes entrantes
    public class DefaultMqttCallback implements MqttCallback {

        @Override
        public void connectionLost(Throwable cause) {
            // Si la conexión se pierde, intentar reconectar
            Log.d(TAG, "Conexión perdida: " + cause.getMessage());
            reconnect();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            // Recibir y procesar el mensaje que llega al tópico
            String messageContent = new String(message.getPayload());
            Log.d(TAG, "Mensaje recibido en el tópico " + topic + ": " + messageContent);

            // Aquí puedes agregar código adicional para manejar el mensaje recibido
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            // Mensaje entregado con éxito
            Log.d(TAG, "Mensaje entregado con éxito: " + token.getMessageId());
        }
    }
}
