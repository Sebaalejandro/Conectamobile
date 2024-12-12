package com.example.evaluacionnacional.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evaluacionnacional.MqttManager;
import com.example.evaluacionnacional.R;
import com.google.firebase.auth.FirebaseAuth;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento que maneja la funcionalidad principal del chat, incluyendo la interfaz de usuario
 * y la conexión con MQTT para enviar y recibir mensajes.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewChats; // RecyclerView para mostrar los mensajes del chat.
    private EditText editTextMessage;      // Campo de entrada para escribir mensajes.
    private Button buttonSend;             // Botón para enviar mensajes.
    private ChatAdapter chatAdapter;       // Adaptador para manejar los mensajes en el RecyclerView.
    private List<ChatModel> chatList;      // Lista de mensajes del chat.
    private MqttManager mqttManager;       // Administrador para la conexión y manejo de MQTT.
    private FirebaseAuth auth;             // Instancia de autenticación Firebase.

    /**
     * Inflar el diseño del fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * Configurar los elementos de la vista después de que se ha creado.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar el RecyclerView y su configuración.
        recyclerViewChats = view.findViewById(R.id.recyclerViewChats);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar los campos de entrada y el botón de envío.
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);

        // Configurar la lista y el adaptador para los mensajes del chat.
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);
        recyclerViewChats.setAdapter(chatAdapter);

        // Inicializar la autenticación de Firebase y el administrador MQTT.
        auth = FirebaseAuth.getInstance();
        mqttManager = new MqttManager(getContext());

        // Conectar al servidor MQTT.
        mqttManager.connect();
        mqttManager.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Manejar mensajes entrantes desde MQTT.
                onMqttMessageReceived(message.toString());
            }

            @Override
            public void connectionLost(Throwable cause) {
                // Manejar reconexión si se pierde la conexión.
                reconnectToMqtt();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Evento opcional: manejo de confirmación de entrega.
            }
        });

        // Suscribirse al tópico MQTT para mensajes generales.
        mqttManager.subscribeToTopic("Topico/General");

        // Configurar el evento de clic del botón de enviar.
        buttonSend.setOnClickListener(v -> sendMessage());
    }

    /**
     * Método para enviar un mensaje al servidor MQTT.
     */
    private void sendMessage() {
        String messageContent = editTextMessage.getText().toString().trim();
        if (messageContent.isEmpty()) {
            Toast.makeText(getContext(), "Escribe un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserEmail = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : "unknown";
        String timestamp = String.valueOf(System.currentTimeMillis());

        try {
            // Crear el mensaje en formato JSON.
            JSONObject messageJson = new JSONObject();
            messageJson.put("message", messageContent);
            messageJson.put("sender", currentUserEmail);
            messageJson.put("timestamp", timestamp);

            // Publicar el mensaje en MQTT.
            mqttManager.publishMessage("Topico/General", messageContent, currentUserEmail, timestamp);

            // Agregar el mensaje a la lista local y actualizar el RecyclerView.
            ChatModel chat = new ChatModel(messageContent, currentUserEmail, timestamp);
            chatList.add(chat);
            chatAdapter.notifyItemInserted(chatList.size() - 1);
            recyclerViewChats.scrollToPosition(chatList.size() - 1);

            // Limpiar el campo de texto.
            editTextMessage.setText("");
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al enviar el mensaje", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Manejar mensajes entrantes desde MQTT.
     */
    private void onMqttMessageReceived(String messageContent) {
        try {
            JSONObject jsonMessage = new JSONObject(messageContent);
            String senderEmail = jsonMessage.getString("sender");
            String message = jsonMessage.getString("message");
            String timestamp = jsonMessage.getString("timestamp");

            ChatModel chat = new ChatModel(message, senderEmail, timestamp);

            // Actualizar la interfaz de usuario en el hilo principal.
            getActivity().runOnUiThread(() -> {
                chatList.add(chat);
                chatAdapter.notifyItemInserted(chatList.size() - 1);
                recyclerViewChats.scrollToPosition(chatList.size() - 1);
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al procesar el mensaje", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Reconectar automáticamente al servidor MQTT en caso de pérdida de conexión.
     */
    private void reconnectToMqtt() {
        Toast.makeText(getContext(), "Conexión perdida, intentando reconectar...", Toast.LENGTH_SHORT).show();
        mqttManager.connect();
    }

    /**
     * Desconectar del servidor MQTT cuando se destruye la vista.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mqttManager.disconnect();
    }
}
