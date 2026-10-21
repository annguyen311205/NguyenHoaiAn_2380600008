package com.nguyenhoaian2380600008.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nguyenhoaian2380600008.R;
import com.nguyenhoaian2380600008.activities.admin.AdminMainActivity;
import com.nguyenhoaian2380600008.activities.giangvien.GiangVienDashboardActivity;
import com.nguyenhoaian2380600008.activities.sinhvien.SinhVienMainActivity;
import com.nguyenhoaian2380600008.api.MockDataRepository;
import com.nguyenhoaian2380600008.models.NguoiDung;
import com.nguyenhoaian2380600008.utils.SessionManager;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText edtUsername, edtPassword;
    private MaterialButton btnLogin, btnFaceLogin;
    private TextView tvGoToRegister;

    // Các nút chọn nhanh tài khoản test demo
    private MaterialButton btnQuickAdmin, btnQuickGiangVien, btnQuickSinhVien;

    private MockDataRepository repository;
    private SessionManager sessionManager;
    private static io.flutter.embedding.engine.FlutterEngine backgroundFlutterEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo FlutterEngine nền để tương thích hoàn toàn khi chạy/debug bằng cấu hình Flutter (main.dart)
        if (backgroundFlutterEngine == null) {
            try {
                backgroundFlutterEngine = new io.flutter.embedding.engine.FlutterEngine(getApplicationContext());
                backgroundFlutterEngine.getDartExecutor().executeDartEntrypoint(
                        io.flutter.embedding.engine.dart.DartExecutor.DartEntrypoint.createDefault()
                );
            } catch (Throwable ignored) {
            }
        }

        sessionManager = SessionManager.getInstance(this);
        repository = MockDataRepository.getInstance();

        // Kiểm tra nếu đã đăng nhập trước đó thì chuyển tiếp vào màn hình chính
        if (sessionManager.isLoggedIn() && sessionManager.getCurrentUser() != null) {
            navigateDirectlyByRole(sessionManager.getUserRole());
            return;
        }

        setContentView(R.layout.activity_login);

        initViews();
        setupQuickTestAccounts();
        setupEvents();
    }

    private void initViews() {
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnFaceLogin = findViewById(R.id.btnFaceLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnQuickAdmin = findViewById(R.id.btnQuickAdmin);
        btnQuickGiangVien = findViewById(R.id.btnQuickGiangVien);
        btnQuickSinhVien = findViewById(R.id.btnQuickSinhVien);
    }

    /**
     * Cung cấp các nút chọn nhanh tài khoản trên Server API:
     * - Admin: admin / Admin@123
     * - Giảng viên: gv01 / Giangvien@123
     * - Sinh viên: 2380600008 / Sinhvien@123
     */
    private void setupQuickTestAccounts() {
        btnQuickAdmin.setOnClickListener(v -> {
            edtUsername.setText("admin");
            edtPassword.setText("Admin@123");
            tilUsername.setError(null);
            tilPassword.setError(null);
            Toast.makeText(this, "Đã chọn tài khoản Admin: admin / Admin@123", Toast.LENGTH_SHORT).show();
        });

        btnQuickGiangVien.setOnClickListener(v -> {
            edtUsername.setText("gv01");
            edtPassword.setText("Giangvien@123");
            tilUsername.setError(null);
            tilPassword.setError(null);
            Toast.makeText(this, "Đã chọn tài khoản Giảng viên: gv01 / Giangvien@123", Toast.LENGTH_SHORT).show();
        });

        btnQuickSinhVien.setOnClickListener(v -> {
            edtUsername.setText("2380600008");
            edtPassword.setText("Password@123");
            tilUsername.setError(null);
            tilPassword.setError(null);
            Toast.makeText(this, "Đã chọn tài khoản Sinh viên: 2380600008 / Password@123", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupEvents() {
        // Đăng nhập thường bằng Tên đăng nhập và Mật khẩu
        btnLogin.setOnClickListener(v -> handleLogin());

        // Đăng nhập nhanh bằng Khuôn mặt (Biometric / Face Detection)
        btnFaceLogin.setOnClickListener(v -> handleFaceLogin());

        // Chuyển sang màn hình Đăng ký
        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String username = edtUsername.getText() != null ? edtUsername.getText().toString().trim() : "";
        String password = edtPassword.getText() != null ? edtPassword.getText().toString().trim() : "";

        tilUsername.setError(null);
        tilPassword.setError(null);

        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Vui lòng nhập tên đăng nhập");
            edtUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        // Kiểm tra thông tin đăng nhập trong Repository
        NguoiDung user = repository.dangNhap(username, password);
        if (user != null) {
            Toast.makeText(this, "Đăng nhập thành công! Chuyển sang xác thực mã OTP gửi về Email.", Toast.LENGTH_SHORT).show();

            // Chuyển sang màn hình OTP để kiểm tra xác thực 2 lớp theo đúng luồng đề bài
            Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
            intent.putExtra("ACTION", "LOGIN");
            intent.putExtra("EMAIL", user.getEmail());
            intent.putExtra("USER_OBJ", user);
            startActivity(intent);
        } else {
            tilPassword.setError("Tên đăng nhập hoặc mật khẩu không chính xác!");
            Toast.makeText(this, "Đăng nhập thất bại. Vui lòng bấm chọn tài khoản mẫu bên dưới để thử nghiệm!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Chuyển sang màn hình Xác thực Khuôn mặt Google ML Kit Face Detection
     */
    private void handleFaceLogin() {
        Intent intent = new Intent(LoginActivity.this, FaceAuthActivity.class);
        startActivity(intent);
    }

    private void navigateDirectlyByRole(String vaiTro) {
        Intent targetIntent;
        if ("ADMIN".equalsIgnoreCase(vaiTro)) {
            targetIntent = new Intent(this, AdminMainActivity.class);
        } else if ("GIANGVIEN".equalsIgnoreCase(vaiTro)) {
            targetIntent = new Intent(this, GiangVienDashboardActivity.class);
        } else {
            targetIntent = new Intent(this, SinhVienMainActivity.class);
        }
        startActivity(targetIntent);
        finish();
    }
}
