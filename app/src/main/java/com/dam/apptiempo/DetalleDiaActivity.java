package com.dam.apptiempo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetalleDiaActivity extends AppCompatActivity {
    private WebView wv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_dia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Declaraciones de las variables
        wv1 = findViewById(R.id.wv1);
        wv1.loadUrl(getIntent().getStringExtra("url2"));
    }

    //Metodo volver a Main.
    public void volver(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}