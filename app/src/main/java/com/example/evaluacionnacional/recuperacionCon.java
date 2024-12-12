package com.example.evaluacionnacional;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class recuperacionCon extends AppCompatActivity {

    private FirebaseAuth mAuth;  // Instancia de FirebaseAuth para manejar la autenticación
    private EditText emailEditText;  // Campo de texto para ingresar el correo electrónico
    private Button sendButton;  // Botón para enviar la solicitud de restablecimiento de contraseña
    private TextView backToLoginTextView;  // Texto que permite al usuario volver a la pantalla de inicio de sesión

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacion_con);

        // Inicialización de FirebaseAuth para poder interactuar con el sistema de autenticación
        mAuth = FirebaseAuth.getInstance();

        // Asociar las vistas con las variables correspondientes
        emailEditText = findViewById(R.id.emailEditText);
        sendButton = findViewById(R.id.sendButton);
        backToLoginTextView = findViewById(R.id.backToLoginTextView);

        // Configuración del botón de "Enviar" para que ejecute el proceso de envío del correo de restablecimiento
        sendButton.setOnClickListener(v -> sendPasswordResetEmail());

        // Configuración de la opción "Volver a iniciar sesión" que permite cerrar la actividad actual
        backToLoginTextView.setOnClickListener(v -> {
            // El comportamiento aquí es simplemente cerrar esta actividad y regresar a la anterior
            finish();
        });
    }

    // Método que maneja el proceso de envío de un correo de restablecimiento de contraseña
    private void sendPasswordResetEmail() {
        String email = emailEditText.getText().toString().trim();  // Obtener el correo ingresado

        // Validar si el correo ingresado no está vacío
        if (email.isEmpty()) {
            emailEditText.setError("Por favor ingresa un correo electrónico");  // Mostrar error si está vacío
            return;
        }

        // Validar que el correo contenga un "@" para asegurarse de que es un formato válido
        if (!email.contains("@")) {
            emailEditText.setError("Por favor ingresa un correo electrónico válido");  // Mostrar error si no es válido
            return;
        }

        // Enviar el correo de restablecimiento de contraseña a través de Firebase
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Si la operación fue exitosa, mostrar un mensaje de confirmación al usuario
                        Toast.makeText(recuperacionCon.this, "Correo de restablecimiento enviado", Toast.LENGTH_SHORT).show();
                        finish();  // Cerrar la actividad o redirigir a la pantalla de inicio de sesión
                    } else {
                        // Si la operación falla, mostrar el mensaje de error
                        Toast.makeText(recuperacionCon.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
