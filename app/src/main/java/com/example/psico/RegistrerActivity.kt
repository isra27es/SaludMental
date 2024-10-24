package com.example.psico



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrer)
        val btnGoToRegister: Button = findViewById(R.id.btnAtras)
        btnGoToRegister.setOnClickListener {
            // Crear un Intent para ir a RegisterActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}