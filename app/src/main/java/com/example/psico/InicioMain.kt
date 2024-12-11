import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.psico.ChatActivity
import com.example.psico.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class InicioMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_bar_main) // Asegúrate de que sea el layout correcto

        // Encuentra el FAB por su ID
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        // Configura el listener para el FAB
        fab.setOnClickListener {
            // Crea un Intent para iniciar ChatActivity
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }
}
