package com.nguyenhoaian2380600008.activities.admin;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nguyenhoaian2380600008.R;
import com.nguyenhoaian2380600008.activities.auth.LoginActivity;
import com.nguyenhoaian2380600008.adapters.LopHocPhanAdapter;
import com.nguyenhoaian2380600008.api.MockDataRepository;
import com.nguyenhoaian2380600008.models.LopHocPhan;
import com.nguyenhoaian2380600008.models.NguoiDung;
import com.nguyenhoaian2380600008.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminMainActivity extends AppCompatActivity implements LopHocPhanAdapter.OnLopHocPhanListener {

    private TextView tvAdminWelcome;
    private TextView tvAdminClassCount, tvAdminGvCount, tvAdminSvCount;
    private TextView tvAdminSectionTitle, tvAdminClassTotalBadge, tvAdminEmptyLHP;
    private MaterialCardView cardAdminStatLop, cardAdminStatMon, cardAdminStatSv;
    private MaterialButton btnAdminLogout;
    private MaterialButton btnAdminTabLHP, btnAdminTabMonHoc, btnAdminTabSinhVien, btnAdminTabThongKe;
    private EditText edtAdminSearchLHP;
    private RecyclerView rvAdminLopHocPhan;
    private NestedScrollView scrollAdminDynamicContent;
    private LinearLayout layoutAdminDynamicList;
    private FloatingActionButton fabAddLHP;

    private SessionManager sessionManager;
    private MockDataRepository repository;
    private LopHocPhanAdapter adapter;

    // 0: Lớp HP, 1: Môn Học, 2: Sinh Viên, 3: Thống Kê & Cấm Thi
    private int currentTab = 0;
    private String currentSearchQuery = "";

    private List<Map<String, Object>> cachedMonHocList = new ArrayList<>();
    private List<Map<String, Object>> cachedSinhVienList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        sessionManager = SessionManager.getInstance(this);
        repository = MockDataRepository.getInstance();

        initViews();
        setupRecyclerView();
        loadStatsAndData();
        setupEvents();
    }

    private void initViews() {
        tvAdminWelcome = findViewById(R.id.tvAdminWelcome);
        tvAdminClassCount = findViewById(R.id.tvAdminClassCount);
        tvAdminGvCount = findViewById(R.id.tvAdminGvCount);
        tvAdminSvCount = findViewById(R.id.tvAdminSvCount);
        tvAdminSectionTitle = findViewById(R.id.tvAdminSectionTitle);
        tvAdminClassTotalBadge = findViewById(R.id.tvAdminClassTotalBadge);
        tvAdminEmptyLHP = findViewById(R.id.tvAdminEmptyLHP);

        cardAdminStatLop = findViewById(R.id.cardAdminStatLop);
        cardAdminStatMon = findViewById(R.id.cardAdminStatMon);
        cardAdminStatSv = findViewById(R.id.cardAdminStatSv);

        btnAdminLogout = findViewById(R.id.btnAdminLogout);
        btnAdminTabLHP = findViewById(R.id.btnAdminTabLHP);
        btnAdminTabMonHoc = findViewById(R.id.btnAdminTabMonHoc);
        btnAdminTabSinhVien = findViewById(R.id.btnAdminTabSinhVien);
        btnAdminTabThongKe = findViewById(R.id.btnAdminTabThongKe);

        edtAdminSearchLHP = findViewById(R.id.edtAdminSearchLHP);
        rvAdminLopHocPhan = findViewById(R.id.rvAdminLopHocPhan);
        scrollAdminDynamicContent = findViewById(R.id.scrollAdminDynamicContent);
        layoutAdminDynamicList = findViewById(R.id.layoutAdminDynamicList);
        fabAddLHP = findViewById(R.id.fabAddLHP);
    }

    private void setupRecyclerView() {
        rvAdminLopHocPhan.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LopHocPhanAdapter(new ArrayList<>(), this);
        rvAdminLopHocPhan.setAdapter(adapter);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private void loadStatsAndData() {
        NguoiDung currentUser = sessionManager.getCurrentUser();
        if (currentUser != null && currentUser.getHoTen() != null) {
            tvAdminWelcome.setText("Xin chào, " + currentUser.getHoTen());
        } else {
            tvAdminWelcome.setText("Xin chào, Quản trị viên");
        }

        int classCount = repository.getSoLuongLopHocPhan();
        int monCount = repository.getSoLuongGiangVien();
        int svCount = repository.getSoLuongSinhVien();

        tvAdminClassCount.setText(String.valueOf(classCount));
        tvAdminGvCount.setText(String.valueOf(monCount));
        tvAdminSvCount.setText(String.valueOf(svCount));

        renderActiveTab();
    }

    private void switchTab(int tabIndex) {
        currentTab = tabIndex;
        MaterialButton[] tabs = {btnAdminTabLHP, btnAdminTabMonHoc, btnAdminTabSinhVien, btnAdminTabThongKe};
        for (int i = 0; i < tabs.length; i++) {
            if (i == tabIndex) {
                tabs[i].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary));
                tabs[i].setTextColor(Color.WHITE);
            } else {
                tabs[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.TRANSPARENT));
                tabs[i].setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            }
        }
        renderActiveTab();
    }

    private void renderActiveTab() {
        if (currentTab == 0) {
            tvAdminSectionTitle.setText("Danh Sách Lớp Học Phần:");
            edtAdminSearchLHP.setHint("Tìm theo Tên LHP hoặc Mã LHP...");
            fabAddLHP.show();
            rvAdminLopHocPhan.setVisibility(View.VISIBLE);
            scrollAdminDynamicContent.setVisibility(View.GONE);

            List<LopHocPhan> list = repository.getDanhSachLopHocPhan();
            adapter.updateData(list);
            if (!currentSearchQuery.isEmpty()) {
                adapter.filter(currentSearchQuery);
            }
            tvAdminClassTotalBadge.setText(adapter.getItemCount() + " lớp");
            tvAdminEmptyLHP.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        } else if (currentTab == 1) {
            tvAdminSectionTitle.setText("Danh Sách Môn Học (/api/MonHoc):");
            edtAdminSearchLHP.setHint("Tìm theo Mã môn hoặc Tên môn học...");
            fabAddLHP.show();
            rvAdminLopHocPhan.setVisibility(View.GONE);
            scrollAdminDynamicContent.setVisibility(View.VISIBLE);
            tvAdminEmptyLHP.setVisibility(View.GONE);

            cachedMonHocList = repository.getDanhSachMonHocServer();
            tvAdminGvCount.setText(String.valueOf(cachedMonHocList.size()));
            renderMonHocTab();
        } else if (currentTab == 2) {
            tvAdminSectionTitle.setText("Danh Sách Sinh Viên (/api/SinhVien):");
            edtAdminSearchLHP.setHint("Tìm theo MSSV, Họ tên hoặc Lớp...");
            fabAddLHP.show();
            rvAdminLopHocPhan.setVisibility(View.GONE);
            scrollAdminDynamicContent.setVisibility(View.VISIBLE);
            tvAdminEmptyLHP.setVisibility(View.GONE);

            cachedSinhVienList = repository.getDanhSachSinhVienServer();
            tvAdminSvCount.setText(String.valueOf(cachedSinhVienList.size()));
            renderSinhVienTab();
        } else {
            tvAdminSectionTitle.setText("Thống Kê & Cảnh Báo Cấm Thi:");
            edtAdminSearchLHP.setHint("Tìm sinh viên trong danh sách cấm thi...");
            fabAddLHP.hide();
            rvAdminLopHocPhan.setVisibility(View.GONE);
            scrollAdminDynamicContent.setVisibility(View.VISIBLE);
            tvAdminEmptyLHP.setVisibility(View.GONE);

            renderThongKeCamThiTab();
        }
    }

    // === TAB 2: HIỂN THỊ & CRUD MÔN HỌC (/api/MonHoc) ===
    private void renderMonHocTab() {
        layoutAdminDynamicList.removeAllViews();
        String q = currentSearchQuery.toLowerCase(Locale.getDefault()).trim();
        int shown = 0;

        for (Map<String, Object> mh : cachedMonHocList) {
            int id = (int) mh.get("id");
            String maMon = String.valueOf(mh.get("maMon"));
            String tenMon = String.valueOf(mh.get("tenMon"));
            int soTinChi = (int) mh.get("soTinChi");
            String moTa = String.valueOf(mh.get("moTa"));

            if (!q.isEmpty() && !maMon.toLowerCase(Locale.getDefault()).contains(q)
                    && !tenMon.toLowerCase(Locale.getDefault()).contains(q)) {
                continue;
            }
            shown++;

            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cp.bottomMargin = dp(10);
            card.setLayoutParams(cp);
            card.setRadius(dp(16));
            card.setCardElevation(dp(2));
            card.setCardBackgroundColor(Color.WHITE);

            LinearLayout box = new LinearLayout(this);
            box.setOrientation(LinearLayout.VERTICAL);
            box.setPadding(dp(14), dp(14), dp(14), dp(14));

            TextView tvHeader = new TextView(this);
            tvHeader.setText("📖 " + tenMon + " (" + maMon + ")");
            tvHeader.setTextColor(Color.parseColor("#0F172A"));
            tvHeader.setTextSize(15f);
            tvHeader.setTypeface(null, Typeface.BOLD);

            TextView tvSub = new TextView(this);
            tvSub.setText("🎓 Số tín chỉ: " + soTinChi + " TC | ID Server: #" + id);
            tvSub.setTextColor(Color.parseColor("#2563EB"));
            tvSub.setTextSize(12.5f);
            tvSub.setTypeface(null, Typeface.BOLD);
            tvSub.setPadding(0, dp(4), 0, dp(4));

            TextView tvDesc = new TextView(this);
            tvDesc.setText("📝 Mô tả: " + (moTa.isEmpty() ? "Chưa có mô tả" : moTa));
            tvDesc.setTextColor(Color.parseColor("#475569"));
            tvDesc.setTextSize(12.5f);

            LinearLayout actions = new LinearLayout(this);
            actions.setOrientation(LinearLayout.HORIZONTAL);
            actions.setGravity(Gravity.END);
            actions.setPadding(0, dp(8), 0, 0);

            MaterialButton btnEdit = new MaterialButton(this);
            btnEdit.setText("✏️ Sửa Môn");
            btnEdit.setTextSize(11.5f);
            btnEdit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EFF6FF")));
            btnEdit.setTextColor(Color.parseColor("#1D4ED8"));
            btnEdit.setOnClickListener(v -> showMonHocDialog(mh));

            MaterialButton btnDel = new MaterialButton(this);
            LinearLayout.LayoutParams delParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            delParams.leftMargin = dp(8);
            btnDel.setLayoutParams(delParams);
            btnDel.setText("🗑️ Xóa");
            btnDel.setTextSize(11.5f);
            btnDel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FEF2F2")));
            btnDel.setTextColor(Color.parseColor("#DC2626"));
            btnDel.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Xóa Môn Học")
                        .setMessage("Bạn có chắc muốn xóa môn \"" + tenMon + "\" (" + maMon + ") trên Server không?")
                        .setPositiveButton("Xóa", (d, w) -> {
                            if (repository.xoaMonHocServer(id)) {
                                Toast.makeText(this, "Đã xóa môn học: " + maMon, Toast.LENGTH_SHORT).show();
                                loadStatsAndData();
                            } else {
                                Toast.makeText(this, "Không thể xóa môn học đang có lớp học phần hoạt động!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });

            actions.addView(btnEdit);
            actions.addView(btnDel);

            box.addView(tvHeader);
            box.addView(tvSub);
            box.addView(tvDesc);
            box.addView(actions);
            card.addView(box);
            layoutAdminDynamicList.addView(card);
        }

        tvAdminClassTotalBadge.setText(shown + " môn học");
        tvAdminEmptyLHP.setVisibility(shown == 0 ? View.VISIBLE : View.GONE);
    }

    private void showMonHocDialog(Map<String, Object> editItem) {
        boolean isEdit = editItem != null;
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(14), dp(20), dp(8));

        EditText edtMaMon = new EditText(this);
        edtMaMon.setHint("Mã môn học (VD: AI03)");
        EditText edtTenMon = new EditText(this);
        edtTenMon.setHint("Tên môn học (VD: Trí tuệ nhân tạo)");
        EditText edtSoTinChi = new EditText(this);
        edtSoTinChi.setHint("Số tín chỉ (VD: 3)");
        edtSoTinChi.setInputType(InputType.TYPE_CLASS_NUMBER);
        EditText edtMoTa = new EditText(this);
        edtMoTa.setHint("Mô tả môn học...");

        if (isEdit) {
            edtMaMon.setText(String.valueOf(editItem.get("maMon")));
            edtTenMon.setText(String.valueOf(editItem.get("tenMon")));
            edtSoTinChi.setText(String.valueOf(editItem.get("soTinChi")));
            edtMoTa.setText(String.valueOf(editItem.get("moTa")));
        } else {
            edtSoTinChi.setText("3");
        }

        form.addView(edtMaMon);
        form.addView(edtTenMon);
        form.addView(edtSoTinChi);
        form.addView(edtMoTa);

        new MaterialAlertDialogBuilder(this)
                .setTitle(isEdit ? "Cập Nhật Môn Học" : "Thêm Môn Học Mới (/api/MonHoc)")
                .setView(form)
                .setPositiveButton(isEdit ? "Lưu Thay Đổi" : "Thêm Môn Học", (d, w) -> {
                    String maMon = edtMaMon.getText().toString().trim();
                    String tenMon = edtTenMon.getText().toString().trim();
                    String strTC = edtSoTinChi.getText().toString().trim();
                    String moTa = edtMoTa.getText().toString().trim();

                    if (maMon.isEmpty() || tenMon.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập đầy đủ Mã môn và Tên môn!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int tc = 3;
                    try { tc = Integer.parseInt(strTC); } catch (Exception ignored) {}

                    boolean ok;
                    if (isEdit) {
                        int id = (int) editItem.get("id");
                        ok = repository.capNhatMonHocServer(id, maMon, tenMon, tc, moTa);
                    } else {
                        ok = repository.themMonHocServer(maMon, tenMon, tc, moTa);
                    }
                    if (ok) {
                        Toast.makeText(this, isEdit ? "Đã cập nhật môn học!" : "Đã thêm môn học mới lên Server!", Toast.LENGTH_SHORT).show();
                        loadStatsAndData();
                    } else {
                        Toast.makeText(this, "Thao tác môn học thất bại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // === TAB 3: HIỂN THỊ & CRUD SINH VIÊN (/api/SinhVien) ===
    private void renderSinhVienTab() {
        layoutAdminDynamicList.removeAllViews();
        String q = currentSearchQuery.toLowerCase(Locale.getDefault()).trim();
        int shown = 0;

        for (Map<String, Object> sv : cachedSinhVienList) {
            int id = (int) sv.get("id");
            String maSV = String.valueOf(sv.get("maSV"));
            String hoTen = String.valueOf(sv.get("hoTen"));
            String email = String.valueOf(sv.get("email"));
            String sdt = String.valueOf(sv.get("soDienThoai"));
            String lop = String.valueOf(sv.get("lop"));
            String khoa = String.valueOf(sv.get("khoa"));

            if (!q.isEmpty() && !maSV.toLowerCase(Locale.getDefault()).contains(q)
                    && !hoTen.toLowerCase(Locale.getDefault()).contains(q)
                    && !lop.toLowerCase(Locale.getDefault()).contains(q)) {
                continue;
            }
            shown++;

            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cp.bottomMargin = dp(10);
            card.setLayoutParams(cp);
            card.setRadius(dp(16));
            card.setCardElevation(dp(2));
            card.setCardBackgroundColor(Color.WHITE);

            LinearLayout box = new LinearLayout(this);
            box.setOrientation(LinearLayout.VERTICAL);
            box.setPadding(dp(14), dp(14), dp(14), dp(14));

            TextView tvHeader = new TextView(this);
            tvHeader.setText("🎓 " + hoTen + " (" + maSV + ")");
            tvHeader.setTextColor(Color.parseColor("#0F172A"));
            tvHeader.setTextSize(15f);
            tvHeader.setTypeface(null, Typeface.BOLD);

            TextView tvSub = new TextView(this);
            tvSub.setText("🏫 Lớp: " + lop + " | Khoa: " + khoa);
            tvSub.setTextColor(Color.parseColor("#059669"));
            tvSub.setTextSize(12.5f);
            tvSub.setTypeface(null, Typeface.BOLD);
            tvSub.setPadding(0, dp(3), 0, dp(3));

            TextView tvContact = new TextView(this);
            tvContact.setText("✉️ " + email + " | 📞 " + sdt);
            tvContact.setTextColor(Color.parseColor("#475569"));
            tvContact.setTextSize(12f);

            LinearLayout actions = new LinearLayout(this);
            actions.setOrientation(LinearLayout.HORIZONTAL);
            actions.setGravity(Gravity.END);
            actions.setPadding(0, dp(8), 0, 0);

            MaterialButton btnStats = new MaterialButton(this);
            btnStats.setText("📊 Chuyên Cần");
            btnStats.setTextSize(11f);
            btnStats.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#ECFDF5")));
            btnStats.setTextColor(Color.parseColor("#059669"));
            btnStats.setOnClickListener(v -> showStudentAttendanceReportDialog(maSV, hoTen));

            MaterialButton btnEdit = new MaterialButton(this);
            LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            editParams.leftMargin = dp(6);
            btnEdit.setLayoutParams(editParams);
            btnEdit.setText("✏️ Sửa");
            btnEdit.setTextSize(11f);
            btnEdit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EFF6FF")));
            btnEdit.setTextColor(Color.parseColor("#1D4ED8"));
            btnEdit.setOnClickListener(v -> showSinhVienDialog(sv));

            MaterialButton btnDel = new MaterialButton(this);
            LinearLayout.LayoutParams delParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            delParams.leftMargin = dp(6);
            btnDel.setLayoutParams(delParams);
            btnDel.setText("🗑️ Xóa");
            btnDel.setTextSize(11f);
            btnDel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FEF2F2")));
            btnDel.setTextColor(Color.parseColor("#DC2626"));
            btnDel.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Xóa Sinh Viên")
                        .setMessage("Bạn có chắc muốn xóa sinh viên \"" + hoTen + "\" (" + maSV + ") khỏi hệ thống không?")
                        .setPositiveButton("Xóa", (d, w) -> {
                            if (repository.xoaSinhVienServer(id)) {
                                Toast.makeText(this, "Đã xóa sinh viên: " + maSV, Toast.LENGTH_SHORT).show();
                                loadStatsAndData();
                            } else {
                                Toast.makeText(this, "Xóa sinh viên thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });

            actions.addView(btnStats);
            actions.addView(btnEdit);
            actions.addView(btnDel);

            box.addView(tvHeader);
            box.addView(tvSub);
            box.addView(tvContact);
            box.addView(actions);
            card.addView(box);
            layoutAdminDynamicList.addView(card);
        }

        tvAdminClassTotalBadge.setText(shown + " sinh viên");
        tvAdminEmptyLHP.setVisibility(shown == 0 ? View.VISIBLE : View.GONE);
    }

    private void showSinhVienDialog(Map<String, Object> editItem) {
        boolean isEdit = editItem != null;
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(14), dp(20), dp(8));

        EditText edtMaSV = new EditText(this);
        edtMaSV.setHint("Mã số sinh viên (VD: 2380608888)");
        EditText edtHoTen = new EditText(this);
        edtHoTen.setHint("Họ và tên sinh viên");
        EditText edtEmail = new EditText(this);
        edtEmail.setHint("Email (VD: sv@hutech.edu.vn)");
        EditText edtSdt = new EditText(this);
        edtSdt.setHint("Số điện thoại");
        EditText edtLop = new EditText(this);
        edtLop.setHint("Lớp niên chế (VD: 21DTH01)");
        EditText edtKhoa = new EditText(this);
        edtKhoa.setHint("Khoa (VD: Công nghệ thông tin)");

        if (isEdit) {
            edtMaSV.setText(String.valueOf(editItem.get("maSV")));
            edtHoTen.setText(String.valueOf(editItem.get("hoTen")));
            edtEmail.setText(String.valueOf(editItem.get("email")));
            edtSdt.setText(String.valueOf(editItem.get("soDienThoai")));
            edtLop.setText(String.valueOf(editItem.get("lop")));
            edtKhoa.setText(String.valueOf(editItem.get("khoa")));
        } else {
            edtLop.setText("21DTH01");
            edtKhoa.setText("Công nghệ thông tin");
        }

        form.addView(edtMaSV);
        form.addView(edtHoTen);
        form.addView(edtEmail);
        form.addView(edtSdt);
        form.addView(edtLop);
        form.addView(edtKhoa);

        new MaterialAlertDialogBuilder(this)
                .setTitle(isEdit ? "Cập Nhật Sinh Viên" : "Thêm Sinh Viên Mới (/api/SinhVien)")
                .setView(form)
                .setPositiveButton(isEdit ? "Lưu Thay Đổi" : "Thêm Sinh Viên", (d, w) -> {
                    String maSV = edtMaSV.getText().toString().trim();
                    String hoTen = edtHoTen.getText().toString().trim();
                    String email = edtEmail.getText().toString().trim();
                    String sdt = edtSdt.getText().toString().trim();
                    String lop = edtLop.getText().toString().trim();
                    String khoa = edtKhoa.getText().toString().trim();

                    if (maSV.isEmpty() || hoTen.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập Mã SV và Họ tên!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean ok;
                    if (isEdit) {
                        int id = (int) editItem.get("id");
                        ok = repository.capNhatSinhVienServer(id, maSV, hoTen, email, sdt, lop, khoa);
                    } else {
                        ok = repository.themSinhVienServer(maSV, hoTen, email, sdt, lop, khoa);
                    }
                    if (ok) {
                        Toast.makeText(this, isEdit ? "Đã cập nhật sinh viên!" : "Đã thêm sinh viên mới!", Toast.LENGTH_SHORT).show();
                        loadStatsAndData();
                    } else {
                        Toast.makeText(this, "Thao tác sinh viên thất bại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showStudentAttendanceReportDialog(String maSV, String hoTen) {
        String[] summary = repository.getThongKeChuyenCanSV(maSV);
        List<Map<String, Object>> subjects = repository.getDanhSachThongKeMonHocSV(maSV);

        StringBuilder sb = new StringBuilder();
        sb.append("👤 Sinh viên: ").append(hoTen).append(" (").append(maSV).append(")\n");
        sb.append("📊 Tổng chuyên cần: ").append(summary[0]).append(" (").append(summary[1]).append(")\n\n");

        if (subjects.isEmpty()) {
            sb.append("Chưa có dữ liệu điểm danh môn học.");
        } else {
            for (Map<String, Object> mh : subjects) {
                sb.append("📚 ").append(mh.get("tenMon")).append(" (").append(mh.get("maLopHocPhan")).append("):\n");
                sb.append("   • Có mặt: ").append(mh.get("soBuoiCoMat")).append("/").append(mh.get("tongBuoiDaMo")).append(" buổi\n");
                sb.append("   • Vắng có phép: ").append(mh.get("soBuoiVangCoPhep"))
                        .append(" | Vắng không phép: ").append(mh.get("soBuoiVangKhongPhep")).append("\n");
                sb.append("   • Tỷ lệ vắng: ").append(mh.get("tiLeVang")).append("%\n\n");
            }
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Thống Kê Chuyên Cần Sinh Viên")
                .setMessage(sb.toString().trim())
                .setPositiveButton("Đóng", null)
                .show();
    }

    // === TAB 4: BÁO CÁO THỐNG KÊ & DANH SÁCH CẢNH BÁO CẤM THI (/api/ThongKe) ===
    private void renderThongKeCamThiTab() {
        layoutAdminDynamicList.removeAllViews();
        String q = currentSearchQuery.toLowerCase(Locale.getDefault()).trim();

        Map<String, Integer> overview = repository.getThongKeTongQuanChiTiet();

        // Thẻ Tổng Quan Hệ Thống
        MaterialCardView summaryCard = new MaterialCardView(this);
        LinearLayout.LayoutParams scp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        scp.bottomMargin = dp(12);
        summaryCard.setLayoutParams(scp);
        summaryCard.setRadius(dp(16));
        summaryCard.setCardElevation(dp(2));
        summaryCard.setCardBackgroundColor(Color.parseColor("#EFF6FF"));

        LinearLayout sumBox = new LinearLayout(this);
        sumBox.setOrientation(LinearLayout.VERTICAL);
        sumBox.setPadding(dp(14), dp(14), dp(14), dp(14));

        TextView tvSumTitle = new TextView(this);
        tvSumTitle.setText("📊 BÁO CÁO TỔNG QUAN HỆ THỐNG (/api/ThongKe/tong-quan)");
        tvSumTitle.setTextColor(Color.parseColor("#1E3A8A"));
        tvSumTitle.setTextSize(13.5f);
        tvSumTitle.setTypeface(null, Typeface.BOLD);

        TextView tvSumDetail = new TextView(this);
        tvSumDetail.setText(
                "• Tổng số môn học: " + overview.get("tongSoMonHoc") + " môn\n" +
                "• Tổng số lớp học phần: " + overview.get("tongSoLopHocPhan") + " lớp\n" +
                "• Tổng số sinh viên: " + overview.get("tongSoSinhVien") + " sinh viên\n" +
                "• Số buổi điểm danh đã mở: " + overview.get("tongSoBuoiDiemDanhHomNay") + " buổi\n" +
                "• Tổng lượt điểm danh ghi nhận: " + overview.get("tongSoLuotDiemDanhHomNay") + " lượt"
        );
        tvSumDetail.setTextColor(Color.parseColor("#1E40AF"));
        tvSumDetail.setTextSize(12.5f);
        tvSumDetail.setPadding(0, dp(6), 0, 0);

        sumBox.addView(tvSumTitle);
        sumBox.addView(tvSumDetail);
        summaryCard.addView(sumBox);
        layoutAdminDynamicList.addView(summaryCard);

        // Danh sách cảnh báo cấm thi từng lớp học phần
        List<LopHocPhan> classes = repository.getDanhSachLopHocPhan();
        int shown = 0;
        for (LopHocPhan lhp : classes) {
            List<Map<String, Object>> warnings = repository.getDanhSachCanhBaoCamThi(lhp.getId());
            for (Map<String, Object> w : warnings) {
                String maSV = String.valueOf(w.get("maSV"));
                String hoTen = String.valueOf(w.get("hoTen"));
                String lop = String.valueOf(w.get("lop"));
                String maLHP = String.valueOf(w.get("maLopHocPhan"));
                String tenMon = String.valueOf(w.get("tenMonHoc"));
                int coMat = (int) w.get("soBuoiCoMat");
                int diTre = (int) w.get("soBuoiDiTre");
                int vang = (int) w.get("soBuoiVang");
                int tongBuoi = (int) w.get("tongSoBuoi");
                double tiLeVang = (double) w.get("tiLeVangPhanTram");
                String mucDo = String.valueOf(w.get("mucDoCanhBao"));
                String khuyenNghi = String.valueOf(w.get("khuyenNghi"));

                if (!q.isEmpty() && !maSV.toLowerCase(Locale.getDefault()).contains(q)
                        && !hoTen.toLowerCase(Locale.getDefault()).contains(q)) {
                    continue;
                }
                shown++;

                MaterialCardView card = new MaterialCardView(this);
                LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                cp.bottomMargin = dp(10);
                card.setLayoutParams(cp);
                card.setRadius(dp(16));
                card.setCardElevation(dp(2));
                card.setCardBackgroundColor(Color.WHITE);

                LinearLayout box = new LinearLayout(this);
                box.setOrientation(LinearLayout.VERTICAL);
                box.setPadding(dp(14), dp(14), dp(14), dp(14));

                TextView tvName = new TextView(this);
                String badgeIcon = "CamThi".equalsIgnoreCase(mucDo) ? "🚫 [CẤM THI] "
                        : ("CanhBao_NguyCo".equalsIgnoreCase(mucDo) ? "⚠️ [NGUY CƠ CAO] " : "🔔 [CHÚ Ý] ");
                tvName.setText(badgeIcon + hoTen + " (" + maSV + ")");
                int color = "CamThi".equalsIgnoreCase(mucDo) ? Color.parseColor("#DC2626")
                        : ("CanhBao_NguyCo".equalsIgnoreCase(mucDo) ? Color.parseColor("#D97706") : Color.parseColor("#2563EB"));
                tvName.setTextColor(color);
                tvName.setTextSize(14.5f);
                tvName.setTypeface(null, Typeface.BOLD);

                TextView tvClass = new TextView(this);
                tvClass.setText("📚 Lớp HP: " + maLHP + " - " + tenMon + " | Lớp: " + lop);
                tvClass.setTextColor(Color.parseColor("#0F172A"));
                tvClass.setTextSize(12.5f);
                tvClass.setPadding(0, dp(4), 0, dp(4));

                TextView tvStats = new TextView(this);
                tvStats.setText(String.format(Locale.US,
                        "✅ Có mặt: %d | ⏰ Đi trễ: %d | ❌ Vắng: %d/%d buổi (Tỷ lệ vắng: %.1f%%)",
                        coMat, diTre, vang, tongBuoi, tiLeVang));
                tvStats.setTextColor(Color.parseColor("#334155"));
                tvStats.setTextSize(12f);
                tvStats.setTypeface(null, Typeface.BOLD);

                TextView tvAdvice = new TextView(this);
                tvAdvice.setText("💡 " + khuyenNghi);
                tvAdvice.setTextColor(color);
                tvAdvice.setTextSize(12f);
                tvAdvice.setPadding(0, dp(4), 0, 0);

                box.addView(tvName);
                box.addView(tvClass);
                box.addView(tvStats);
                box.addView(tvAdvice);
                card.addView(box);
                layoutAdminDynamicList.addView(card);
            }
        }

        tvAdminClassTotalBadge.setText(shown + " cảnh báo");
    }

    private void setupEvents() {
        // Đăng xuất
        btnAdminLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Toast.makeText(this, "Đã đăng xuất tài khoản Quản trị viên!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Bấm vào 4 nút Tab hoặc 3 thẻ thống kê trên cùng để chuyển Tab
        btnAdminTabLHP.setOnClickListener(v -> switchTab(0));
        btnAdminTabMonHoc.setOnClickListener(v -> switchTab(1));
        btnAdminTabSinhVien.setOnClickListener(v -> switchTab(2));
        btnAdminTabThongKe.setOnClickListener(v -> switchTab(3));

        cardAdminStatLop.setOnClickListener(v -> switchTab(0));
        cardAdminStatMon.setOnClickListener(v -> switchTab(1));
        cardAdminStatSv.setOnClickListener(v -> switchTab(2));

        // Tìm kiếm thời gian thực theo từng Tab
        edtAdminSearchLHP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s != null ? s.toString() : "";
                if (currentTab == 0 && adapter != null) {
                    adapter.filter(currentSearchQuery);
                    tvAdminClassTotalBadge.setText(adapter.getItemCount() + " lớp");
                    tvAdminEmptyLHP.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                } else if (currentTab == 1) {
                    renderMonHocTab();
                } else if (currentTab == 2) {
                    renderSinhVienTab();
                } else if (currentTab == 3) {
                    renderThongKeCamThiTab();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Bấm FloatingActionButton mở Dialog Thêm mới theo đúng Tab đang chọn
        fabAddLHP.setOnClickListener(v -> {
            if (currentTab == 0) {
                showAddEditDialog(null);
            } else if (currentTab == 1) {
                showMonHocDialog(null);
            } else if (currentTab == 2) {
                showSinhVienDialog(null);
            }
        });
    }

    @Override
    public void onItemClick(LopHocPhan lhp) {
        String[] options = {
                "👥 Xem DS Sinh Viên & Cảnh Báo Cấm Thi của lớp",
                "➕ Ghi danh thêm Sinh Viên vào lớp này",
                "✏️ Chỉnh sửa thông tin Lớp Học Phần",
                "🗑️ Xóa Lớp Học Phần"
        };
        new MaterialAlertDialogBuilder(this)
                .setTitle(lhp.getTenLHP() + " (" + lhp.getMaLHP() + ")")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showClassStudentsAndWarningsDialog(lhp);
                    } else if (which == 1) {
                        showEnrollStudentDialog(lhp);
                    } else if (which == 2) {
                        showAddEditDialog(lhp);
                    } else if (which == 3) {
                        onDeleteClick(lhp);
                    }
                })
                .show();
    }

    private void showClassStudentsAndWarningsDialog(LopHocPhan lhp) {
        List<Map<String, Object>> list = repository.getDanhSachCanhBaoCamThi(lhp.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("🏫 Phòng: ").append(lhp.getPhongHoc()).append(" | GV: ").append(lhp.getTenGV()).append("\n\n");

        if (list.isEmpty()) {
            sb.append("Chưa có sinh viên hoặc dữ liệu điểm danh trong lớp này.");
        } else {
            for (Map<String, Object> item : list) {
                sb.append("🎓 ").append(item.get("hoTen")).append(" (").append(item.get("maSV")).append(")\n");
                sb.append("   • Có mặt: ").append(item.get("soBuoiCoMat"))
                        .append(" | Đi trễ: ").append(item.get("soBuoiDiTre"))
                        .append(" | Vắng: ").append(item.get("soBuoiVang")).append("/").append(item.get("tongSoBuoi")).append("\n");
                sb.append("   • Trạng thái: ").append(item.get("khuyenNghi")).append("\n\n");
            }
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("DS Sinh Viên Lớp " + lhp.getMaLHP())
                .setMessage(sb.toString().trim())
                .setPositiveButton("➕ Ghi Danh Thêm SV", (d, w) -> showEnrollStudentDialog(lhp))
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void showEnrollStudentDialog(LopHocPhan lhp) {
        List<Map<String, Object>> allStudents = repository.getDanhSachSinhVienServer();
        if (allStudents.isEmpty()) {
            Toast.makeText(this, "Không lấy được danh sách sinh viên từ Server!", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] labels = new String[allStudents.size()];
        for (int i = 0; i < allStudents.size(); i++) {
            Map<String, Object> sv = allStudents.get(i);
            labels[i] = sv.get("maSV") + " - " + sv.get("hoTen") + " (" + sv.get("lop") + ")";
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Chọn Sinh Viên Ghi Danh Vào Lớp " + lhp.getMaLHP())
                .setItems(labels, (dialog, which) -> {
                    Map<String, Object> chosen = allStudents.get(which);
                    int svId = (int) chosen.get("id");
                    boolean ok = repository.dangKySinhVienVaoLopServer(lhp.getId(), svId);
                    if (ok) {
                        Toast.makeText(this, "Đã ghi danh sinh viên " + chosen.get("hoTen") + " vào lớp " + lhp.getMaLHP() + "!", Toast.LENGTH_SHORT).show();
                        loadStatsAndData();
                    } else {
                        Toast.makeText(this, "Sinh viên này đã có trong lớp học phần!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onEditClick(LopHocPhan lhp) {
        showAddEditDialog(lhp);
    }

    @Override
    public void onDeleteClick(LopHocPhan lhp) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xác Nhận Xóa Lớp")
                .setMessage("Bạn có chắc chắn muốn xóa lớp học phần:\n\"" + lhp.getTenLHP() + "\" (" + lhp.getMaLHP() + ") không?")
                .setPositiveButton("Xóa Vĩnh Viễn", (dialog, which) -> {
                    boolean success = repository.xoaLopHocPhan(lhp.getMaLHP());
                    if (success) {
                        Toast.makeText(this, "Đã xóa thành công lớp: " + lhp.getMaLHP(), Toast.LENGTH_SHORT).show();
                        loadStatsAndData();
                    } else {
                        Toast.makeText(this, "Xóa lớp học phần thất bại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Dialog Thêm mới hoặc Sửa thông tin LopHocPhan khớp 100% với bảng LopHocPhan trên Web API
     */
    private void showAddEditDialog(LopHocPhan itemToEdit) {
        boolean isEdit = (itemToEdit != null);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_lop_hoc_phan, null);
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        Spinner spMonHoc = dialogView.findViewById(R.id.spDlgMonHoc);
        TextInputLayout tilMaLHP = dialogView.findViewById(R.id.tilDlgMaLHP);
        TextInputEditText edtMaLHP = dialogView.findViewById(R.id.edtDlgMaLHP);
        TextInputEditText edtTenGV = dialogView.findViewById(R.id.edtDlgTenGV);
        TextInputEditText edtPhongHoc = dialogView.findViewById(R.id.edtDlgPhongHoc);
        TextInputEditText edtSoBuoi = dialogView.findViewById(R.id.edtDlgSoBuoi);
        TextInputEditText edtHocKy = dialogView.findViewById(R.id.edtDlgHocKy);
        TextInputEditText edtNamHoc = dialogView.findViewById(R.id.edtDlgNamHoc);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnDlgCancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnDlgSave);

        List<Map<String, Object>> monHocs = repository.getDanhSachMonHocServer();
        List<String> monHocLabels = new ArrayList<>();
        int selectedMonIndex = 0;
        for (int i = 0; i < monHocs.size(); i++) {
            Map<String, Object> m = monHocs.get(i);
            int mId = (int) m.get("id");
            monHocLabels.add(m.get("maMon") + " - " + m.get("tenMon") + " (" + m.get("soTinChi") + " TC)");
            if (isEdit && itemToEdit.getMonHocId() == mId) {
                selectedMonIndex = i;
            }
        }
        ArrayAdapter<String> monAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, monHocLabels);
        spMonHoc.setAdapter(monAdapter);
        if (!monHocLabels.isEmpty()) {
            spMonHoc.setSelection(selectedMonIndex);
        }

        if (isEdit) {
            tvTitle.setText("CẬP NHẬT LỚP HỌC PHẦN");
            edtMaLHP.setText(itemToEdit.getMaLHP());
            edtMaLHP.setEnabled(false);
            tilMaLHP.setEnabled(false);
            edtTenGV.setText(itemToEdit.getTenGV());
            edtPhongHoc.setText(itemToEdit.getPhongHoc());
            edtSoBuoi.setText(String.valueOf(itemToEdit.getSoBuoi()));
            edtHocKy.setText(itemToEdit.getHocKy() != null ? itemToEdit.getHocKy() : "HK1");
            edtNamHoc.setText(itemToEdit.getNamHoc() != null ? itemToEdit.getNamHoc() : "2026-2027");
            btnSave.setText("LƯU THAY ĐỔI");
        } else {
            tvTitle.setText("THÊM LỚP HỌC PHẦN MỚI");
            edtMaLHP.setEnabled(true);
            tilMaLHP.setEnabled(true);
            edtTenGV.setText("ThS. Nguyễn Hữu Trung");
            edtPhongHoc.setText("A-04.05");
            edtSoBuoi.setText("15");
            edtHocKy.setText("HK1");
            edtNamHoc.setText("2026-2027");
            btnSave.setText("THÊM MỚI");
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String maLHP = edtMaLHP.getText() != null ? edtMaLHP.getText().toString().trim() : "";
            String tenGV = edtTenGV.getText() != null ? edtTenGV.getText().toString().trim() : "";
            String phongHoc = edtPhongHoc.getText() != null ? edtPhongHoc.getText().toString().trim() : "";
            String strSoBuoi = edtSoBuoi.getText() != null ? edtSoBuoi.getText().toString().trim() : "15";
            String hocKy = edtHocKy.getText() != null ? edtHocKy.getText().toString().trim() : "HK1";
            String namHoc = edtNamHoc.getText() != null ? edtNamHoc.getText().toString().trim() : "2026-2027";

            if (TextUtils.isEmpty(maLHP)) {
                edtMaLHP.setError("Vui lòng nhập mã lớp học phần");
                edtMaLHP.requestFocus();
                return;
            }

            int pos = spMonHoc.getSelectedItemPosition();
            int monHocId = 1;
            String tenMonHoc = maLHP;
            String maMon = "LTDD01";
            int soTinChi = 3;
            if (pos >= 0 && pos < monHocs.size()) {
                Map<String, Object> chosenMon = monHocs.get(pos);
                monHocId = (int) chosenMon.get("id");
                tenMonHoc = String.valueOf(chosenMon.get("tenMon"));
                maMon = String.valueOf(chosenMon.get("maMon"));
                soTinChi = (int) chosenMon.get("soTinChi");
            }

            int soBuoi = 15;
            try { soBuoi = Integer.parseInt(strSoBuoi); } catch (Exception ignored) {}

            if (isEdit) {
                itemToEdit.setMonHocId(monHocId);
                itemToEdit.setMaMon(maMon);
                itemToEdit.setSoTinChi(soTinChi);
                itemToEdit.setTenLHP(tenMonHoc);
                itemToEdit.setTenMonHoc(tenMonHoc);
                itemToEdit.setTenGV(tenGV.isEmpty() ? "ThS. Nguyễn Hữu Trung" : tenGV);
                itemToEdit.setPhongHoc(phongHoc.isEmpty() ? "A-04.05" : phongHoc);
                itemToEdit.setSoBuoi(soBuoi);
                itemToEdit.setHocKy(hocKy.isEmpty() ? "HK1" : hocKy);
                itemToEdit.setNamHoc(namHoc.isEmpty() ? "2026-2027" : namHoc);

                boolean ok = repository.capNhatLopHocPhan(itemToEdit);
                if (ok) {
                    Toast.makeText(this, "Đã cập nhật lớp học phần: " + itemToEdit.getMaLHP(), Toast.LENGTH_SHORT).show();
                    loadStatsAndData();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                LopHocPhan newLHP = new LopHocPhan(
                        maLHP,
                        tenMonHoc,
                        tenMonHoc,
                        "ND_GV01",
                        tenGV.isEmpty() ? "ThS. Nguyễn Hữu Trung" : tenGV,
                        phongHoc.isEmpty() ? "A-04.05" : phongHoc,
                        Math.max(1, soBuoi / 5),
                        0
                );
                newLHP.setMonHocId(monHocId);
                newLHP.setMaMon(maMon);
                newLHP.setSoTinChi(soTinChi);
                newLHP.setSoBuoi(soBuoi);
                newLHP.setHocKy(hocKy.isEmpty() ? "HK1" : hocKy);
                newLHP.setNamHoc(namHoc.isEmpty() ? "2026-2027" : namHoc);

                boolean ok = repository.themLopHocPhan(newLHP);
                if (ok) {
                    Toast.makeText(this, "Đã thêm lớp học phần mới thành công!", Toast.LENGTH_SHORT).show();
                    loadStatsAndData();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Thêm mới thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }
}
