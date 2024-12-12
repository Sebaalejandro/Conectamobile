package com.example.evaluacionnacional;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;  // Código para la solicitud de inicio de sesión con Google
    private static final String TAG = "GoogleSignIn";  // Etiqueta para loguear información de Google SignIn

    private FirebaseAuth auth;  // Instancia de FirebaseAuth para gestionar la autenticación
    private GoogleSignInClient googleSignInClient;  // Cliente para Google SignIn

    // Campos para el formulario de login con correo y contraseña
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In con opciones de solicitud de email y token de ID
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.cliente_id))  // Cliente ID desde strings.xml
                .requestEmail()  // Solicitar email del usuario
                .build();

        // Crear el cliente de Google SignIn con las opciones configuradas
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Inicializa los campos de entrada para email y contraseña
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // Verificar si el usuario ya está autenticado
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            goToHomeActivity();  // Si el usuario ya está autenticado, redirigir a la pantalla principal
        }

        // Configurar el evento click para el botón de login con correo y contraseña
        TextView loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> loginUserWithEmailPassword());

        // Configurar el evento click para redirigir a la pantalla de registro
        TextView registerTextView = findViewById(R.id.registerTextView);
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, registroApp.class);
            startActivity(intent);  // Redirige al usuario a la actividad de registro
        });

        // Configurar el evento click para redirigir a la pantalla de recuperación de contraseña
        TextView restablecer = findViewById(R.id.forgotPasswordTextView);
        restablecer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, recuperacionCon.class);
            startActivity(intent);  // Redirige al usuario a la actividad de recuperación de contraseña
        });
    }

    // Método para iniciar sesión con Google
    private void signInWithGoogle() {
        // Crear la intención de inicio de sesión con Google
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);  // Iniciar la actividad para obtener los datos del usuario
    }

    // Manejar el resultado de la solicitud de inicio de sesión con Google
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                // Obtener los datos de la cuenta de Google
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);  // Autenticar con Firebase usando las credenciales de Google
                }
            } catch (ApiException e) {
                // En caso de error al intentar autenticar con Google
                Toast.makeText(this, "Error: No se pudo autenticar con Google.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Autenticar con Firebase usando las credenciales obtenidas de Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        goToHomeActivity();  // Si la autenticación es exitosa, redirigir a la pantalla principal
                    } else {
                        // Si la autenticación falla, mostrar un mensaje de error
                        Toast.makeText(this, "Error: No se pudo autenticar en Firebase.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para iniciar sesión con correo y contraseña
    private void loginUserWithEmailPassword() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
            return;  // Si los campos están vacíos, no continuar
        }

        // Intentar iniciar sesión con Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        goToHomeActivity();  // Si el inicio de sesión es exitoso, redirigir a la pantalla principal
                    } else {
                        // Si ocurre un error durante el inicio de sesión, mostrar el mensaje correspondiente
                        Toast.makeText(MainActivity.this, "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Redirigir a la HomeActivity después de un inicio de sesión exitoso
    private void goToHomeActivity() {
        Intent intent = new Intent(MainActivity.this, Homelogin.class);
        startActivity(intent);  // Iniciar la actividad de inicio
        finish();  // Finalizar la actividad actual (login)
    }
}
