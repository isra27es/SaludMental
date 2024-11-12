package com.example.psico.ui.diario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.psico.databinding.ActivityDiario2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DiarioFragment : Fragment() {

    private var _binding: ActivityDiario2Binding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private val currentDate: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityDiario2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        updateDateDisplay()
        loadDiaryEntry(getFormattedDate(currentDate.time))

        binding.saveButton.setOnClickListener { saveDiaryEntry() }

        binding.prevButton.setOnClickListener {
            currentDate.add(Calendar.DAY_OF_YEAR, -1)
            updateDateDisplay()
            loadDiaryEntry(getFormattedDate(currentDate.time))
        }

        binding.nextButton.setOnClickListener {
            currentDate.add(Calendar.DAY_OF_YEAR, 1)
            updateDateDisplay()
            loadDiaryEntry(getFormattedDate(currentDate.time))
        }
    }

    private fun updateDateDisplay() {
        binding.dateText.text = getFormattedDate(currentDate.time)
    }

    private fun getFormattedDate(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(date)
    }

    private fun loadDiaryEntry(date: String) {
        val userId = mAuth.currentUser?.uid ?: return
        val diaryRef = mDatabase.child("usuarios").child(userId).child("diario").child(date)

        diaryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entry = snapshot.child("entrada").getValue(String::class.java)
                binding.diaryEntry.setText(entry ?: "")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error al cargar la entrada", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveDiaryEntry() {
        val entry = binding.diaryEntry.text.toString().trim()
        val date = getFormattedDate(currentDate.time)
        val userId = mAuth.currentUser?.uid ?: return

        if (entry.isNotEmpty()) {
            mDatabase.child("usuarios").child(userId).child("diario").child(date).child("entrada").setValue(entry)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Entrada guardada", Toast.LENGTH_SHORT).show()
                        binding.diaryEntry.setText("")  // Limpia la entrada después de guardar
                        currentDate.time = Calendar.getInstance().time  // Restablece la fecha a hoy
                        updateDateDisplay()
                    } else {
                        Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "La entrada está vacía", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
