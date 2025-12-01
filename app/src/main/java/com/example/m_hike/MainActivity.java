package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.adapters.HikeAdapter;
import com.example.m_hike.database.HikeDAO;
import com.example.m_hike.models.Hike;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements HikeAdapter.OnItemClickListener {
    private HikeDAO dao;
    private HikeAdapter adapter;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = new HikeDAO(this);
        rv = findViewById(R.id.rvHikes);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HikeAdapter(this);
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddHike);
        fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddHikeActivity.class)));

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Hike> list = dao.getAll();
        adapter.setList(list);
    }

    @Override
    public void onItemClick(Hike hike) {
        Intent i = new Intent(this, HikeDetailActivity.class);
        i.putExtra("hikeId", hike.getId());
        startActivity(i);
    }

    @Override
    public void onItemLongClick(Hike hike) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Hike")
                .setMessage("Delete '" + hike.getName() + "' and its observations?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean ok = dao.delete(hike.getId());
                    if (ok) {
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                        loadData();
                    } else {
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Search").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add("Reset DB").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String t = item.getTitle().toString();
        if (t.equals("Search")) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        } else if (t.equals("Reset DB")) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Delete all hikes and observations?")
                    .setPositiveButton("Yes", (d, w) -> {
                        dao.deleteAll();
                        loadData();
                        Toast.makeText(this, "Database cleared", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}