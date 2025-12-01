package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.adapters.ObservationAdapter;
import com.example.m_hike.database.HikeDAO;
import com.example.m_hike.database.ObservationDAO;
import com.example.m_hike.models.Hike;
import com.example.m_hike.models.Observation;

import java.util.List;

public class HikeDetailActivity extends AppCompatActivity implements ObservationAdapter.OnItemClickListener {
    private HikeDAO hikeDAO;
    private ObservationDAO obsDAO;
    private TextView tvName, tvLocation, tvDate, tvParking, tvLength, tvDifficulty, tvDescription;
    private RecyclerView rvObs;
    private ObservationAdapter adapter;
    private long hikeId;
    private Hike hike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_detail);

        hikeId = getIntent().getLongExtra("hikeId", -1);
        if (hikeId == -1) {
            finish(); return;
        }

        hikeDAO = new HikeDAO(this);
        obsDAO = new ObservationDAO(this);

        tvName = findViewById(R.id.tvHikeName);
        tvLocation = findViewById(R.id.tvHikeLocation);
        tvDate = findViewById(R.id.tvHikeDate);
        tvParking = findViewById(R.id.tvHikeParking);
        tvLength = findViewById(R.id.tvHikeLength);
        tvDifficulty = findViewById(R.id.tvHikeDifficulty);
        tvDescription = findViewById(R.id.tvHikeDescription);

        rvObs = findViewById(R.id.rvObservations);
        rvObs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ObservationAdapter(this);
        rvObs.setAdapter(adapter);

        findViewById(R.id.btnAddObservation).setOnClickListener(v -> {
            Intent i = new Intent(this, AddObservationActivity.class);
            i.putExtra("hikeId", hikeId);
            startActivity(i);
        });

        findViewById(R.id.btnEditHike).setOnClickListener(v -> {
            Intent i = new Intent(this, AddHikeActivity.class);
            i.putExtra("editId", hikeId);
            startActivity(i);
        });

        findViewById(R.id.btnDeleteHike).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Delete this hike and its observations?")
                    .setPositiveButton("Delete", (d, w) -> {
                        hikeDAO.delete(hikeId);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        loadHike();
        loadObservations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHike();
        loadObservations();
    }

    private void loadHike() {
        hike = hikeDAO.getById(hikeId);
        if (hike != null) {
            tvName.setText(hike.getName());
            tvLocation.setText(hike.getLocation());
            tvDate.setText(hike.getDate());
            tvParking.setText(hike.isParkingAvailable() ? "Yes" : "No");
            tvLength.setText(hike.getLength());
            tvDifficulty.setText(hike.getDifficulty());
            tvDescription.setText(hike.getDescription() == null ? "" : hike.getDescription());
        }
    }

    private void loadObservations() {
        List<Observation> list = obsDAO.getByHike(hikeId);
        adapter.setList(list);
    }

    @Override
    public void onItemClick(Observation o) {
        // open edit dialog/activity
        Intent i = new Intent(this, AddObservationActivity.class);
        i.putExtra("hikeId", hikeId);
        i.putExtra("editObsId", o.getId());
        startActivity(i);
    }

    @Override
    public void onItemLongClick(Observation o) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Observation")
                .setMessage("Delete this observation?")
                .setPositiveButton("Delete", (d, w) -> {
                    obsDAO.delete(o.getId());
                    loadObservations();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
