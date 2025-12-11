package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.adapters.HikeAdapter;
import com.example.m_hike.database.HikeDAO;
import com.example.m_hike.models.Hike;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HikeAdapter.OnItemClickListener {
    private HikeDAO dao;
    private HikeAdapter adapter;
    private RecyclerView rv;
    private SearchView searchView;
    private List<Hike> fullList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dao = new HikeDAO(this);
        adapter = new HikeAdapter(this);
        fullList = new ArrayList<>();


        rv = findViewById(R.id.rvHikes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);


        FloatingActionButton fab = findViewById(R.id.fabAddHike);
        fab.setOnClickListener(v -> {

            startActivity(new Intent(MainActivity.this, AddHikeActivity.class));
        });


        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });


        loadData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadData();


        if (searchView != null && searchView.getQuery().length() > 0) {
            filterList(searchView.getQuery().toString());
        }
    }

    private void loadData() {
        fullList = dao.getAll();
        adapter.setList(fullList);
    }

    private void filterList(String text) {
        List<Hike> filteredList = new ArrayList<>();
        for (Hike hike : fullList) {
            if (hike.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(hike);
            }
        }

        adapter.setList(filteredList);
    }


    @Override
    public void onItemClick(Hike hike) {
        Intent intent = new Intent(MainActivity.this, AddHikeActivity.class);


        intent.putExtra("id", hike.getId());


        intent.putExtra("name", hike.getName());
        intent.putExtra("location", hike.getLocation());
        intent.putExtra("date", hike.getDate());
        intent.putExtra("parking", hike.isParkingAvailable());
        intent.putExtra("length", hike.getLength());
        intent.putExtra("difficulty", hike.getDifficulty());
        intent.putExtra("description", hike.getDescription());


        intent.putExtra("extra1", hike.getExtra1());
        intent.putExtra("extra2", hike.getExtra2());


        intent.putExtra("photo_path", hike.getImagePath());

        startActivity(intent);
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
        menu.add("Reset DB").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("Reset DB")) {
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