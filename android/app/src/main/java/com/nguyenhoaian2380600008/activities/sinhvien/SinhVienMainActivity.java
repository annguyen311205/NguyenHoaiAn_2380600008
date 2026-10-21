package com.nguyenhoaian2380600008.activities.sinhvien;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.nguyenhoaian2380600008.R;
import com.nguyenhoaian2380600008.activities.auth.LoginActivity;
import com.nguyenhoaian2380600008.api.MockDataRepository;
import com.nguyenhoaian2380600008.models.ChiTietDiemDanh;
import com.nguyenhoaian2380600008.models.DonXinPhep;
import com.nguyenhoaian2380600008.models.NguoiDung;
import com.nguyenhoaian2380600008.utils.SessionManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SinhVienMainActivity extends AppCompatActivity {

    private TextView tvSvStudentId;
    private TextView tvSvWelcome;
    private TextView tvSvPresentCount;
    private TextView tvSvAttendanceRate;
    private TextView tvSvPendingLeaveBadge;
    private TextView tvAttendanceWarningMsg;
    private MaterialButton btnSvLogout;
    private MaterialCardView cardSvScanQr;
    private MaterialCardView cardSvSendLeaveRequest;
    private MaterialCardView cardAttendanceWarning;
    private LinearLayout layoutSvSubjectStats;
    private LinearLayout layoutSvAttendanceHistory;

    private SessionManager sessionManager;
    private MockDataRepository repository;

    private String studentId = "2380600008";
    private String studentName = "Nguyễn Hoài An";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinh_vien_main);

        sessionManager = SessionManager.getInstance(this);
        repository = MockDataRepository.getInstance();

        initViews();
        setupEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSinhVienData();
    }

    private void initViews() {
        tvSvStudentId = findViewById(R.id.tvSvStudentId);
        tvSvWelcome = findViewById(R.id.tvSvWelcome);
        tvSvPresentCount = findViewById(R.id.tvSvPresentCount);
        tvSvAttendanceRate = findViewById(R.id.tvSvAttendanceRate);
        tvSvPendingLeaveBadge = findViewById(R.id.tvSvPendingLeaveBadge);
        tvAttendanceWarningMsg = findViewById(R.id.tvAttendanceWarningMsg);
        btnSvLogout = findViewById(R.id.btnSvLogout);
        cardSvScanQr = findViewById(R.id.cardSvScanQr);
        cardSvSendLeaveRequest = findViewById(R.id.cardSvSendLeaveRequest);
        cardAttendanceWarning = findViewById(R.id.cardAttendanceWarning);
        layoutSvSubjectStats = findViewById(R.id.layoutSvSubjectStats);
        layoutSvAttendanceHistory = findViewById(R.id.layoutSvAttendanceHistory);
    }

    private void loadSinhVienData() {
        NguoiDung currentUser = sessionManager.getCurrentUser();
        if (currentUser != null) {
            studentId = currentUser.getTenDangNhap() != null ? currentUser.getTenDangNhap() : "2380600008";
            studentName = currentUser.getHoTen() != null ? currentUser.getHoTen() : "Nguyễn Hoài An";
        }

        // Lấy hồ sơ sinh viên thực tế từ GET /api/SinhVien/by-masv/{maSV}
        Map<String, Object> profile = repository.getThongTinSinhVienTheoMaSV(studentId);
        if (profile != null) {
            String apiName = String.valueOf(profile.get("hoTen"));
            String lop = String.valueOf(profile.get("lop"));
            String khoa = String.valueOf(profile.get("khoa"));
            if (apiName != null && !apiName.isEmpty() && !"null".equals(apiName)) {
                studentName = apiName;
            }
            tvSvStudentId.setText("MSSV: " + studentId + " • Lớp: " + lop + " • Khoa: " + khoa);
        } else {
            tvSvStudentId.setText("MSSV: " + studentId);
        }
        tvSvWelcome.setText(studentName);

        String[] stats = repository.getThongKeChuyenCanSV(studentId);
        tvSvPresentCount.setText(stats[0]);
        tvSvAttendanceRate.setText(stats[1]);

        updateLeaveBadge();
        checkAttendanceWarning();
        renderSubjectStatsFromApi();
        renderAttendanceHistoryFromApi();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    /**
     * Hiển thị danh sách thống kê từng môn học thực tế lấy từ GET /api/ThongKe/sinh-vien/{sinhVienId}
     */
    private void renderSubjectStatsFromApi() {
        if (layoutSvSubjectStats == null) return;
        layoutSvSubjectStats.removeAllViews();

        List<Map<String, Object>> subjects = repository.getDanhSachThongKeMonHocSV(studentId);
        if (subjects.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Chưa có dữ liệu môn học trên Web API.");
            tvEmpty.setTextColor(Color.parseColor("#64748B"));
            tvEmpty.setTextSize(13f);
            tvEmpty.setPadding(dp(4), dp(8), dp(4), dp(8));
            layoutSvSubjectStats.addView(tvEmpty);
            return;
        }

        for (Map<String, Object> item : subjects) {
            String maLHP = String.valueOf(item.get("maLopHocPhan"));
            String tenMon = String.valueOf(item.get("tenMon"));
            int tongMo = (int) item.get("tongBuoiDaMo");
            int coMat = (int) item.get("soBuoiCoMat");
            int vangCP = (int) item.get("soBuoiVangCoPhep");
            int vangKP = (int) item.get("soBuoiVangKhongPhep");
            double tiLeVang = (double) item.get("tiLeVang");
            double chuyenCan = Math.max(0.0, 100.0 - tiLeVang);

            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.topMargin = dp(8);
            card.setLayoutParams(cardParams);
            card.setRadius(dp(16));
            card.setCardElevation(dp(2));
            card.setCardBackgroundColor(Color.WHITE);

            LinearLayout container = new LinearLayout(this);
            container.setOrientation(LinearLayout.VERTICAL);
            container.setPadding(dp(14), dp(14), dp(14), dp(14));

            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvTitle = new TextView(this);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            );
            tvTitle.setLayoutParams(titleParams);
            tvTitle.setText("📚 " + tenMon + " (" + maLHP + ")");
            tvTitle.setTextColor(Color.parseColor("#0F172A"));
            tvTitle.setTextSize(14f);
            tvTitle.setTypeface(null, Typeface.BOLD);

            TextView tvRate = new TextView(this);
            int totalVang = vangCP + vangKP;
            tvRate.setText(String.format(Locale.US, "%.1f%%", chuyenCan));
            int badgeColor = totalVang == 0 ? Color.parseColor("#059669")
                    : (totalVang >= 2 ? Color.parseColor("#DC2626") : Color.parseColor("#D97706"));
            tvRate.setTextColor(badgeColor);
            tvRate.setTextSize(13.5f);
            tvRate.setTypeface(null, Typeface.BOLD);

            topRow.addView(tvTitle);
            topRow.addView(tvRate);

            TextView tvDetail = new TextView(this);
            tvDetail.setText("• Đã mở: " + tongMo + " buổi | Có mặt: " + coMat
                    + " | Vắng CP: " + vangCP + " | Vắng KP: " + vangKP + " (Tỷ lệ vắng: " + tiLeVang + "%)");
            tvDetail.setTextColor(Color.parseColor("#475569"));
            tvDetail.setTextSize(12f);
            tvDetail.setPadding(0, dp(4), 0, dp(2));

            LinearProgressIndicator progress = new LinearProgressIndicator(this);
            LinearLayout.LayoutParams progParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            progParams.topMargin = dp(6);
            progress.setLayoutParams(progParams);
            progress.setMax(100);
            progress.setProgress((int) Math.round(chuyenCan));
            progress.setIndicatorColor(badgeColor);
            progress.setTrackColor(Color.parseColor("#E2E8F0"));
            progress.setTrackCornerRadius(dp(4));

            container.addView(topRow);
            container.addView(tvDetail);
            container.addView(progress);
            card.addView(container);
            layoutSvSubjectStats.addView(card);
        }
    }

    /**
     * Hiển thị lịch sử điểm danh thực tế từ GET /api/DiemDanh/by-sinh-vien/{sinhVienId}
     */
    private void renderAttendanceHistoryFromApi() {
        if (layoutSvAttendanceHistory == null) return;
        layoutSvAttendanceHistory.removeAllViews();

        List<ChiTietDiemDanh> history = repository.getLichSuTheoSV(studentId);
        if (history.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Chưa có lượt điểm danh nào được ghi nhận trên Server.");
            tvEmpty.setTextColor(Color.parseColor("#64748B"));
            tvEmpty.setTextSize(13f);
            tvEmpty.setPadding(dp(4), dp(8), dp(4), dp(8));
            layoutSvAttendanceHistory.addView(tvEmpty);
            return;
        }

        for (ChiTietDiemDanh ct : history) {
            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.topMargin = dp(8);
            card.setLayoutParams(cardParams);
            card.setRadius(dp(14));
            card.setCardElevation(dp(1));
            card.setCardBackgroundColor(Color.WHITE);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(14), dp(12), dp(14), dp(12));

            LinearLayout infoCol = new LinearLayout(this);
            infoCol.setOrientation(LinearLayout.VERTICAL);
            infoCol.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            ));

            String tenBuoi = (ct.getTenBuoi() != null && !ct.getTenBuoi().isEmpty())
                    ? ct.getTenBuoi() : ("Buổi " + ct.getSoThuTuBuoi());
            TextView tvSession = new TextView(this);
            tvSession.setText("📌 " + tenBuoi);
            tvSession.setTextColor(Color.parseColor("#0F172A"));
            tvSession.setTextSize(13.5f);
            tvSession.setTypeface(null, Typeface.BOLD);

            TextView tvSubject = new TextView(this);
            tvSubject.setText("🏫 Lớp HP: " + ct.getMaLopHocPhan() + " • Phòng " + ct.getPhongHoc());
            tvSubject.setTextColor(Color.parseColor("#2563EB"));
            tvSubject.setTextSize(12f);
            tvSubject.setTypeface(null, Typeface.BOLD);
            tvSubject.setPadding(0, dp(2), 0, dp(2));

            TextView tvTime = new TextView(this);
            tvTime.setText("🕒 " + ct.getThoiGianDiemDanh() + " | 🔑 " + ct.getPhuongThuc()
                    + " (Cách " + String.format(Locale.US, "%.1fm", ct.getKhoangCachMet()) + ")");
            tvTime.setTextColor(Color.parseColor("#475569"));
            tvTime.setTextSize(12f);

            infoCol.addView(tvSession);
            infoCol.addView(tvSubject);
            infoCol.addView(tvTime);

            if (ct.getGhiChu() != null && !ct.getGhiChu().trim().isEmpty()
                    && !ct.getGhiChu().startsWith("DEV_SV_")) {
                TextView tvNote = new TextView(this);
                tvNote.setText("📝 " + ct.getGhiChu());
                tvNote.setTextColor(Color.parseColor("#64748B"));
                tvNote.setTextSize(11.5f);
                tvNote.setPadding(0, dp(2), 0, 0);
                infoCol.addView(tvNote);
            }

            TextView tvStatus = new TextView(this);
            String st = ct.getTrangThai() != null ? ct.getTrangThai() : "CoMat";
            if ("CoMat".equalsIgnoreCase(st)) {
                tvStatus.setText("CÓ MẶT");
                tvStatus.setTextColor(Color.parseColor("#059669"));
            } else if ("DiTre".equalsIgnoreCase(st)) {
                tvStatus.setText("ĐI TRỄ");
                tvStatus.setTextColor(Color.parseColor("#D97706"));
            } else if ("VangCoPhep".equalsIgnoreCase(st)) {
                tvStatus.setText("VẮNG CP");
                tvStatus.setTextColor(Color.parseColor("#2563EB"));
            } else {
                tvStatus.setText("VẮNG KP");
                tvStatus.setTextColor(Color.parseColor("#DC2626"));
            }
            tvStatus.setTextSize(12f);
            tvStatus.setTypeface(null, Typeface.BOLD);
            tvStatus.setPadding(dp(8), 0, 0, 0);

            row.addView(infoCol);
            row.addView(tvStatus);
            card.addView(row);
            layoutSvAttendanceHistory.addView(card);
        }
    }

    private void checkAttendanceWarning() {
        String warning = repository.kiemTraNguyCoCamThi(studentId);
        if (warning != null) {
            cardAttendanceWarning.setVisibility(View.VISIBLE);
            tvAttendanceWarningMsg.setText(warning);
        } else {
            cardAttendanceWarning.setVisibility(View.GONE);
        }
    }

    private void updateLeaveBadge() {
        List<DonXinPhep> list = repository.getDonXinPhepTheoSV(studentId);
        int pendingCount = 0;
        for (DonXinPhep d : list) {
            if ("ChoDuyet".equalsIgnoreCase(d.getTrangThaiDuyet())) {
                pendingCount++;
            }
        }
        tvSvPendingLeaveBadge.setText(list.size() + " Đơn (" + pendingCount + " chờ)");
    }

    private void setupEvents() {
        btnSvLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Toast.makeText(this, "Đã đăng xuất tài khoản Sinh viên!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        cardSvScanQr.setOnClickListener(v -> {
            Intent intent = new Intent(SinhVienMainActivity.this, QuetQRDiemDanhActivity.class);
            startActivity(intent);
        });

        cardSvSendLeaveRequest.setOnClickListener(v -> {
            Intent intent = new Intent(SinhVienMainActivity.this, DonXinPhepActivity.class);
            startActivity(intent);
        });
    }
}
