package com.example.psico

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicia DiarioActivity directamente
        val intent = Intent(this, DIarioActivity::class.java)
        startActivity(intent)
        finish() // Cierra MainActivity para que no esté en la pila de actividades
    }
}