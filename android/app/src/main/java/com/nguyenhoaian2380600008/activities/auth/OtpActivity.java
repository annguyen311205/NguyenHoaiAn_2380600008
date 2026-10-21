package com.nguyenhoaian2380600008.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

public class OtpActivity extends AppCompatActivity {

    private ImageView btnOtpBack;
    private TextView tvOtpTargetEmail;
    private TextInputLayout tilOtpCode;
    private TextInputEditText edtOtpCode;
    private MaterialButton btnQuickFillOtp;
    private TextView tvCountdown;
    private TextView tvResendOtp;
    private MaterialButton btnVerifyOtp;

    private CountDownTimer countDownTimer;
    private String email = "nguyenhoaian@hutech.edu.vn";
    private String action = "LOGIN";
    private NguoiDung currentUser;

    private MockDataRepository repository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        repository = MockDataRepository.getInstance();
        sessionManager = SessionManager.getInstance(this);

        initViews();
        getIntentData();
        setupEvents();
        startOtpCountdown();

        String activeOtp = repository.getLastDemoOtp();
        btnQuickFillOtp.setText("⚡ Điền nhanh OTP từ Server (" + activeOtp + ")");
        Toast.makeText(this, "Mã OTP từ Server API là: " + activeOtp, Toast.LENGTH_LONG).show();
    }

    private void initViews() {
        btnOtpBack = findViewById(R.id.btnOtpBack);
        tvOtpTargetEmail = findViewById(R.id.tvOtpTargetEmail);
        tilOtpCode = findViewById(R.id.tilOtpCode);
        edtOtpCode = findViewById(R.id.edtOtpCode);
        btnQuickFillOtp = findViewById(R.id.btnQuickFillOtp);
        tvCountdown = findViewById(R.id.tvCountdown);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
    }

    private void getIntentData() {
        if (getIntent().hasExtra("EMAIL")) {
            email = getIntent().getStringExtra("EMAIL");
        }
        if (getIntent().hasExtra("ACTION")) {
            action = getIntent().getStringExtra("ACTION");
        }
        if (getIntent().hasExtra("USER_OBJ")) {
            currentUser = (NguoiDung) getIntent().getSerializableExtra("USER_OBJ");
        }

        tvOtpTargetEmail.setText("Mã xác thực 6 số đã gửi tới:\n" + email);
    }

    private void setupEvents() {
        btnOtpBack.setOnClickListener(v -> finish());

        // Điền nhanh mã OTP từ Server API
        btnQuickFillOtp.setOnClickListener(v -> {
            String otpToFill = repository.getLastDemoOtp();
            edtOtpCode.setText(otpToFill);
            edtOtpCode.setSelection(otpToFill.length());
            tilOtpCode.setError(null);
            Toast.makeText(this, "Đã điền mã OTP: " + otpToFill, Toast.LENGTH_SHORT).show();
        });

        // Gửi lại mã OTP
        tvResendOtp.setOnClickListener(v -> {
            String newOtp = repository.guiOTP(email);
            btnQuickFillOtp.setText("⚡ Điền nhanh OTP từ Server (" + newOtp + ")");
            Toast.makeText(this, "Mã OTP mới từ Server là: " + newOtp, Toast.LENGTH_LONG).show();
            startOtpCountdown();
        });

        // Xác nhận OTP
        btnVerifyOtp.setOnClickListener(v -> handleVerifyOtp());
    }

    private void startOtpCountdown() {
        tvResendOtp.setEnabled(false);
        tvResendOtp.setTextColor(ContextCompat.getColor(this, R.color.text_muted));

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                tvCountdown.setText("Gửi lại mã sau: " + seconds + "s");
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("Bạn chưa nhận được mã?");
                tvResendOtp.setEnabled(true);
                tvResendOtp.setTextColor(ContextCompat.getColor(OtpActivity.this, R.color.primary));
            }
        }.start();
    }

    private void handleVerifyOtp() {
        String otpInput = edtOtpCode.getText() != null ? edtOtpCode.getText().toString().trim() : "";
        tilOtpCode.setError(null);

        if (TextUtils.isEmpty(otpInput)) {
            tilOtpCode.setError("Vui lòng nhập mã OTP 6 chữ số");
            edtOtpCode.requestFocus();
            return;
        }

        if (otpInput.length() != 6) {
            tilOtpCode.setError("Mã OTP phải có đúng 6 chữ số");
            edtOtpCode.requestFocus();
            return;
        }

        // Xác thực mã OTP qua Repository (/api/Authenticate/verify-otp)
        boolean isValid = repository.xacThucOTP(email, otpInput);
        if (!isValid) {
            tilOtpCode.setError("Mã OTP không chính xác. Vui lòng nhập '" + repository.getLastDemoOtp() + "'");
            return;
        }

        // Lưu phiên đăng nhập
        if (currentUser == null) {
            currentUser = repository.timNguoiDungTheoEmail(email);
        }

        if (currentUser != null) {
            if ("REGISTER".equalsIgnoreCase(action) && currentUser.getMatKhau() != null && !currentUser.getMatKhau().isEmpty()) {
                NguoiDung loggedIn = repository.dangNhap(currentUser.getTenDangNhap(), currentUser.getMatKhau());
                if (loggedIn != null) {
                    currentUser = loggedIn;
                }
            }
            String tokenToSave = (currentUser.getToken() != null && !currentUser.getToken().isEmpty())
                    ? currentUser.getToken()
                    : "TOKEN_AUTH_" + System.currentTimeMillis();
            sessionManager.saveUserLogin(currentUser, tokenToSave);
            navigateByRole(currentUser.getVaiTro());
        } else {
            // Mặc định sinh viên nếu không tìm thấy
            navigateByRole("SINHVIEN");
        }
    }

    /**
     * Điều hướng theo VaiTro theo đúng yêu cầu đề bài:
     * - "ADMIN" -> AdminMainActivity
     * - "GIANGVIEN" -> GiangVienDashboardActivity
     * - "SINHVIEN" -> SinhVienMainActivity
     */
    private void navigateByRole(String vaiTro) {
        Intent targetIntent;
        if (vaiTro != null && vaiTro.equalsIgnoreCase("ADMIN")) {
            targetIntent = new Intent(this, AdminMainActivity.class);
            Toast.makeText(this, "Đăng nhập thành công với quyền Quản trị viên (Admin)!", Toast.LENGTH_SHORT).show();
        } else if (vaiTro != null && vaiTro.equalsIgnoreCase("GIANGVIEN")) {
            targetIntent = new Intent(this, GiangVienDashboardActivity.class);
            Toast.makeText(this, "Đăng nhập thành công với quyền Giảng viên!", Toast.LENGTH_SHORT).show();
        } else {
            targetIntent = new Intent(this, SinhVienMainActivity.class);
            Toast.makeText(this, "Đăng nhập thành công với quyền Sinh viên!", Toast.LENGTH_SHORT).show();
        }

        targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(targetIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }
}
