package com.dam.apptiempo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView tvCiudad, tvFecha, tvTemperatura, tvResumenTiempo, tvDetalleTiempo;
    private ImageView imgvIcono;
    private RecyclerView rvTiempoHora, rvTiempoSemana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        tvCiudad = findViewById(R.id.tvCiudad);
        tvFecha = findViewById(R.id.tvFecha);
        tvTemperatura = findViewById(R.id.tvTemperatura);
        tvResumenTiempo = findViewById(R.id.tvResumenTiempo);
        tvDetalleTiempo = findViewById(R.id.tvDetalleTiempo);
        imgvIcono = findViewById(R.id.imgvIcono);
        rvTiempoHora = findViewById(R.id.rvTiempoHora);
        rvTiempoSemana = findViewById(R.id.rvTiempoSemana);

        // Cargar datos del clima
        cargarDatosClima();
    }

    private void cargarDatosClima() {
        new FetchWeatherTask().execute("https://www.el-tiempo.net/api/json/v2/provincias/11"); // Cambia '11' por el código de provincia que necesites
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            parseWeatherData(result);
        }
    }

    private void parseWeatherData(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject today = jsonObject.getJSONObject("today");
            String resumen = today.getString("p"); // Resumen del clima
            tvResumenTiempo.setText(resumen);

            // Puedes agregar más detalles como la ciudad y la temperatura
            JSONArray ciudades = jsonObject.getJSONArray("ciudades");
            JSONObject ciudad = ciudades.getJSONObject(0); // Ejemplo con la primera ciudad
            String ciudadNombre = ciudad.getString("name");
            String tempMax = ciudad.getJSONObject("temperatures").getString("max");
            String tempMin = ciudad.getJSONObject("temperatures").getString("min");

            tvCiudad.setText(ciudadNombre);
            tvTemperatura.setText(tempMax + "°C (Max)");

            // Mostrar detalles adicionales del clima
            tvDetalleTiempo.setText("Humedad: 60% | Viento: 12 km/h"); // Puedes actualizar esto con datos reales si los obtienes

            // Cambiar el icono según el estado del tiempo
            switch (ciudad.getJSONObject("stateSky").getString("description")) {
                case "Soleado":
                    imgvIcono.setImageResource(R.drawable.ic_sol);
                    break;
                case "Nublado":
                    imgvIcono.setImageResource(R.drawable.ic_nublado);
                    break;
                case "Lluvioso":
                    imgvIcono.setImageResource(R.drawable.ic_lluvia);
                    break;
                case "Nube":
                    imgvIcono.setImageResource(R.drawable.ic_nube);
                    break;
                case "Tormenta":
                    imgvIcono.setImageResource(R.drawable.ic_tormenta);
                    break;
                default:
                    imgvIcono.setImageResource(R.drawable.ic_sol); // Icono por defecto
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
