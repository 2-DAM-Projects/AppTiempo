package com.dam.apptiempo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UsuarioActivity extends AppCompatActivity {

    // Declaración de EditText para usuario y contraseña, y las SharedPreferences
    private EditText etUsuario, etContrasena;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activamos EdgeToEdge para que la interfaz sea de pantalla completa
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_usuario);

        // Inicializamos los EditText y las SharedPreferences
        etUsuario = findViewById(R.id.et1);
        etContrasena = findViewById(R.id.et2);
        sharedPreferences = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Cargamos el nombre de usuario y contraseña guardados previamente
        String usuarioGuardado = sharedPreferences.getString("usuario", "");
        String contrasenaGuardada = sharedPreferences.getString("contrasena", "");

        // Mostramos los valores guardados en los EditText
        etUsuario.setText(usuarioGuardado);
        etContrasena.setText(contrasenaGuardada);

        // Ajustamos los insets del sistema para que no haya solapamientos con la interfaz
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Método para guardar los datos de usuario y contraseña
    public void guardar(View view) {
        String usuario = etUsuario.getText().toString();
        String contrasena = etContrasena.getText().toString();

        // Verificamos que la contraseña tenga exactamente 9 dígitos numéricos
        if (!contrasena.matches("\\d{9}")) {
            Toast.makeText(this, "La contraseña debe tener exactamente 9 números", Toast.LENGTH_SHORT).show();
            return; // No se guarda si no cumple con los requisitos
        }

        // Guardamos el usuario y contraseña en SharedPreferences
        editor.putString("usuario", usuario);
        editor.putString("contrasena", contrasena);
        editor.apply();

        // Mostramos un mensaje de éxito
        Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();

        // Transición a MainActivity tras guardar los datos correctamente
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    // Método para volver a MainActivity sin guardar
    public void volver(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
