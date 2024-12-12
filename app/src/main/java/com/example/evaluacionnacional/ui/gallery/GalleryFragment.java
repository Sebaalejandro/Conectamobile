package com.example.evaluacionnacional.ui.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.evaluacionnacional.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class GalleryFragment extends Fragment {

    private FirebaseAuth mAuth;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflar el diseño para este fragmento
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        // Inicializar FirebaseAuth y FirebaseStorage
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Obtener referencias a los elementos de la interfaz
        EditText userNameEditText = root.findViewById(R.id.userNameEditText);
        EditText userEmailEditText = root.findViewById(R.id.userEmailEditText);
        ImageView profileImageView = root.findViewById(R.id.profileImageView);
        Button confirmChangesButton = root.findViewById(R.id.confirmChangesButton);
        Button selectPhotoButton = root.findViewById(R.id.selectPhotoButton);

        // Obtener información del usuario autenticado
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Mostrar el nombre de usuario en el campo de texto
            String displayName = user.getDisplayName();
            userNameEditText.setText(displayName != null && !displayName.isEmpty() ? displayName : "Nombre de Usuario");

            // Mostrar el correo electrónico en el campo de texto
            String email = user.getEmail();
            userEmailEditText.setText(email);

            // Cargar la foto de perfil del usuario usando Picasso
            String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
            if (photoUrl != null) {
                Picasso.get()
                        .load(photoUrl)
                        .placeholder(R.drawable.usuario)  // Imagen por defecto mientras se carga
                        .error(R.drawable.usuario)  // Imagen en caso de error
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.usuario); // Imagen predeterminada
            }

            // Configurar el botón para guardar los cambios
            confirmChangesButton.setOnClickListener(v -> {
                String newName = userNameEditText.getText().toString();
                String newEmail = userEmailEditText.getText().toString();
                confirmChanges(user, newName, newEmail);
            });

            // Configurar el botón para seleccionar una nueva foto
            selectPhotoButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            });
        } else {
            // Mostrar un mensaje si no hay un usuario autenticado
            Toast.makeText(getActivity(), "No hay usuario autenticado", Toast.LENGTH_SHORT).show();
        }

        return root;
    }

    // Método para actualizar el perfil del usuario
    private void confirmChanges(FirebaseUser user, String newName, String newEmail) {
        // Actualizar el nombre de usuario
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Nombre actualizado", Toast.LENGTH_SHORT).show();
                    }
                });

        // Actualizar el correo electrónico
        user.updateEmail(newEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Correo actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Error al actualizar correo", Toast.LENGTH_SHORT).show();
            }
        });

        // Subir nueva foto de perfil si se seleccionó
        if (selectedImageUri != null) {
            uploadImageToFirebase(user, selectedImageUri);
        }
    }

    // Método para subir una imagen al almacenamiento de Firebase
    private void uploadImageToFirebase(FirebaseUser user, Uri imageUri) {
        StorageReference fileReference = storageReference.child("profile_pics/" + user.getUid() + ".jpg");

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build();

                    user.updateProfile(profileUpdates).addOnCompleteListener(profileTask -> {
                        if (profileTask.isSuccessful()) {
                            Toast.makeText(getActivity(), "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                        }
                    });
                }))
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al subir la imagen", Toast.LENGTH_SHORT).show());
    }

    // Manejar el resultado de la selección de imagen
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ImageView profileImageView = getView().findViewById(R.id.profileImageView);
            profileImageView.setImageURI(selectedImageUri); // Mostrar la imagen seleccionada
        }
    }
}
