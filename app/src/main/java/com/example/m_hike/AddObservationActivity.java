package com.example.m_hike;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.database.ObservationDAO;
import com.example.m_hike.models.Observation;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddObservationActivity extends AppCompatActivity {
    private EditText etObservation, etComments;
    private TextView tvTimestamp;
    private Button btnSave;
    private ObservationDAO dao;
    private long hikeId;
    private long editObsId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observation);

        etObservation = findViewById(R.id.etObservation); // Nhớ tạo ID này trong XML
        etComments = findViewById(R.id.etComments);       // Nhớ tạo ID này trong XML
        tvTimestamp = findViewById(R.id.tvTimestamp);     // Nhớ tạo ID này trong XML
        btnSave = findViewById(R.id.btnSaveObservation);  // Nhớ tạo ID này trong XML

        dao = new ObservationDAO(this);

        hikeId = getIntent().getLongExtra("hikeId", -1);
        editObsId = getIntent().getLongExtra("editObsId", -1);

        // Mặc định hiển thị giờ hiện tại
        String currentTime = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvTimestamp.setText(currentTime);

        // Bấm vào giờ để chọn lại giờ (Optional)
        tvTimestamp.setOnClickListener(v -> showTimePicker());

        // Kiểm tra xem có phải đang EDIT không
        if (editObsId != -1) {
            Observation o = dao.getById(editObsId);
            if (o != null) {
                etObservation.setText(o.getObsText());
                etComments.setText(o.getComments());
                tvTimestamp.setText(o.getTimestamp());
                btnSave.setText("Update Observation");
            }
        }

        btnSave.setOnClickListener(v -> {
            String obsName = etObservation.getText().toString().trim();
            String comments = etComments.getText().toString().trim();
            String time = tvTimestamp.getText().toString().trim();

            if (TextUtils.isEmpty(obsName)) {
                etObservation.setError("Observation name is required");
                return;
            }

            Observation o = new Observation();
            o.setHikeId(hikeId);
            o.setObsText(obsName);
            o.setTimestamp(time);
            o.setComments(comments);

            if (editObsId == -1) {
                // Thêm mới
                long id = dao.insert(o);
                if (id > 0) {
                    Toast.makeText(this, "Observation added", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Cập nhật
                o.setId(editObsId);
                boolean ok = dao.update(o);
                if (ok) {
                    Toast.makeText(this, "Observation updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            // Có thể nối thêm ngày vào nếu muốn
            String date = new SimpleDateFormat(" dd/MM/yyyy", Locale.getDefault()).format(new Date());
            tvTimestamp.setText(time + date);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }
}