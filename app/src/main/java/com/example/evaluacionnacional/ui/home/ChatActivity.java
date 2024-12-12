package com.example.evaluacionnacional.ui.home;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.evaluacionnacional.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private MqttClient mqttClient;
    private String topic;
    private String userEmail;
    private String contactEmail;
    private List<Message> messages;
    private MessagesAdapter messagesAdapter;

    private static final String BROKER_URL = "tcp://broker.hivemq.com:1883";
    private static final String PASSWORD = "contraseña"; // Esto debe ser dinámico según el usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Obtener los correos electrónicos de usuario y contacto desde los extras del intent
        userEmail = getIntent().getStringExtra("userEmail");
        contactEmail = getIntent().getStringExtra("contactEmail");

        // Crear el tópico en formato "chat/{email_usuario}/{email_contacto}"
        topic = "chat/" + userEmail + "/" + contactEmail;

        // Inicializar la lista y el adaptador de mensajes
        messages = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(messages);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messagesAdapter);

        // Conectar al broker MQTT y suscribirse al tópico
        connectToMqttBroker(topic);

        // Configurar el botón para enviar mensajes
        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> sendMessage());
    }

    // Método para conectar al broker MQTT y suscribirse a un tópico
    private void connectToMqttBroker(String topic) {
        try {
            mqttClient = new MqttClient(BROKER_URL, MqttClient.generateClientId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(userEmail); // Usar el correo del usuario como nombre de usuario
            options.setPassword(PASSWORD.toCharArray());
            options.setCleanSession(true);

            // Conectar al broker MQTT
            mqttClient.connect(options);

            // Suscribirse al tópico del chat
            mqttClient.subscribe(topic);

            // Configurar el callback para recibir mensajes
            mqttClient.setCallback(new org.eclipse.paho.client.mqttv3.MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Toast.makeText(ChatActivity.this, "Conexión perdida", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Procesar el mensaje recibido
                    String msg = new String(message.getPayload());
                    Message newMessage = new Message(msg, contactEmail, System.currentTimeMillis());

                    // Actualizar la lista de mensajes en el hilo principal
                    runOnUiThread(() -> {
                        messages.add(newMessage);
                        messagesAdapter.notifyItemInserted(messages.size() - 1);
                    });
                }

                @Override
                public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {
                    // Confirmación de entrega exitosa
                }
            });

        } catch (MqttException e) {
            Toast.makeText(this, "Error al conectar al broker MQTT", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para enviar un mensaje al tópico MQTT
    private void sendMessage() {
        String message = ((EditText) findViewById(R.id.editTextMessage)).getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Publicar el mensaje en el tópico
            mqttClient.publish(topic, new MqttMessage(message.getBytes()));

            // Limpiar el campo de texto
            ((EditText) findViewById(R.id.editTextMessage)).setText("");

            // Agregar el mensaje enviado a la lista
            Message sentMessage = new Message(message, "Tú", System.currentTimeMillis());
            messages.add(sentMessage);
            messagesAdapter.notifyItemInserted(messages.size() - 1);

            // Desplazar el RecyclerView al último mensaje
            RecyclerView recyclerView = findViewById(R.id.recyclerViewMessages);
            recyclerView.scrollToPosition(messages.size() - 1);
        } catch (MqttException e) {
            Toast.makeText(this, "No se pudo enviar el mensaje", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (MqttException e) {
            Toast.makeText(this, "Error al desconectar", Toast.LENGTH_SHORT).show();
        }
    }
}