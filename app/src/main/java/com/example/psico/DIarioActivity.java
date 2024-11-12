package com.example.psico;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DIarioActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private TextView dateText;
    private EditText diaryEntry;
    private Button saveButton;
    private ImageButton prevButton, nextButton;

    private Calendar currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diario2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        dateText = findViewById(R.id.date_text);
        diaryEntry = findViewById(R.id.diary_entry);
        saveButton = findViewById(R.id.save_button);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);

        currentDate = Calendar.getInstance();
        updateDateDisplay();
        loadDiaryEntry(getFormattedDate(currentDate.getTime()));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDiaryEntry();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.DAY_OF_YEAR, -1);
                updateDateDisplay();
                loadDiaryEntry(getFormattedDate(currentDate.getTime()));
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.DAY_OF_YEAR, 1);
                updateDateDisplay();
                loadDiaryEntry(getFormattedDate(currentDate.getTime()));
            }
        });
    }

    private void updateDateDisplay() {
        String formattedDate = getFormattedDate(currentDate.getTime());
        dateText.setText(formattedDate);
    }

    private String getFormattedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    private void loadDiaryEntry(String date) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference diaryRef = mDatabase.child("usuarios").child(userId).child("diario").child(date);

        diaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String entry = dataSnapshot.child("entrada").getValue(String.class);
                    diaryEntry.setText(entry);
                } else {
                    diaryEntry.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DIarioActivity.this, "Error al cargar la entrada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDiaryEntry() {
        String entry = diaryEntry.getText().toString().trim();
        String date = getFormattedDate(currentDate.getTime());
        String userId = mAuth.getCurrentUser().getUid();

        if (!entry.isEmpty()) {
            mDatabase.child("usuarios").child(userId).child("diario").child(date).child("entrada").setValue(entry)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(DIarioActivity.this, "Entrada guardada", Toast.LENGTH_SHORT).show();
                            diaryEntry.setText(""); // Limpia la entrada después de guardar
                            currentDate = Calendar.getInstance(); // Restablece la fecha a hoy
                            updateDateDisplay();
                        } else {
                            Toast.makeText(DIarioActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(DIarioActivity.this, "La entrada está vacía", Toast.LENGTH_SHORT).show();
        }
    }
}
