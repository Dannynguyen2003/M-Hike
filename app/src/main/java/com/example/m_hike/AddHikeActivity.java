package com.example.m_hike;

import android.app.DatePickerDialog; // Import thêm cái này
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import java.util.Calendar; // Import thêm cái này
import java.util.Date;
import java.util.Locale;

public class AddHikeActivity extends AppCompatActivity {

    private ImageView ivHikeImage;
    private TextView tvDate; // Đưa tvDate ra biến toàn cục để dùng ở nhiều chỗ
    private String currentPhotoPath;
    private Uri photoURI;

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

        ivHikeImage = findViewById(R.id.ivHikeImage);
        tvDate = findViewById(R.id.tvDate); // Ánh xạ ngay tại đây

        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        Button btnSave = findViewById(R.id.btnSaveHike);

        // --- PHẦN MỚI: TỰ ĐỘNG LẤY NGÀY HIỆN TẠI ---
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvDate.setText(currentDate);

        // --- PHẦN MỚI: BẤM VÀO ĐỂ CHỌN NGÀY KHÁC ---
        tvDate.setOnClickListener(v -> showDatePicker());

        btnTakePhoto.setOnClickListener(v -> dispatchTakePictureIntent());
        btnSave.setOnClickListener(v -> saveHike());
    }

    // Hàm hiển thị lịch chọn ngày
    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    // Lưu ý: tháng trong Android bắt đầu từ 0 nên phải +1
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
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void saveHike() {
        EditText edtName = findViewById(R.id.etName);
        EditText edtLocation = findViewById(R.id.etLocation);
        // tvDate đã được khai báo ở trên nên không cần findViewById lại
        SwitchMaterial swParking = findViewById(R.id.swParking);
        EditText edtLength = findViewById(R.id.etLength);
        EditText edtDifficulty = findViewById(R.id.etDifficulty);
        EditText edtDescription = findViewById(R.id.etDescription);
        EditText edtExtra1 = findViewById(R.id.etExtra1);
        EditText edtExtra2 = findViewById(R.id.etExtra2);

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

        Hike newHike = new Hike(name, location, date, parking, length, difficulty, description, extra1, extra2, currentPhotoPath);

        HikeDAO dao = new HikeDAO(this);
        long result = dao.insert(newHike);

        if (result != -1) {
            Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
        }
    }
}