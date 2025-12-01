package com.example.m_hike;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.R;
import com.example.m_hike.database.ObservationDAO;
import com.example.m_hike.models.Observation;
import com.example.m_hike.utils.DateTimeUtils;

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

        etObservation = findViewById(R.id.etObservation);
        etComments = findViewById(R.id.etComments);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        btnSave = findViewById(R.id.btnSaveObservation);

        dao = new ObservationDAO(this);

        hikeId = getIntent().getLongExtra("hikeId", -1);
        editObsId = getIntent().getLongExtra("editObsId", -1);

        tvTimestamp.setText(DateTimeUtils.nowDateTime());

        if (editObsId != -1) {
            // load observation
            // simple: query all for hike and find matching id (or extend DAO to getById)
            for (Observation o : dao.getByHike(hikeId)) {
                if (o.getId() == editObsId) {
                    etObservation.setText(o.getObsText());
                    etComments.setText(o.getComments());
                    tvTimestamp.setText(o.getTimestamp());
                    break;
                }
            }
        }

        btnSave.setOnClickListener(v -> {
            String obs = etObservation.getText().toString().trim();
            String comments = etComments.getText().toString().trim();
            String ts = tvTimestamp.getText().toString().trim();
            if (TextUtils.isEmpty(obs)) {
                etObservation.setError("Required");
                return;
            }
            Observation o = new Observation();
            o.setHikeId(hikeId);
            o.setObsText(obs);
            o.setTimestamp(ts);
            o.setComments(comments);

            if (editObsId == -1) {
                long id = dao.insert(o);
                if (id > 0) {
                    Toast.makeText(this, "Observation saved", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
                }
            } else {
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
}