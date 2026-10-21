package com.nguyenhoaian2380600008.activities.giangvien;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.nguyenhoaian2380600008.R;
import com.nguyenhoaian2380600008.activities.auth.LoginActivity;
import com.nguyenhoaian2380600008.adapters.ChiTietDiemDanhAdapter;
import com.nguyenhoaian2380600008.adapters.DuyetDonAdapter;
import com.nguyenhoaian2380600008.adapters.PhienDiemDanhAdapter;
import com.nguyenhoaian2380600008.api.MockDataRepository;
import com.nguyenhoaian2380600008.models.ChiTietDiemDanh;
import com.nguyenhoaian2380600008.models.DonXinPhep;
import com.nguyenhoaian2380600008.models.LopHocPhan;
import com.nguyenhoaian2380600008.models.NguoiDung;
import com.nguyenhoaian2380600008.models.PhienDiemDanh;
import com.nguyenhoaian2380600008.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class GiangVienDashboardActivity extends AppCompatActivity
        implements PhienDiemDanhAdapter.OnPhienListener, DuyetDonAdapter.OnDuyetDonListener {

    private TextView tvGvWelcome;
    private MaterialButton btnGvLogout;
    private MaterialButton btnGvTabPhien, btnGvTabDuyetDon;
    private LinearLayout layoutTabPhien, layoutTabDuyetDon;
    private RecyclerView rvGvPhienDiemDanh, rvGvDuyetDon;
    private TextView tvEmptyPhien, tvEmptyDon;
    private ExtendedFloatingActionButton fabGvCreateSession;

    private SessionManager sessionManager;
    private MockDataRepository repository;

    private PhienDiemDanhAdapter phienAdapter;
    private DuyetDonAdapter duyetDonAdapter;

    private boolean isTabPhien = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giang_vien_dashboard);

        sessionManager = SessionManager.getInstance(this);
        repository = MockDataRepository.getInstance();

        initViews();
        setupRecyclerViews();
        setupEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void initViews() {
        tvGvWelcome = findViewById(R.id.tvGvWelcome);
        btnGvLogout = findViewById(R.id.btnGvLogout);
        btnGvTabPhien = findViewById(R.id.btnGvTabPhien);
        btnGvTabDuyetDon = findViewById(R.id.btnGvTabDuyetDon);
        layoutTabPhien = findViewById(R.id.layoutTabPhien);
        layoutTabDuyetDon = findViewById(R.id.layoutTabDuyetDon);
        rvGvPhienDiemDanh = findViewById(R.id.rvGvPhienDiemDanh);
        rvGvDuyetDon = findViewById(R.id.rvGvDuyetDon);
        tvEmptyPhien = findViewById(R.id.tvEmptyPhien);
        tvEmptyDon = findViewById(R.id.tvEmptyDon);
        fabGvCreateSession = findViewById(R.id.fabGvCreateSession);
    }

    private void setupRecyclerViews() {
        rvGvPhienDiemDanh.setLayoutManager(new LinearLayoutManager(this));
        phienAdapter = new PhienDiemDanhAdapter(new ArrayList<>(), this);
        rvGvPhienDiemDanh.setAdapter(phienAdapter);

        rvGvDuyetDon.setLayoutManager(new LinearLayoutManager(this));
        duyetDonAdapter = new DuyetDonAdapter(new ArrayList<>(), this);
        rvGvDuyetDon.setAdapter(duyetDonAdapter);
    }

    private List<PhienDiemDanh> getDanhSachPhien() {
        return repository.getAllPhienDiemDanh();
    }

    private void loadDashboardData() {
        NguoiDung currentUser = sessionManager.getCurrentUser();
        if (currentUser != null && currentUser.getHoTen() != null && !currentUser.getHoTen().isEmpty()) {
            tvGvWelcome.setText("Xin chào, " + currentUser.getHoTen());
        } else {
            tvGvWelcome.setText("Xin chào, ThS. Nguyễn Hữu Trung");
        }

        List<PhienDiemDanh> phienList = getDanhSachPhien();
        phienAdapter.updateData(phienList);
        tvEmptyPhien.setVisibility(phienList.isEmpty() ? View.VISIBLE : View.GONE);

        List<DonXinPhep> donList = repository.getDanhSachDonXinPhep();
        duyetDonAdapter.updateData(donList);
        tvEmptyDon.setVisibility(donList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setupEvents() {
        btnGvLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Toast.makeText(this, "Đã đăng xuất tài khoản Giảng viên!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnGvTabPhien.setOnClickListener(v -> switchTab(true));
        btnGvTabDuyetDon.setOnClickListener(v -> switchTab(false));
        fabGvCreateSession.setOnClickListener(v -> showCreateSessionDialog());
    }

    private void switchTab(boolean toPhien) {
        isTabPhien = toPhien;
        if (isTabPhien) {
            layoutTabPhien.setVisibility(View.VISIBLE);
            layoutTabDuyetDon.setVisibility(View.GONE);
            fabGvCreateSession.show();

            btnGvTabPhien.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
            btnGvTabPhien.setTextColor(ContextCompat.getColor(this, R.color.white));

            btnGvTabDuyetDon.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            btnGvTabDuyetDon.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            layoutTabPhien.setVisibility(View.GONE);
            layoutTabDuyetDon.setVisibility(View.VISIBLE);
            fabGvCreateSession.hide();

            btnGvTabDuyetDon.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
            btnGvTabDuyetDon.setTextColor(ContextCompat.getColor(this, R.color.white));

            btnGvTabPhien.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            btnGvTabPhien.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        }
    }

    /**
     * Dialog Mở Buổi Điểm Danh Mới khớp 100% với bảng BuoiDiemDanh trên Web API
     */
    private void showCreateSessionDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tao_phien_diem_danh, null);
        Spinner spChonLHP = dialogView.findViewById(R.id.spDlgChonLHP);
        TextInputEditText edtSoBuoi = dialogView.findViewById(R.id.edtDlgSoThuTuBuoi);
        TextInputEditText edtBanKinh = dialogView.findViewById(R.id.edtDlgBanKinhMet);
        TextInputEditText edtGhiChu = dialogView.findViewById(R.id.edtDlgGhiChuBuoi);
        TextInputEditText edtGioBatDau = dialogView.findViewById(R.id.edtDlgGioBatDau);
        TextInputEditText edtGioKetThuc = dialogView.findViewById(R.id.edtDlgGioKetThuc);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnDlgCancelMoPhien);
        MaterialButton btnConfirm = dialogView.findViewById(R.id.btnDlgConfirmMoPhien);

        List<LopHocPhan> classes = repository.getDanhSachLopHocPhan();
        List<String> classNames = new ArrayList<>();
        for (LopHocPhan lhp : classes) {
            classNames.add(lhp.getMaLHP() + " - " + lhp.getTenMonHoc() + " (" + lhp.getPhongHoc() + ")");
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, classNames);
        spChonLHP.setAdapter(spinnerAdapter);

        // Tính tự động buổi tiếp theo
        List<PhienDiemDanh> existingSessions = repository.getAllPhienDiemDanh();
        int nextSessionNum = existingSessions.size() + 1;
        for (PhienDiemDanh p : existingSessions) {
            if (p.getSoThuTuBuoi() >= nextSessionNum) {
                nextSessionNum = p.getSoThuTuBuoi() + 1;
            }
        }
        edtSoBuoi.setText(String.valueOf(nextSessionNum));
        edtGhiChu.setText("Buổi " + nextSessionNum + ": Lập trình ứng dụng kết nối Web API");

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            int selectedIndex = spChonLHP.getSelectedItemPosition();
            if (selectedIndex >= 0 && selectedIndex < classes.size()) {
                LopHocPhan chosen = classes.get(selectedIndex);
                int soBuoi = 4;
                try {
                    soBuoi = Integer.parseInt(edtSoBuoi.getText() != null ? edtSoBuoi.getText().toString().trim() : "4");
                } catch (Exception ignored) {}

                double banKinh = 150.0;
                try {
                    banKinh = Double.parseDouble(edtBanKinh.getText() != null ? edtBanKinh.getText().toString().trim() : "150");
                } catch (Exception ignored) {}

                String ghiChu = edtGhiChu.getText() != null ? edtGhiChu.getText().toString().trim() : "";
                String gioBD = edtGioBatDau.getText() != null ? edtGioBatDau.getText().toString().trim() : "07:30:00";
                String gioKT = edtGioKetThuc.getText() != null ? edtGioKetThuc.getText().toString().trim() : "11:30:00";

                PhienDiemDanh newPhien = repository.moPhienDiemDanhChiTiet(
                        chosen.getId(),
                        chosen.getMaLHP(),
                        soBuoi,
                        ghiChu,
                        gioBD,
                        gioKT,
                        banKinh,
                        10.8018,
                        106.7142
                );
                if (newPhien != null) {
                    Toast.makeText(this, "Đã tạo & mở Buổi " + newPhien.getSoThuTuBuoi() + " trên Server!", Toast.LENGTH_SHORT).show();
                    loadDashboardData();
                    dialog.dismiss();

                    Intent intent = new Intent(this, TaoPhienQRActivity.class);
                    intent.putExtra("MA_PHIEN", newPhien.getMaPhien());
                    intent.putExtra("MA_LHP", newPhien.getMaLHP());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Tạo buổi điểm danh trên Server thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    // === XỬ LÝ CÁC SỰ KIỆN PHIÊN ĐIỂM DANH ===
    @Override
    public void onShowQr(PhienDiemDanh phien) {
        Intent intent = new Intent(this, TaoPhienQRActivity.class);
        intent.putExtra("MA_PHIEN", phien.getMaPhien());
        intent.putExtra("MA_LHP", phien.getMaLHP());
        startActivity(intent);
    }

    @Override
    public void onViewStudents(PhienDiemDanh phien) {
        showStudentsAttendanceDialog(phien);
    }

    @Override
    public void onClosePhien(PhienDiemDanh phien) {
        boolean isCurrentlyOpen = phien.isTrangThaiMo();
        String title = isCurrentlyOpen ? "Đóng Buổi Điểm Danh" : "Mở Lại Buổi Điểm Danh";
        String msg = isCurrentlyOpen
                ? "Bạn có chắc chắn muốn kết thúc và đóng Buổi " + phien.getSoThuTuBuoi() + " (" + phien.getMaLHP() + ") không?"
                : "Bạn có muốn mở lại Buổi " + phien.getSoThuTuBuoi() + " (" + phien.getMaLHP() + ") để sinh viên tiếp tục điểm danh không?";
        String posBtn = isCurrentlyOpen ? "Đóng Phiên" : "Mở Lại";

        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(posBtn, (dialog, which) -> {
                    boolean success = repository.dongPhienDiemDanh(phien.getMaPhien());
                    if (success) {
                        Toast.makeText(this, isCurrentlyOpen ? "Đã đóng buổi điểm danh!" : "Đã mở lại buổi điểm danh!", Toast.LENGTH_SHORT).show();
                        loadDashboardData();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDeletePhien(PhienDiemDanh phien) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xóa Buổi Điểm Danh")
                .setMessage("Bạn có chắc muốn xóa Buổi " + phien.getSoThuTuBuoi() + " (" + phien.getGhiChu() + ") khỏi hệ thống Server không?")
                .setPositiveButton("Xóa Vĩnh Viễn", (dialog, which) -> {
                    boolean success = repository.xoaPhienDiemDanh(phien.getMaPhien());
                    if (success) {
                        Toast.makeText(this, "Đã xóa buổi điểm danh khỏi Server!", Toast.LENGTH_SHORT).show();
                        loadDashboardData();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Dialog Xem danh sách sinh viên & Chỉnh sửa trạng thái điểm danh thủ công
     */
    private void showStudentsAttendanceDialog(PhienDiemDanh phien) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_danh_sach_sinh_vien, null);
        TextView tvTitle = dialogView.findViewById(R.id.tvDlgPhienTitle);
        RecyclerView rvStudents = dialogView.findViewById(R.id.rvDlgChiTietDiemDanh);
        MaterialButton btnClose = dialogView.findViewById(R.id.btnDlgClosePhien);

        tvTitle.setText("DS ĐIỂM DANH - " + phien.getMaLHP() + " (BUỔI " + phien.getSoThuTuBuoi() + ")");

        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        List<ChiTietDiemDanh> studentList = repository.getChiTietTheoPhien(phien.getMaPhien());

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        ChiTietDiemDanhAdapter adapter = new ChiTietDiemDanhAdapter(studentList, chiTiet -> {
            showEditStatusDialog(chiTiet, () -> {
                List<ChiTietDiemDanh> updated = repository.getChiTietTheoPhien(phien.getMaPhien());
                ((ChiTietDiemDanhAdapter) rvStudents.getAdapter()).updateData(updated);
            });
        });
        rvStudents.setAdapter(adapter);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showEditStatusDialog(ChiTietDiemDanh ct, Runnable onUpdated) {
        String[] options = {"Có mặt (CoMat)", "Đi trễ (DiTre)", "Vắng có phép (VangCoPhep)", "Vắng không phép (VangKhongPhep)"};
        String[] rawStatus = {"CoMat", "DiTre", "VangCoPhep", "VangKhongPhep"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Cập Nhật Trạng Thái: " + ct.getTenSV())
                .setItems(options, (dialog, which) -> {
                    String selectedStatus = rawStatus[which];
                    boolean ok = repository.capNhatTrangThaiDiemDanh(ct.getMaCT(), selectedStatus);
                    if (ok) {
                        Toast.makeText(this, "Đã chuyển trạng thái sang: " + options[which], Toast.LENGTH_SHORT).show();
                        if (onUpdated != null) onUpdated.run();
                    }
                })
                .show();
    }

    @Override
    public void onDuyet(DonXinPhep don) {
        boolean ok = repository.duyetDonXinPhep(don.getMaDon(), "DaDuyet");
        if (ok) {
            Toast.makeText(this, "Đã DUYỆT đơn của sinh viên: " + don.getTenSV(), Toast.LENGTH_SHORT).show();
            loadDashboardData();
        }
    }

    @Override
    public void onTuChoi(DonXinPhep don) {
        boolean ok = repository.duyetDonXinPhep(don.getMaDon(), "TuChoi");
        if (ok) {
            Toast.makeText(this, "Đã TỪ CHỐI đơn của sinh viên: " + don.getTenSV(), Toast.LENGTH_SHORT).show();
            loadDashboardData();
        }
    }
}
