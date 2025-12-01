package com.example.m_hike;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.adapters.HikeAdapter;
import com.example.m_hike.database.HikeDAO;
import com.example.m_hike.models.Hike;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements HikeAdapter.OnItemClickListener {
    private EditText etName, etLocation, etLength, etDate;
    private Button btnSearch;
    private HikeDAO dao;
    private RecyclerView rv;
    private HikeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etName = findViewById(R.id.etSearchName);
        etLocation = findViewById(R.id.etSearchLocation);
        etLength = findViewById(R.id.etSearchLength);
        etDate = findViewById(R.id.etSearchDate);
        btnSearch = findViewById(R.id.btnPerformSearch);
        dao = new HikeDAO(this);

        rv = findViewById(R.id.rvSearchResults);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HikeAdapter(this);
        rv.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> doSearch());
    }

    private void doSearch() {
        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String length = etLength.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        List<Hike> results;
        if (!name.isEmpty() || !location.isEmpty() || !length.isEmpty() || !date.isEmpty()) {
            results = dao.advancedSearch(name, location, length, date);
        } else {
            Toast.makeText(this, "Enter at least one criterion", Toast.LENGTH_SHORT).show();
            return;
        }
        adapter.setList(results);
    }

    @Override
    public void onItemClick(Hike hike) {
        // open detail
        startActivity(new android.content.Intent(this, HikeDetailActivity.class).putExtra("hikeId", hike.getId()));
    }

    @Override
    public void onItemLongClick(Hike hike) {
        // not used
    }
}
