package com.example.psico

import android.media.MediaPlayer
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        var mediaPlayer = MediaPlayer.create(this, R.raw.inner_light)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnGoToRegister: Button = findViewById(R.id.btnCreateAccount)
        btnGoToRegister.setOnClickListener {
            // Crear un Intent para ir a RegisterActivity
            val intent = Intent(this, RegistrerActivity::class.java)
            startActivity(intent)
        }
        fun onDestroy() {
            super.onDestroy()
            // Libera el MediaPlayer para evitar fugas de memoria
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
        }
    }
}