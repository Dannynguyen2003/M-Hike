package com.example.m_hike;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.R;
import com.example.m_hike.database.HikeDAO;
import com.example.m_hike.models.Hike;
import com.example.m_hike.utils.DateTimeUtils;

import java.util.Calendar;

public class AddHikeActivity extends AppCompatActivity {
    private EditText etName, etLocation, etLength, etDifficulty, etDescription, etExtra1, etExtra2;
    private TextView tvDate;
    private Switch swParking;
    private Button btnSave;
    private HikeDAO dao;
    private long editingId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hike);

        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etLength = findViewById(R.id.etLength);
        etDifficulty = findViewById(R.id.etDifficulty);
        etDescription = findViewById(R.id.etDescription);
        etExtra1 = findViewById(R.id.etExtra1);
        etExtra2 = findViewById(R.id.etExtra2);
        tvDate = findViewById(R.id.tvDate);
        swParking = findViewById(R.id.swParking);
        btnSave = findViewById(R.id.btnSaveHike);

        dao = new HikeDAO(this);

        tvDate.setText(DateTimeUtils.todayDate());
        tvDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> onSave());

        // Check if editing
        long id = getIntent().getLongExtra("editId", -1);
        if (id != -1) {
            editingId = id;
            Hike h = dao.getById(id);
            if (h != null) {
                etName.setText(h.getName());
                etLocation.setText(h.getLocation());
                tvDate.setText(h.getDate());
                swParking.setChecked(h.isParkingAvailable());
                etLength.setText(h.getLength());
                etDifficulty.setText(h.getDifficulty());
                etDescription.setText(h.getDescription());
                etExtra1.setText(h.getExtra1());
                etExtra2.setText(h.getExtra2());
            }
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String s = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            tvDate.setText(s);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    private void onSave() {
        String name = etName.getText().toString().trim();
        String loc = etLocation.getText().toString().trim();
        String date = tvDate.getText().toString().trim();
        boolean parking = swParking.isChecked();
        String length = etLength.getText().toString().trim();
        String diff = etDifficulty.getText().toString().trim();

        // Validation of required fields
        if (TextUtils.isEmpty(name)) { etName.setError("Required"); etName.requestFocus(); return; }
        if (TextUtils.isEmpty(loc)) { etLocation.setError("Required"); etLocation.requestFocus(); return; }
        if (TextUtils.isEmpty(date)) { Toast.makeText(this, "Date required", Toast.LENGTH_SHORT).show(); return; }
        if (TextUtils.isEmpty(length)) { etLength.setError("Required"); etLength.requestFocus(); return; }
        if (TextUtils.isEmpty(diff)) { etDifficulty.setError("Required"); etDifficulty.requestFocus(); return; }

        Hike h = new Hike();
        h.setName(name);
        h.setLocation(loc);
        h.setDate(date);
        h.setParkingAvailable(parking);
        h.setLength(length);
        h.setDifficulty(diff);
        h.setDescription(etDescription.getText().toString().trim());
        h.setExtra1(etExtra1.getText().toString().trim());
        h.setExtra2(etExtra2.getText().toString().trim());

        if (editingId == -1) {
            long id = dao.insert(h);
            if (id > 0) {
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error saving", Toast.LENGTH_SHORT).show();
            }
        } else {
            h.setId(editingId);
            boolean ok = dao.update(h);
            if (ok) {
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
