package com.example.m_hike;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.adapters.ObservationAdapter;
import com.example.m_hike.database.HikeDAO;
import com.example.m_hike.database.ObservationDAO;
import com.example.m_hike.models.Hike;
import com.example.m_hike.models.Observation;
import com.example.m_hike.utils.DateTimeUtils; // Giả sử bạn có class này, hoặc dùng SimpleDateFormat trực tiếp
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddHikeActivity extends AppCompatActivity {

    // Views cũ
    private ImageView ivHikeImage;
    private TextView tvDate;
    private EditText edtName, edtLocation, edtLength, edtDifficulty, edtDescription, edtExtra1, edtExtra2;
    private SwitchMaterial swParking;
    private Button btnSave;

    // --- Views MỚI cho Observation ---
    private LinearLayout layoutObservations;
    private RecyclerView rvObservations;
    private Button btnAddObsQuick;
    private ObservationDAO obsDAO;
    private ObservationAdapter obsAdapter;
    // --------------------------------

    private String currentPhotoPath;
    private Uri photoURI;
    private long hikeId = -1;

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (result) {
                    ivHikeImage.setImageURI(photoURI);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hike);

        obsDAO = new ObservationDAO(this); // Khởi tạo DAO

        initViews();
        setupObservationList(); // Cài đặt RecyclerView

        // Mặc định ngày hiện tại
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvDate.setText(currentDate);

        // Sự kiện click
        tvDate.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.btnTakePhoto).setOnClickListener(v -> dispatchTakePictureIntent());
        btnSave.setOnClickListener(v -> saveHike());

        // Sự kiện thêm Observation nhanh
        btnAddObsQuick.setOnClickListener(v -> showObservationDialog(null));

        // Kiểm tra chế độ Edit
        checkForEditMode();
    }

    private void initViews() {
        ivHikeImage = findViewById(R.id.ivHikeImage);
        tvDate = findViewById(R.id.tvDate);
        edtName = findViewById(R.id.etName);
        edtLocation = findViewById(R.id.etLocation);
        swParking = findViewById(R.id.swParking);
        edtLength = findViewById(R.id.etLength);
        edtDifficulty = findViewById(R.id.etDifficulty);
        edtDescription = findViewById(R.id.etDescription);
        edtExtra1 = findViewById(R.id.etExtra1);
        edtExtra2 = findViewById(R.id.etExtra2);
        btnSave = findViewById(R.id.btnSaveHike);

        // Ánh xạ views mới
        layoutObservations = findViewById(R.id.layoutObservations);
        rvObservations = findViewById(R.id.rvObservationsInside);
        btnAddObsQuick = findViewById(R.id.btnAddObsQuick);
    }

    private void setupObservationList() {
        rvObservations.setLayoutManager(new LinearLayoutManager(this));
        obsAdapter = new ObservationAdapter(new ObservationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Observation o) {
                // Click vào item để sửa
                showObservationDialog(o);
            }

            @Override
            public void onItemLongClick(Observation o) {
                // Giữ lì để xóa
                new AlertDialog.Builder(AddHikeActivity.this)
                        .setTitle("Delete Observation")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", (d, w) -> {
                            obsDAO.delete(o.getId());
                            loadObservations(); // Load lại list
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        rvObservations.setAdapter(obsAdapter);
    }

    private void checkForEditMode() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            hikeId = intent.getLongExtra("id", -1);
            btnSave.setText("Update Hike");

            // Fill dữ liệu cũ
            edtName.setText(intent.getStringExtra("name"));
            edtLocation.setText(intent.getStringExtra("location"));
            tvDate.setText(intent.getStringExtra("date"));
            swParking.setChecked(intent.getBooleanExtra("parking", false));
            edtLength.setText(intent.getStringExtra("length"));
            edtDifficulty.setText(intent.getStringExtra("difficulty"));
            edtDescription.setText(intent.getStringExtra("description"));
            edtExtra1.setText(intent.getStringExtra("extra1"));
            edtExtra2.setText(intent.getStringExtra("extra2"));

            currentPhotoPath = intent.getStringExtra("photo_path");
            if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
                File imgFile = new File(currentPhotoPath);
                if (imgFile.exists()) {
                    ivHikeImage.setImageURI(Uri.fromFile(imgFile));
                }
            }

            // --- QUAN TRỌNG: HIỆN PHẦN OBSERVATION ---
            layoutObservations.setVisibility(View.VISIBLE);
            loadObservations();

        } else {
            // Đang thêm mới -> Ẩn phần Observation (vì chưa có hikeId để link)
            layoutObservations.setVisibility(View.GONE);
        }
    }

    private void loadObservations() {
        if (hikeId != -1) {
            List<Observation> list = obsDAO.getByHike(hikeId);
            obsAdapter.setList(list);
        }
    }

    // --- HÀM HIỆN DIALOG ĐỂ THÊM/SỬA OBSERVATION ---
    private void showObservationDialog(Observation obsToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(obsToEdit == null ? "Add Observation" : "Edit Observation");

        // Tạo layout cho dialog bằng code (hoặc inflate từ xml riêng)
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        final EditText etObsName = new EditText(this);
        etObsName.setHint("Observation Name");
        layout.addView(etObsName);

        final EditText etObsTime = new EditText(this);
        etObsTime.setHint("Time (HH:mm)");
        // Mặc định lấy giờ hiện tại
        etObsTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        layout.addView(etObsTime);

        final EditText etObsComment = new EditText(this);
        etObsComment.setHint("Comments");
        layout.addView(etObsComment);

        // Nếu đang sửa thì điền dữ liệu cũ
        if (obsToEdit != null) {
            etObsName.setText(obsToEdit.getObsText());
            etObsTime.setText(obsToEdit.getTimestamp());
            etObsComment.setText(obsToEdit.getComments());
        }

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = etObsName.getText().toString();
            String time = etObsTime.getText().toString();
            String comment = etObsComment.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (obsToEdit == null) {
                // Thêm mới
                Observation newObs = new Observation();
                newObs.setHikeId(hikeId);
                newObs.setObsText(name);
                newObs.setTimestamp(time);
                newObs.setComments(comment);
                obsDAO.insert(newObs);
                Toast.makeText(this, "Added observation", Toast.LENGTH_SHORT).show();
            } else {
                // Sửa
                obsToEdit.setObsText(name);
                obsToEdit.setTimestamp(time);
                obsToEdit.setComments(comment);
                obsDAO.update(obsToEdit);
                Toast.makeText(this, "Updated observation", Toast.LENGTH_SHORT).show();
            }
            loadObservations(); // Refresh list ngay lập tức
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                    tvDate.setText(selectedDate);
                }, year, month, day);
        dialog.show();
    }

    private void dispatchTakePictureIntent() {
        try {
            File photoFile = createImageFile();
            photoURI = FileProvider.getUriForFile(this,
                    "com.example.m_hike.fileprovider",
                    photoFile);
            takePictureLauncher.launch(photoURI);
        } catch (IOException ex) {
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void saveHike() {
        String name = edtName.getText().toString();
        String location = edtLocation.getText().toString();
        String date = tvDate.getText().toString();
        boolean parking = swParking.isChecked();
        String length = edtLength.getText().toString();
        String difficulty = edtDifficulty.getText().toString();
        String description = edtDescription.getText().toString();
        String extra1 = edtExtra1.getText().toString();
        String extra2 = edtExtra2.getText().toString();

        if (name.isEmpty() || location.isEmpty() || date.isEmpty() || length.isEmpty() || difficulty.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Hike hike = new Hike(name, location, date, parking, length, difficulty, description, extra1, extra2, currentPhotoPath);
        HikeDAO dao = new HikeDAO(this);

        if (hikeId == -1) {
            // Create
            long result = dao.insert(hike);
            if (result != -1) {
                Toast.makeText(this, "Saved successfully! You can now add observations.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update
            hike.setId(hikeId);
            int rowsAffected = dao.update(hike);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}