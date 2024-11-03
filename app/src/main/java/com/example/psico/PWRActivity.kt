package com.example.psico

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class PWRActivity : AppCompatActivity() {
    private lateinit var etResetEmail: EditText
    private lateinit var btnSendReset: Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pwr)
        auth= FirebaseAuth.getInstance()

        // Referenciar campos
        etResetEmail = findViewById(R.id.etResetEmail)
        btnSendReset = findViewById(R.id.btnSendReset)

        btnSendReset.setOnClickListener {
            val email = etResetEmail.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
            } else {
                sendPasswordResetEmail(email)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Correo de restablecimiento enviado", Toast.LENGTH_SHORT).show()
                    finish() // Cierra la actividad después de enviar el correo
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}