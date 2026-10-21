package com.nguyenhoaian2380600008.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nguyenhoaian2380600008.R;
import com.nguyenhoaian2380600008.api.MockDataRepository;
import com.nguyenhoaian2380600008.models.NguoiDung;

public class RegisterActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputLayout tilRegUsername, tilRegFullName, tilRegEmail, tilRegPassword, tilRegConfirmPassword;
    private TextInputEditText edtRegUsername, edtRegFullName, edtRegEmail, edtRegPassword, edtRegConfirmPassword;
    private RadioGroup rgRole;
    private MaterialButton btnRegisterSubmit;
    private TextView tvBackToLogin;

    private MockDataRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        repository = MockDataRepository.getInstance();

        initViews();
        setupEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tilRegUsername = findViewById(R.id.tilRegUsername);
        tilRegFullName = findViewById(R.id.tilRegFullName);
        tilRegEmail = findViewById(R.id.tilRegEmail);
        tilRegPassword = findViewById(R.id.tilRegPassword);
        tilRegConfirmPassword = findViewById(R.id.tilRegConfirmPassword);

        edtRegUsername = findViewById(R.id.edtRegUsername);
        edtRegFullName = findViewById(R.id.edtRegFullName);
        edtRegEmail = findViewById(R.id.edtRegEmail);
        edtRegPassword = findViewById(R.id.edtRegPassword);
        edtRegConfirmPassword = findViewById(R.id.edtRegConfirmPassword);

        rgRole = findViewById(R.id.rgRole);
        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        tvBackToLogin.setOnClickListener(v -> finish());

        btnRegisterSubmit.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String username = edtRegUsername.getText() != null ? edtRegUsername.getText().toString().trim() : "";
        String fullName = edtRegFullName.getText() != null ? edtRegFullName.getText().toString().trim() : "";
        String email = edtRegEmail.getText() != null ? edtRegEmail.getText().toString().trim() : "";
        String password = edtRegPassword.getText() != null ? edtRegPassword.getText().toString().trim() : "";
        String confirmPassword = edtRegConfirmPassword.getText() != null ? edtRegConfirmPassword.getText().toString().trim() : "";

        // Reset errors
        tilRegUsername.setError(null);
        tilRegFullName.setError(null);
        tilRegEmail.setError(null);
        tilRegPassword.setError(null);
        tilRegConfirmPassword.setError(null);

        // Validation
        if (TextUtils.isEmpty(username)) {
            tilRegUsername.setError("Vui lòng nhập tên đăng nhập hoặc Mã SV");
            edtRegUsername.requestFocus();
            return;
        }

        if (username.length() < 3) {
            tilRegUsername.setError("Tên đăng nhập phải có ít nhất 3 ký tự");
            edtRegUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(fullName)) {
            tilRegFullName.setError("Vui lòng nhập họ và tên");
            edtRegFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            tilRegEmail.setError("Vui lòng nhập địa chỉ email");
            edtRegEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilRegEmail.setError("Định dạng email không hợp lệ (VD: user@hutech.edu.vn)");
            edtRegEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            tilRegPassword.setError("Vui lòng nhập mật khẩu");
            edtRegPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            tilRegPassword.setError("Mật khẩu phải có tối thiểu 6 ký tự");
            edtRegPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            tilRegConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            edtRegConfirmPassword.requestFocus();
            return;
        }

        String vaiTro = "SINHVIEN";
        int checkedId = rgRole.getCheckedRadioButtonId();
        if (checkedId == R.id.rbGiangVien) {
            vaiTro = "GIANGVIEN";
        }

        // Tạo đối tượng NguoiDung mới
        String maND = "ND_" + System.currentTimeMillis();
        String maThietBi = "DEV_" + System.currentTimeMillis();
        NguoiDung newUser = new NguoiDung(
                maND,
                username,
                password,
                fullName,
                email,
                vaiTro,
                maThietBi,
                ""
        );

        // Đăng ký trực tiếp lên Server API (/api/Authenticate/register) và lấy mã OTP thật
        boolean registered = repository.dangKy(newUser);
        if (!registered) {
            Toast.makeText(this, "Tên đăng nhập hoặc Email đã tồn tại trên hệ thống Server!", Toast.LENGTH_LONG).show();
            return;
        }

        String generatedOtp = repository.getLastDemoOtp();
        Toast.makeText(this, "Đăng ký thành công! Mã OTP từ Server: " + generatedOtp, Toast.LENGTH_LONG).show();

        // Chuyển sang màn hình xác thực OTP
        Intent intent = new Intent(this, OtpActivity.class);
        intent.putExtra("ACTION", "REGISTER");
        intent.putExtra("EMAIL", email);
        intent.putExtra("USER_OBJ", newUser);
        startActivity(intent);
    }
}
