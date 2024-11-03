package com.example.psico;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DIarioActivity extends AppCompatActivity {

    private DatabaseReference database;
    private int currentPageId = 0;
    private List<DiaryEntry> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diario2);

        database = FirebaseDatabase.getInstance().getReference();

        TextView dateText = findViewById(R.id.date_text);
        Button saveButton = findViewById(R.id.save_button);
        EditText diaryEntry = findViewById(R.id.diary_entry);
        ImageButton prevButton = findViewById(R.id.prev_button);
        ImageButton nextButton = findViewById(R.id.next_button);

        // Mostrar la fecha actual
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        dateText.setText(currentDate);

        // Cargar entradas desde Firebase
        loadEntries();

        // Manejar el guardado de entradas
        saveButton.setOnClickListener(v -> {
            String text = diaryEntry.getText().toString();
            saveDiaryEntry(currentDate, text);
            diaryEntry.setText(""); // Limpiar el campo de entrada
        });

        // Manejar navegación entre páginas
        prevButton.setOnClickListener(v -> {
            if (currentPageId > 0) {
                currentPageId--;
                showEntry();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentPageId < entries.size() - 1) {
                currentPageId++;
                showEntry();
            }
        });

        // Mostrar la entrada inicial
        showEntry();
    }

    private void saveDiaryEntry(String date, String text) {
        String entryId = database.child("diary_entries").push().getKey();
        if (entryId == null) return;

        DiaryEntry entry = new DiaryEntry(entryId, date, text);
        database.child("diary_entries").child(entryId).setValue(entry)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Entrada guardada", Toast.LENGTH_SHORT).show();
                    loadEntries(); // Volver a cargar entradas después de guardar
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
    }

    private void loadEntries() {
        database.child("diary_entries").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                entries.clear();
                for (DataSnapshot entrySnapshot : dataSnapshot.getChildren()) {
                    DiaryEntry entry = entrySnapshot.getValue(DiaryEntry.class);
                    if (entry != null) {
                        entries.add(entry);
                    }
                }
                currentPageId = 0;
                showEntry(); // Mostrar la primera entrada después de cargar
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DIarioActivity.this, "Error al cargar entradas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEntry() {
        if (!entries.isEmpty()) {
            DiaryEntry currentEntry = entries.get(currentPageId);
            ((EditText) findViewById(R.id.diary_entry)).setText(currentEntry.getText());
            ((TextView) findViewById(R.id.date_text)).setText(currentEntry.getDate());
        }
    }
}
