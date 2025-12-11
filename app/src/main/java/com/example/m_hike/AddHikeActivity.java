package com.example.m_hike;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.m_hike.database.HikeDAO;
import com.example.m_hike.models.Hike;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddHikeActivity extends AppCompatActivity {

    // Khai báo biến toàn cục để dùng chung cho việc Load dữ liệu và Save
    private ImageView ivHikeImage;
    private TextView tvDate;
    private EditText edtName, edtLocation, edtLength, edtDifficulty, edtDescription, edtExtra1, edtExtra2;
    private SwitchMaterial swParking;
    private Button btnSave;

    private String currentPhotoPath;
    private Uri photoURI;

    // Biến lưu ID của chuyến đi (Mặc định -1 nghĩa là đang Thêm mới)
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

        // 1. Ánh xạ View (Tách ra hàm riêng cho gọn)
        initViews();

        // 2. Thiết lập ngày mặc định
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvDate.setText(currentDate);

        // 3. Xử lý sự kiện
        tvDate.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.btnTakePhoto).setOnClickListener(v -> dispatchTakePictureIntent());
        btnSave.setOnClickListener(v -> saveHike());

        // 4. QUAN TRỌNG: Kiểm tra xem có phải đang sửa (Edit) không để điền dữ liệu cũ vào
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
    }

    // Hàm kiểm tra Intent gửi sang
    private void checkForEditMode() {
        Intent intent = getIntent();
        // Kiểm tra xem có ID được gửi sang không (key phải khớp với bên Adapter gửi)
        if (intent != null && intent.hasExtra("id")) {
            // Lấy ID ra lưu lại
            hikeId = intent.getLongExtra("id", -1);

            // Đổi tên nút Save thành Update cho dễ nhìn
            btnSave.setText("Update Hike");

            // Điền dữ liệu cũ vào các ô nhập liệu
            edtName.setText(intent.getStringExtra("name"));
            edtLocation.setText(intent.getStringExtra("location"));
            tvDate.setText(intent.getStringExtra("date"));
            swParking.setChecked(intent.getBooleanExtra("parking", false));
            edtLength.setText(intent.getStringExtra("length"));
            edtDifficulty.setText(intent.getStringExtra("difficulty"));
            edtDescription.setText(intent.getStringExtra("description"));
            edtExtra1.setText(intent.getStringExtra("extra1")); // Nếu có gửi
            edtExtra2.setText(intent.getStringExtra("extra2")); // Nếu có gửi

            // Load ảnh cũ (nếu có)
            currentPhotoPath = intent.getStringExtra("photo_path");
            if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
                File imgFile = new File(currentPhotoPath);
                if (imgFile.exists()) {
                    ivHikeImage.setImageURI(Uri.fromFile(imgFile));
                }
            }
        }
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
        // Lấy dữ liệu từ các ô nhập liệu
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

        // Tạo object Hike mới chứa thông tin mới nhập
        Hike hike = new Hike(name, location, date, parking, length, difficulty, description, extra1, extra2, currentPhotoPath);

        HikeDAO dao = new HikeDAO(this);

        if (hikeId == -1) {
            // --- TRƯỜNG HỢP INSERT (THÊM MỚI) ---
            long result = dao.insert(hike);
            if (result != -1) {
                Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            // --- TRƯỜNG HỢP UPDATE (SỬA) ---

            // CỰC KỲ QUAN TRỌNG: Gán lại ID cũ cho object mới để Room biết sửa dòng nào
            hike.setId(hikeId);

            // Gọi hàm update (Hàm này trả về số dòng bị ảnh hưởng, thường là kiểu int)
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