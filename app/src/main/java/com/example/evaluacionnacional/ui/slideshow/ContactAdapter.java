package com.example.evaluacionnacional.ui.slideshow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.evaluacionnacional.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contacto> contactList;  // Lista de contactos a mostrar
    private OnContactClickListener listener;  // Listener para manejar los clics en los elementos

    // Constructor del adaptador que recibe una lista de contactos
    public ContactAdapter(List<Contacto> contactList) {
        this.contactList = contactList;  // Inicializa la lista de contactos
    }

    // Método para establecer el listener de clics en los contactos
    public void setOnContactClickListener(OnContactClickListener listener) {
        this.listener = listener;  // Establece el listener recibido
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de un contacto en la lista
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false);
        return new ContactViewHolder(view);  // Devuelve un nuevo ViewHolder con la vista inflada
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        // Obtiene el contacto en la posición actual y lo vincula al ViewHolder
        Contacto contact = contactList.get(position);
        holder.bind(contact);  // Asocia los datos del contacto al ViewHolder
    }

    @Override
    public int getItemCount() {
        return contactList.size();  // Devuelve el número de contactos en la lista
    }

    // Clase interna ViewHolder para los elementos de la lista de contactos
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;  // Vista para mostrar el nombre del contacto
        private TextView emailTextView;  // Vista para mostrar el correo del contacto
        private ImageView photoImageView;  // Vista para mostrar la foto del contacto

        // Constructor del ViewHolder que inicializa las vistas
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contactName);  // Inicializa el TextView para el nombre
            emailTextView = itemView.findViewById(R.id.contactEmail);  // Inicializa el TextView para el correo
            photoImageView = itemView.findViewById(R.id.contactPhoto);  // Inicializa el ImageView para la foto

            // Configura el clic en el ítem de la lista
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onContactClick(contactList.get(getAdapterPosition()));  // Llama al listener si está configurado
                }
            });
        }

        // Método para vincular los datos del contacto a las vistas
        public void bind(Contacto contact) {
            nameTextView.setText(contact.getName());  // Asocia el nombre del contacto al TextView
            emailTextView.setText(contact.getEmail());  // Asocia el correo del contacto al TextView

            // Carga la imagen del contacto utilizando Picasso
            if (contact.getPhotoUrl() != null && !contact.getPhotoUrl().isEmpty()) {
                Picasso.get()
                        .load(contact.getPhotoUrl())  // Carga la imagen desde la URL
                        .placeholder(R.drawable.ic_launcher_foreground)  // Imagen por defecto mientras carga
                        .into(photoImageView);  // Establece la imagen en el ImageView
            } else {
                photoImageView.setImageResource(R.drawable.ic_launcher_foreground);  // Imagen por defecto si no hay URL
            }
        }
    }

    // Interfaz para manejar los clics en los contactos
    public interface OnContactClickListener {
        void onContactClick(Contacto contact);  // Método para manejar el clic en un contacto
    }
}
