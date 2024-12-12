package com.example.evaluacionnacional;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class registroApp extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Código de solicitud para seleccionar una imagen
    private static final int MAX_IMAGE_SIZE = 1024 * 1024; // Tamaño máximo de la imagen (1 MB)

    private ImageView profileImageView;  // Vista para mostrar la imagen de perfil seleccionada
    private Uri profileImageUri;  // URI de la imagen de perfil seleccionada

    private EditText emailEditText, passwordEditText, confirmPasswordEditText, usernameEditText; // Campos de texto para capturar los datos del usuario
    private Button registerButton, selectImageButton;  // Botones para registrar al usuario y seleccionar la imagen de perfil

    private FirebaseAuth mAuth;  // Instancia de FirebaseAuth para manejar la autenticación de usuarios
    private StorageReference storageReference;  // Referencia para interactuar con Firebase Storage y almacenar la imagen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_app);

        // Inicialización de FirebaseAuth y FirebaseStorage
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Asociar las vistas a los elementos de la interfaz de usuario
        profileImageView = findViewById(R.id.profileImageView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        usernameEditText = findViewById(R.id.nameEditText);
        registerButton = findViewById(R.id.registerButton);
        selectImageButton = findViewById(R.id.selectImageButton);

        // Configurar los eventos de los botones
        selectImageButton.setOnClickListener(v -> openGallery());  // Abrir la galería para seleccionar una imagen
        registerButton.setOnClickListener(v -> registerUser());  // Ejecutar el registro del usuario cuando se presiona el botón
    }

    // Método para abrir la galería y permitir al usuario seleccionar una imagen de perfil
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);  // Iniciar la actividad para seleccionar una imagen
    }

    // Manejo del resultado de la selección de la imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileImageUri = data.getData();  // Obtener la URI de la imagen seleccionada

            // Validar tipo y tamaño de la imagen seleccionada
            try {
                InputStream inputStream = getContentResolver().openInputStream(profileImageUri);
                if (inputStream != null) {
                    int fileSize = inputStream.available();  // Obtener el tamaño de la imagen
                    inputStream.close();

                    String mimeType = getContentResolver().getType(profileImageUri);  // Verificar el tipo MIME de la imagen
                    if (!"image/jpeg".equals(mimeType)) {
                        Toast.makeText(this, "La imagen debe ser JPG", Toast.LENGTH_SHORT).show();  // Mensaje si no es una imagen JPG
                        profileImageUri = null;
                        return;
                    }

                    if (fileSize > MAX_IMAGE_SIZE) {
                        Toast.makeText(this, "La imagen debe ser menor a 1 MB", Toast.LENGTH_SHORT).show();  // Mensaje si la imagen excede el tamaño máximo
                        profileImageUri = null;
                        return;
                    }

                    profileImageView.setImageURI(profileImageUri);  // Mostrar la imagen seleccionada en la vista
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();  // Mostrar error si ocurre una excepción
                profileImageUri = null;
            }
        }
    }

    // Método para registrar al usuario
    private void registerUser() {
        String email = emailEditText.getText().toString().trim();  // Obtener el correo electrónico ingresado
        String password = passwordEditText.getText().toString().trim();  // Obtener la contraseña ingresada
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();  // Obtener la confirmación de la contraseña
        String username = usernameEditText.getText().toString().trim();  // Obtener el nombre de usuario

        // Validaciones de campos
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.endsWith("@gmail.com")) {
            emailEditText.setError("Ingresa un correo válido de Gmail");  // Verificar si el correo es válido
            return;
        }

        if (TextUtils.isEmpty(password) || !password.matches("^(?=.*[A-Z])(?=.*[@#$%^&+=!]).{6,}$")) {
            passwordEditText.setError("La contraseña debe tener al menos 6 caracteres, una mayúscula y un carácter especial");  // Validar la contraseña
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Las contraseñas no coinciden");  // Verificar que las contraseñas coincidan
            return;
        }

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Ingresa un nombre de usuario");  // Verificar que se ingrese un nombre de usuario
            return;
        }

        if (profileImageUri == null) {
            Toast.makeText(this, "Selecciona una foto de perfil válida", Toast.LENGTH_SHORT).show();  // Validar que se haya seleccionado una imagen de perfil
            return;
        }

        // Registrar usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        uploadImageToFirebase(username);  // Subir la imagen y los datos del usuario
                    } else {
                        Toast.makeText(this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();  // Mostrar mensaje de error
                    }
                });
    }

    // Método para subir la imagen de perfil a Firebase Storage
    private void uploadImageToFirebase(String username) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Crear una referencia única para la imagen del usuario usando su UID
            StorageReference fileReference = storageReference.child("profile_pics/" + user.getUid() + ".jpg");

            // Subir la imagen seleccionada
            fileReference.putFile(profileImageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Actualizar el perfil del usuario con la URL de la imagen subida
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .setPhotoUri(uri)
                                .build();

                        user.updateProfile(profileUpdates).addOnCompleteListener(profileTask -> {
                            if (profileTask.isSuccessful()) {
                                saveUserDataToFirestore(user.getUid(), username, user.getEmail(), uri.toString());  // Guardar los datos del usuario en Firestore
                            }
                        });
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show());  // Manejar el error al subir la imagen
        }
    }

    // Método para guardar los datos del usuario en Firestore
    private void saveUserDataToFirestore(String uid, String username, String email, String photoUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Crear un tópico único para el usuario basado en su correo
        String userTopic = "chat/user_" + email.hashCode();

        // Crear un mapa con los datos del usuario
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("photoUrl", photoUrl);
        userData.put("topic", userTopic); // Agregar el tópico generado

        // Guardar los datos del usuario en Firestore
        db.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));  // Redirigir al usuario a la actividad principal
                    finish();  // Cerrar la actividad de registro
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar los datos en Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show()  // Mostrar mensaje de error si falla el guardado en Firestore
                );
    }
}
