package com.nguyenhoaian2380600008.activities.giangvien;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.nguyenhoaian2380600008.R;
import com.nguyenhoaian2380600008.api.MockDataRepository;
import com.nguyenhoaian2380600008.models.ChiTietDiemDanh;
import com.nguyenhoaian2380600008.models.PhienDiemDanh;
import java.util.List;
import java.util.Locale;

public class TaoPhienQRActivity extends AppCompatActivity {

    private ImageView btnQrBack, imgQrCode;
    private LinearProgressIndicator pbQrRefresh;
    private TextView tvQrCountdown, tvDynamicQrToken, tvQrClassInfo, tvGvGpsLocation;
    private MaterialButton btnRefreshQrNow, btnViewStudentsInSession, btnFetchRealGps, btnCloseSession;

    private FusedLocationProviderClient fusedLocationClient;
    private MockDataRepository repository;

    private String maPhien = "BUOI_3";
    private String maLHP = "24DTH01_LTDD";
    private int soThuTuBuoi = 3;
    private String ghiChuBuoi = "Buổi 3: Thực hành Điểm danh Trực tiếp";
    private String phongHoc = "A-04.05";
    private double banKinhMet = 150.0;
    private double viDoGV = 10.8018;
    private double kinhDoGV = 106.7142;
    private String currentMaQR = "QR_HUTECH_LTDD_BUOI03";

