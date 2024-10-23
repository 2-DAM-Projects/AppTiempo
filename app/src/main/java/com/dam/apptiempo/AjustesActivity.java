package com.dam.apptiempo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AjustesActivity extends AppCompatActivity {
    private Switch swtTema, swtUnidad;
    private Spinner spnProvincias;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ajustes);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("Ajustes", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        spnProvincias = findViewById(R.id.spnProvincias);
        swtTema = findViewById(R.id.swtTema);




        // Configurar el Spinner con las provincias de España
        String[] provincias = {"Almería", "Cádiz", "Córdoba", "Granada", "Huelva", "Jaén", "Málaga", "Sevilla"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provincias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnProvincias.setAdapter(adapter);


        // Recuperar el valor guardado anteriormente (si existe)
        String provinciaGuardada = sharedPreferences.getString("provincia", null);
        if (provinciaGuardada != null) {
            int posicion = adapter.getPosition(provinciaGuardada);
            spnProvincias.setSelection(posicion);
        }

        //para cambiar de tema la app.
        boolean isDarkMode = sharedPreferences.getBoolean("tema_oscuro", false);
        swtTema.setChecked(isDarkMode);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    public void guardar(View view, boolean isChecked) {
        String provinciaSeleccionada = spnProvincias.getSelectedItem().toString();
        editor.putString("provincia", provinciaSeleccionada);
        editor.putBoolean("tema_oscuro", isChecked);
        editor.apply();

        aplicarTemaOscuro(isChecked);

        // Mostrar un mensaje de confirmación
        Toast.makeText(AjustesActivity.this, "Datos Guardados Correctamente" , Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void aplicarTemaOscuro(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}