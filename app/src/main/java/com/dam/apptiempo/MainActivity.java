package com.dam.apptiempo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView tvCiudad, tvFecha, tvTemperatura, tvResumenTiempo, tvDetalleTiempo;
    private ImageView imgvIcono;
    private WebView wv1, wv2, wv3;
    private String ciudadMapa;
    private String provincias;


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

        SharedPreferences sharedPreferences = getSharedPreferences("Ajustes", MODE_PRIVATE);
        provincias = sharedPreferences.getString("provincia", "Sevilla");

        // Inicializar variables para poder introducirla en las vistas.
        tvCiudad = findViewById(R.id.tvCiudad);
        tvFecha = findViewById(R.id.tvFecha);
        tvTemperatura = findViewById(R.id.tvTemperatura);
        tvResumenTiempo = findViewById(R.id.tvResumenTiempo);
        tvDetalleTiempo = findViewById(R.id.tvDetalleTiempo);
        imgvIcono = findViewById(R.id.imgvIcono);
        wv1 = findViewById(R.id.wv1);
        wv2 = findViewById(R.id.wv2);

        wv1.setWebViewClient(new WebViewClient());
        wv2.setWebViewClient(new WebViewClient());


        // Cargar datos del clima
        cargarDatosClima();
    }

    private void cargarDatosClima() {
        switch (provincias) {
            case "Almería":
                new FetchWeatherTask().execute("https://www.el-tiempo.net/api/json/v2/provincias/04");
                break;
            case "Cádiz":
                new FetchWeatherTask().execute("https://www.el-tiempo.net/api/json/v2/provincias/11");
                break;
            case "Córdoba":
                new FetchWeatherTask().execute("https://www.el-tiempo.net/api/json/v2/provincias/14");
                break;
            case "Granada":
                new FetchWeatherTask().execute("https://www.el-tiempo.net/api/json/v2/provincias/18");
                break;
            case "Huelva":
                new FetchWeatherTask().execute("https://www.el-tiempo.net/api/json/v2/provincias/21");
                break;
            case "Jaén":
                new FetchWeatherTask().execute("https://www.el-tiempo.net/api/json/v2/provincias/23");
                break;
            case "Málaga":
                new FetchWeatherTask().execute("https://www.el-tiempo.net/api/json/v2/provincias/29");
                break;
            case "Sevilla":
                new FetchWeatherTask().execute("https://www.el-tiempo.net/api/json/v2/provincias/41");
                break;
            default:
                new FetchWeatherTask().execute("https://www.el-tiempo.net/api/json/v2/provincias/41");
                break;
        }

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
            resumen = corregirTexto(resumen);

            // Encontrar el índice del primer punto
            int index = resumen.indexOf('.');
            if (index != -1) {
                resumen = resumen.substring(0, index + 1); // Incluye el primer punto
            }

            // Actualizar el TextView con el resumen corregido
            tvResumenTiempo.setText(resumen);


            // Puedes agregar más detalles como la ciudad y la temperatura
            JSONArray ciudades = jsonObject.getJSONArray("ciudades");
            JSONObject ciudad = ciudades.getJSONObject(ciudades.length()-1); // Ejemplo con la primera ciudad
            String ciudadNombre = ciudad.getString("name");
            String tempMax = ciudad.getJSONObject("temperatures").getString("max");
            String tempMin = ciudad.getJSONObject("temperatures").getString("min");

            String tempMedia= String.valueOf((Integer.parseInt(tempMax)+Integer.parseInt(tempMin))/2);

            String ciudadFormat = corregirTexto(ciudadNombre);
            tvCiudad.setText(ciudadFormat);
            tvTemperatura.setText(tempMedia + "°C");

            // Mostrar detalles adicionales del clima
            tvDetalleTiempo.setText("Temperatura Mín.: "+tempMin+"ºC | Temperatura Máx.: "+tempMax+"ºC");

            String ciudadMinus = borrarTilde(ciudadFormat).toLowerCase();

            //Para que en los dos WebView se muestren el tiempo por horas y el de los próximos días.
            wv1.loadUrl("https://www.eltiempo.es/"+ ciudadMinus+ ".html?v=por_hora#meteograma");
            wv2.loadUrl("https://www.eltiempo.es/"+ ciudadMinus+ ".html#cityPoisTable");

            //Utilizo la variable ciudadMapa para poder abrir el enlace del mapa de precipitaciones.
            ciudadMapa = ciudadMinus;


            //Para mostrar la fecha de hoy en español
            Calendar hoy = Calendar.getInstance();
            SimpleDateFormat formatoFecha = new SimpleDateFormat("EEEE, d 'de' MMMM", new Locale("es", "ES"));
            String fechaFormateada = formatoFecha.format(hoy.getTime());
            tvFecha.setText(fechaFormateada);


            // Cambiar el icono según el estado del tiempo
            switch (ciudad.getJSONObject("stateSky").getString("description")) {
                case "Soleado":
                    imgvIcono.setImageResource(R.drawable.ic_sol);
                    break;
                case "Poco nuboso":
                    imgvIcono.setImageResource(R.drawable.ic_nublado);
                    break;
                case "Lluvioso":
                    case "Cubierto con lluvia escasa ":
                        case "Intervalos nubosos con lluvia":
                    imgvIcono.setImageResource(R.drawable.ic_lluvia);
                    break;
                case "Nuboso":
                    case "Muy nuboso":
                        case "Intervalos nubosos":
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



    /*
    * Metodo para corregir el texto que recojo del JSON de la API.
    * */
    private String corregirTexto(String texto) {
        // Crear un mapa con los caracteres mal codificados y sus equivalentes correctos
        String[][] reemplazos = {
                {"Ã¡", "á"}, {"Ã©", "é"}, {"Ã­", "í"}, {"Ã³", "ó"}, {"Ãº", "ú"},
                {"Ã±", "ñ"}, {"Ã", "í"}, {"Â", ""}
        };

        // Recorrer el mapa y aplicar los reemplazos
        for (String[] reemplazo : reemplazos) {
            texto = texto.replace(reemplazo[0], reemplazo[1]);
        }

        return texto;
    }


    /*
    * Metodo para borrar las tildes de los nombres de las ciudades ya que estas no son compatibles
    * con la url que necesito.
    * */
    private String borrarTilde(String texto){
        String[][] reemplazos = {
                {"á", "a"}, {"é", "e"}, {"í", "i"}, {"ó", "o"}, {"ú", "u"}
        };
        for (String[] reemplazo : reemplazos) {
            texto = texto.replace(reemplazo[0], reemplazo[1]);
        }

        return texto;
    }

     /*
     * Estos métodos que hay a partir de ahora son para los botones de la
     * actividad principal.
     * Los botones hacen que puedan moverse entre las activities de la app,
     * y también poder pasar los datos necesarios.
     * */

    public void detalleHora(View view) {
        Intent intent = new Intent(this, DetalleHoraActivity.class);
        intent.putExtra("url1", wv1.getUrl());
        startActivity(intent);
    }


    public void detalleDia(View view) {
        Intent intent = new Intent(this, DetalleDiaActivity.class);
        intent.putExtra("url2", wv2.getUrl());
        startActivity(intent);
    }

    public void irAjustes(View view){
        Intent i = new Intent(this, AjustesActivity.class);
        startActivity(i);
    }

    public void irUsuario(View view){
        Intent i = new Intent(this, UsuarioActivity.class);
        startActivity(i);
    }

    public void irMapa(View view){
        String url = "https://www.tiempoyradar.es/tiempo/" + ciudadMapa;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


}