    private CountDownTimer qrRefreshTimer;
    private static final int REFRESH_INTERVAL_SECONDS = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tao_phien_qr);

        repository = MockDataRepository.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        getIntentData();
        setupEvents();
        drawQrBitmap(currentMaQR);
        startLiveAttendanceSyncTimer();
    }

    private void initViews() {
        btnQrBack = findViewById(R.id.btnQrBack);
        imgQrCode = findViewById(R.id.imgQrCode);
        pbQrRefresh = findViewById(R.id.pbQrRefresh);
        tvQrCountdown = findViewById(R.id.tvQrCountdown);
        tvDynamicQrToken = findViewById(R.id.tvDynamicQrToken);
        tvQrClassInfo = findViewById(R.id.tvQrClassInfo);
        tvGvGpsLocation = findViewById(R.id.tvGvGpsLocation);
        btnRefreshQrNow = findViewById(R.id.btnRefreshQrNow);
        btnViewStudentsInSession = findViewById(R.id.btnMockGpsLocation);
        btnFetchRealGps = findViewById(R.id.btnFetchRealGps);
        btnCloseSession = findViewById(R.id.btnCloseSession);
    }

    private void getIntentData() {
        if (getIntent().hasExtra("MA_PHIEN")) {
            maPhien = getIntent().getStringExtra("MA_PHIEN");
        }
        if (getIntent().hasExtra("MA_LHP")) {
            maLHP = getIntent().getStringExtra("MA_LHP");
        }

        PhienDiemDanh phien = repository.getPhienTheoMa(maPhien);
        if (phien != null) {
            viDoGV = phien.getViDoGV();
            kinhDoGV = phien.getKinhDoGV();
            maLHP = phien.getMaLHP();
            soThuTuBuoi = phien.getSoThuTuBuoi();
            ghiChuBuoi = phien.getGhiChu();
            phongHoc = phien.getPhongHoc() != null && !phien.getPhongHoc().isEmpty() ? phien.getPhongHoc() : "A-04.05";
            banKinhMet = phien.getBanKinhChoPhepMet();
            if (phien.getMaQR() != null && !phien.getMaQR().isEmpty()) {
                currentMaQR = phien.getMaQR();
            }
        }

        tvQrClassInfo.setText("📚 " + maLHP + " • " + ghiChuBuoi);
        updateLocationDisplay();
    }

    private void updateLocationDisplay() {
        List<ChiTietDiemDanh> list = repository.getChiTietTheoPhien(maPhien);
        int presentCount = 0;
        for (ChiTietDiemDanh ct : list) {
            if ("CoMat".equalsIgnoreCase(ct.getTrangThai()) || "DiTre".equalsIgnoreCase(ct.getTrangThai())) {
                presentCount++;
            }
        }
        tvGvGpsLocation.setText(String.format(Locale.US,
                "📍 Phòng %s • GPS: %.4f, %.4f (Bán kính: %dm)\n👥 Đã điểm danh trên Server: %d / %d SV",
                phongHoc, viDoGV, kinhDoGV, (int) banKinhMet, presentCount, list.size()));
    }

    private void setupEvents() {
        btnQrBack.setOnClickListener(v -> finish());

        // Đổi mã QR mới trên Server khi Giảng viên bấm nút
        btnRefreshQrNow.setOnClickListener(v -> {
            String cleanCode = String.format(Locale.US, "QR_HUTECH_LTDD_BUOI%02d_%04d",
                    soThuTuBuoi, System.currentTimeMillis() % 10000);
            currentMaQR = cleanCode;
            repository.capNhatMaQRPhien(maPhien, currentMaQR);
            drawQrBitmap(currentMaQR);
            Toast.makeText(this, "Đã tạo mã QR mới trên Server: " + currentMaQR, Toast.LENGTH_SHORT).show();
        });

        // Xem danh sách sinh viên đã điểm danh trong buổi học này
        btnViewStudentsInSession.setOnClickListener(v -> showCheckedInStudentsDialog());

        // Lấy tọa độ GPS thật từ thiết bị của Giảng viên
        btnFetchRealGps.setOnClickListener(v -> fetchRealGpsLocation());

        // Đóng phiên điểm danh
        btnCloseSession.setOnClickListener(v -> {
            repository.dongPhienDiemDanh(maPhien);
            Toast.makeText(this, "Đã đóng Buổi " + soThuTuBuoi + " (" + maLHP + ")", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showCheckedInStudentsDialog() {
        List<ChiTietDiemDanh> list = repository.getChiTietTheoPhien(maPhien);
        StringBuilder sb = new StringBuilder();
        for (ChiTietDiemDanh ct : list) {
            String st = ct.getTrangThai();
            String icon = "CoMat".equalsIgnoreCase(st) ? "✅ Có mặt"
                    : ("DiTre".equalsIgnoreCase(st) ? "⏰ Đi trễ"
                    : ("VangCoPhep".equalsIgnoreCase(st) ? "📝 Vắng CP" : "❌ Chưa điểm danh"));
            sb.append("• ").append(ct.getTenSV()).append(" (").append(ct.getMaSV()).append("): ").append(icon);
            if (ct.getThoiGianDiemDanh() != null && !ct.getThoiGianDiemDanh().isEmpty()) {
                sb.append("\n   🕒 ").append(ct.getThoiGianDiemDanh()).append(" (").append(ct.getPhuongThuc()).append(")");
            }
            sb.append("\n\n");
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle("Danh Sách Điểm Danh - Buổi " + soThuTuBuoi)
                .setMessage(sb.toString().trim())
                .setPositiveButton("Đóng", null)
                .show();
    }

    private void startLiveAttendanceSyncTimer() {
        if (qrRefreshTimer != null) {
            qrRefreshTimer.cancel();
        }

        pbQrRefresh.setMax(REFRESH_INTERVAL_SECONDS);
        pbQrRefresh.setProgress(REFRESH_INTERVAL_SECONDS);

        qrRefreshTimer = new CountDownTimer(REFRESH_INTERVAL_SECONDS * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000L);
                tvQrCountdown.setText("🔄 Tự động đồng bộ danh sách SV từ Server sau: " + secondsRemaining + "s");
                pbQrRefresh.setProgress(secondsRemaining);
            }

            @Override
            public void onFinish() {
                updateLocationDisplay();
                startLiveAttendanceSyncTimer();
            }
        }.start();
    }

    private void drawQrBitmap(String qrContent) {
        tvDynamicQrToken.setText("🔑 Mã QR trên Server: " + qrContent);
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, 600, 600);
            imgQrCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi vẽ mã QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchRealGpsLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                viDoGV = location.getLatitude();
                kinhDoGV = location.getLongitude();
                repository.capNhatToaDoPhien(maPhien, viDoGV, kinhDoGV);
                updateLocationDisplay();
                Toast.makeText(this, "Đã cập nhật tọa độ GPS thiết bị lên Server!", Toast.LENGTH_SHORT).show();
            } else {
                updateLocationDisplay();
                Toast.makeText(this, "Đang sử dụng tọa độ GPS phòng học trên Server (" + viDoGV + ", " + kinhDoGV + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (qrRefreshTimer != null) {
            qrRefreshTimer.cancel();
        }
        super.onDestroy();
    }
}
