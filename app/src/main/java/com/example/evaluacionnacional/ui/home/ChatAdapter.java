package com.example.evaluacionnacional.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evaluacionnacional.R;

import java.util.List;

/**
 * Adapter para manejar y mostrar una lista de mensajes de chat en un RecyclerView.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatModel> chatList; // Lista de mensajes del chat.

    /**
     * Constructor para inicializar la lista de mensajes.
     *
     * @param chatList Lista de mensajes del chat.
     */
    public ChatAdapter(List<ChatModel> chatList) {
        this.chatList = chatList;
    }

    /**
     * Inflar la vista del ítem de chat para el RecyclerView.
     *
     * @param parent   Vista padre donde se agregará el ítem inflado.
     * @param viewType Tipo de vista (no utilizado en este caso).
     * @return ChatViewHolder con la vista inflada.
     */
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    /**
     * Vincular los datos de un mensaje de chat a un ViewHolder.
     *
     * @param holder   ViewHolder donde se mostrarán los datos.
     * @param position Posición del mensaje en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatModel chat = chatList.get(position);
        holder.textViewSender.setText(chat.getSenderEmail()); // Mostrar el remitente del mensaje.
        holder.textViewMessageContent.setText(chat.getMessageContent()); // Mostrar el contenido del mensaje.
        holder.textViewTimestamp.setText(chat.getTimestamp()); // Mostrar la marca de tiempo del mensaje.
    }

    /**
     * Obtener el número total de elementos en la lista.
     *
     * @return Cantidad de mensajes en la lista.
     */
    @Override
    public int getItemCount() {
        return chatList.size();
    }

    /**
     * ViewHolder para representar un mensaje de chat en el RecyclerView.
     */
    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView textViewSender, textViewMessageContent, textViewTimestamp;

        /**
         * Constructor para inicializar los elementos de la vista del mensaje.
         *
         * @param itemView Vista del ítem de chat.
         */
        public ChatViewHolder(View itemView) {
            super(itemView);
            textViewSender = itemView.findViewById(R.id.textViewSender); // TextView para el remitente.
            textViewMessageContent = itemView.findViewById(R.id.textViewMessageContent); // TextView para el contenido del mensaje.
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp); // TextView para la marca de tiempo.
        }
    }
}
