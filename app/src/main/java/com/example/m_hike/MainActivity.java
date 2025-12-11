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

        // 1. Khởi tạo Database và Adapter
        dao = new HikeDAO(this);
        adapter = new HikeAdapter(this); // Adapter này dùng interface OnItemClickListener
        fullList = new ArrayList<>();

        // 2. Thiết lập RecyclerView
        rv = findViewById(R.id.rvHikes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // 3. Nút thêm mới
        FloatingActionButton fab = findViewById(R.id.fabAddHike);
        fab.setOnClickListener(v -> {
            // Khi bấm nút thêm, chỉ mở Activity mà KHÔNG gửi ID -> Chế độ Thêm mới
            startActivity(new Intent(MainActivity.this, AddHikeActivity.class));
        });

        // 4. Xử lý tìm kiếm (Search)
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

        // 5. Load dữ liệu lần đầu
        loadData();
    }

    // Mỗi khi quay lại từ màn hình Add/Edit, load lại danh sách để cập nhật thay đổi
    @Override
    protected void onResume() {
        super.onResume();
        loadData();

        // Nếu đang search dở thì search lại
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
        // Có thể thêm thông báo nếu list rỗng ở đây
        adapter.setList(filteredList);
    }

    /**
     * SỰ KIỆN CLICK VÀO 1 DÒNG (ĐỂ SỬA)
     * Đây là đoạn code quan trọng nhất để chức năng Edit hoạt động
     */
    @Override
    public void onItemClick(Hike hike) {
        Intent intent = new Intent(MainActivity.this, AddHikeActivity.class);

        // 1. Gửi ID (BẮT BUỘC để biết sửa dòng nào)
        intent.putExtra("id", hike.getId());

        // 2. Gửi các dữ liệu khác để điền vào form
        intent.putExtra("name", hike.getName());
        intent.putExtra("location", hike.getLocation());
        intent.putExtra("date", hike.getDate());
        intent.putExtra("parking", hike.isParkingAvailable()); // Hoặc isParking() tùy vào model
        intent.putExtra("length", hike.getLength());
        intent.putExtra("difficulty", hike.getDifficulty());
        intent.putExtra("description", hike.getDescription());

        // Gửi thêm các trường phụ nếu Model có
        intent.putExtra("extra1", hike.getExtra1());
        intent.putExtra("extra2", hike.getExtra2());

        // Gửi đường dẫn ảnh
        intent.putExtra("photo_path", hike.getImagePath());

        startActivity(intent);
    }

    /**
     * SỰ KIỆN NHẤN GIỮ (ĐỂ XÓA)
     */
    @Override
    public void onItemLongClick(Hike hike) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Hike")
                .setMessage("Delete '" + hike.getName() + "' and its observations?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Gọi lệnh xóa trong DAO
                    boolean ok = dao.delete(hike.getId());
                    if (ok) {
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                        // Load lại dữ liệu ngay lập tức
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