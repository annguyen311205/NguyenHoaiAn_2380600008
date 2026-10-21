package com.nguyenhoaian2380600008.activities.sinhvien;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.nguyenhoaian2380600008.R;
import com.nguyenhoaian2380600008.adapters.DonXinPhepAdapter;
import com.nguyenhoaian2380600008.api.MockDataRepository;
import com.nguyenhoaian2380600008.models.DonXinPhep;
import com.nguyenhoaian2380600008.models.LopHocPhan;
import com.nguyenhoaian2380600008.models.NguoiDung;
import com.nguyenhoaian2380600008.utils.SessionManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DonXinPhepActivity extends AppCompatActivity implements DonXinPhepAdapter.OnDonXinPhepListener {

    private ImageView btnDonBack;
    private RecyclerView rvSvDonXinPhep;
    private TextView tvEmptyDonSv;
    private ExtendedFloatingActionButton fabSvCreateDon;

    private SessionManager sessionManager;
    private MockDataRepository repository;
    private DonXinPhepAdapter adapter;

    private String currentStudentId = "2380600008";
    private String currentStudentName = "Nguyễn Hoài An";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_xin_phep);

        sessionManager = SessionManager.getInstance(this);
        repository = MockDataRepository.getInstance();

        NguoiDung user = sessionManager.getCurrentUser();
        if (user != null) {
            currentStudentId = user.getTenDangNhap();
            currentStudentName = user.getHoTen();
        }

        initViews();
        setupRecyclerView();
        loadData();
        setupEvents();
    }

    private void initViews() {
        btnDonBack = findViewById(R.id.btnDonBack);
        rvSvDonXinPhep = findViewById(R.id.rvSvDonXinPhep);
        tvEmptyDonSv = findViewById(R.id.tvEmptyDonSv);
        fabSvCreateDon = findViewById(R.id.fabSvCreateDon);
    }

    private void setupRecyclerView() {
        rvSvDonXinPhep.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DonXinPhepAdapter(new ArrayList<>(), this);
        rvSvDonXinPhep.setAdapter(adapter);
    }

    private void loadData() {
        List<DonXinPhep> list = repository.getDonXinPhepTheoSV(currentStudentId);
        adapter.updateData(list);
        tvEmptyDonSv.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setupEvents() {
        btnDonBack.setOnClickListener(v -> finish());
        fabSvCreateDon.setOnClickListener(v -> showAddEditDialog(null));
    }

    @Override
    public void onEdit(DonXinPhep don) {
        if (!"ChoDuyet".equalsIgnoreCase(don.getTrangThaiDuyet())) {
            Toast.makeText(this, "Chỉ có thể chỉnh sửa đơn đang ở trạng thái 'Chờ duyệt'!", Toast.LENGTH_SHORT).show();
            return;
        }
        showAddEditDialog(don);
    }

    @Override
    public void onDelete(DonXinPhep don) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xóa / Hủy Đơn Xin Phép")
                .setMessage("Bạn có chắc chắn muốn xóa/hủy đơn xin nghỉ học ngày " + don.getNgayNghi() + " không?")
                .setPositiveButton("Xóa Đơn", (dialog, which) -> {
                    boolean ok = repository.xoaDonXinPhep(don.getMaDon());
                    if (ok) {
                        Toast.makeText(this, "Đã xóa đơn xin phép thành công!", Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                })
                .setNegativeButton("Quay Lại", null)
                .show();
    }

    /**
     * Dialog Thêm mới hoặc Sửa Đơn Xin Phép
     */
    private void showAddEditDialog(DonXinPhep itemToEdit) {
        boolean isEdit = (itemToEdit != null);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_don_xin_phep, null);
        TextView tvTitle = dialogView.findViewById(R.id.tvDlgDonTitle);
        Spinner spLHP = dialogView.findViewById(R.id.spDlgDonLHP);
        TextInputEditText edtNgayNghi = dialogView.findViewById(R.id.edtDlgDonNgayNghi);
        TextInputEditText edtLyDo = dialogView.findViewById(R.id.edtDlgDonLyDo);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnDlgCancelDon);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnDlgSaveDon);

        // Nạp danh sách Lớp học phần vào Spinner
        List<LopHocPhan> classes = repository.getDanhSachLopHocPhan();
        List<String> classNames = new ArrayList<>();
        int selectedIndex = 0;
        for (int i = 0; i < classes.size(); i++) {
            LopHocPhan lhp = classes.get(i);
            classNames.add(lhp.getMaLHP() + " - " + lhp.getTenLHP());
            if (isEdit && lhp.getMaLHP().equals(itemToEdit.getMaLHP())) {
                selectedIndex = i;
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, classNames);
        spLHP.setAdapter(spinnerAdapter);
        spLHP.setSelection(selectedIndex);

        if (isEdit) {
            tvTitle.setText("SỬA ĐƠN XIN NGHỈ HỌC");
            edtNgayNghi.setText(itemToEdit.getNgayNghi());
            edtLyDo.setText(itemToEdit.getLyDo());
            btnSave.setText("CẬP NHẬT ĐƠN");
        } else {
            tvTitle.setText("TẠO ĐƠN XIN NGHỈ HỌC");
            Calendar now = Calendar.getInstance();
            String defaultDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH));
            edtNgayNghi.setText(defaultDate);
            btnSave.setText("GỬI ĐƠN PHÉP");
        }

        // Chọn ngày qua DatePickerDialog
        edtNgayNghi.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String pickedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                edtNgayNghi.setText(pickedDate);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String ngayNghi = edtNgayNghi.getText() != null ? edtNgayNghi.getText().toString().trim() : "";
            String lyDo = edtLyDo.getText() != null ? edtLyDo.getText().toString().trim() : "";

            if (TextUtils.isEmpty(lyDo)) {
                edtLyDo.setError("Vui lòng nhập lý do xin nghỉ");
                edtLyDo.requestFocus();
                return;
            }

            int pos = spLHP.getSelectedItemPosition();
            LopHocPhan chosen = (pos >= 0 && pos < classes.size()) ? classes.get(pos) : classes.get(0);

            if (isEdit) {
                itemToEdit.setMaLHP(chosen.getMaLHP());
                itemToEdit.setTenLHP(chosen.getTenLHP());
                itemToEdit.setNgayNghi(ngayNghi);
                itemToEdit.setLyDo(lyDo);

                boolean ok = repository.capNhatDonXinPhep(itemToEdit);
                if (ok) {
                    Toast.makeText(this, "Đã cập nhật đơn xin phép!", Toast.LENGTH_SHORT).show();
                    loadData();
                    dialog.dismiss();
                }
            } else {
                DonXinPhep newDon = new DonXinPhep(
                        "DON_" + System.currentTimeMillis(),
                        currentStudentId,
                        currentStudentName,
                        chosen.getMaLHP(),
                        chosen.getTenLHP(),
                        ngayNghi,
                        lyDo,
                        "ChoDuyet"
                );

                boolean ok = repository.guiDonXinPhep(newDon);
                if (ok) {
                    Toast.makeText(this, "Đã gửi đơn xin nghỉ học thành công!", Toast.LENGTH_SHORT).show();
                    loadData();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }
}
