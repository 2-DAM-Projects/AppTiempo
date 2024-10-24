package com.dam.apptiempo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class AjustesActivity extends AppCompatActivity {
    private Switch swtTema;
    private Spinner spnProvincias;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("Ajustes", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("tema_oscuro", false);

        // Establecer el tema antes de setContentView
        if (isDarkMode) {
            setTheme(R.style.Oscuro); // Aplicar tema oscuro
        } else {
            setTheme(R.style.Claro); // Aplicar tema claro
        }

        spnProvincias = findViewById(R.id.spnProvincias);
        swtTema = findViewById(R.id.swtTema);

        // Configurar el Spinner con las provincias de Andalucía
        String[] provincias = {"Almería", "Cádiz", "Córdoba", "Granada", "Huelva", "Jaén", "Málaga", "Sevilla"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provincias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnProvincias.setAdapter(adapter);

        // Recuperar el valor guardado anteriormente (si existe)
        String provinciaGuardada = sharedPreferences.getString("provincia", "Sevilla");
        int posicion = adapter.getPosition(provinciaGuardada);
        spnProvincias.setSelection(posicion);

        // Configurar el Switch para el tema
        swtTema.setChecked(isDarkMode);

        // Listener para cambiar de tema
        swtTema.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit(); // Asegúrate de que el editor esté inicializado aquí
            editor.putBoolean("tema_oscuro", isChecked);
            editor.apply();

            // Reiniciar la actividad para aplicar el nuevo tema
            recreate();
        });
    }

    public void guardar(View view) {
        String provinciaSeleccionada = spnProvincias.getSelectedItem().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit(); // Asegúrate de que el editor esté inicializado aquí
        editor.putString("provincia", provinciaSeleccionada);
        editor.apply();

        // Mostrar un mensaje de confirmación
        Toast.makeText(AjustesActivity.this, "Datos Guardados Correctamente", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
