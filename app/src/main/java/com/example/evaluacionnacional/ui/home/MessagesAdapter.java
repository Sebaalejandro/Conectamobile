package com.example.evaluacionnacional.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.evaluacionnacional.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Message> messages; // Lista de mensajes

    // Constructor para inicializar la lista
    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Infla el layout del mensaje
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        // Vincula los datos del mensaje a las vistas
        Message message = messages.get(position);
        holder.messageContent.setText(message.getContent());
        holder.sender.setText(message.getSender());

        // Formatea y muestra la hora
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.timestamp.setText(sdf.format(message.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return messages.size(); // Tamaño de la lista
    }

    // ViewHolder para manejar las vistas de cada mensaje
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent, sender, timestamp;

        public MessageViewHolder(View itemView) {
            super(itemView);
            // Inicializa los componentes
            messageContent = itemView.findViewById(R.id.textViewMessageContent);
            sender = itemView.findViewById(R.id.textViewSender);
            timestamp = itemView.findViewById(R.id.textViewTimestamp);
        }
    }
}
