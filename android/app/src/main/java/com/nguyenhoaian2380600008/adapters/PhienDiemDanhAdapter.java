package com.nguyenhoaian2380600008.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.nguyenhoaian2380600008.R;
import com.nguyenhoaian2380600008.models.PhienDiemDanh;
import java.util.ArrayList;
import java.util.List;

public class PhienDiemDanhAdapter extends RecyclerView.Adapter<PhienDiemDanhAdapter.ViewHolder> {

    public interface OnPhienListener {
        void onViewStudents(PhienDiemDanh phien);
        void onClosePhien(PhienDiemDanh phien);
        void onDeletePhien(PhienDiemDanh phien);
        void onShowQr(PhienDiemDanh phien);
    }

    private final List<PhienDiemDanh> list;
    private final OnPhienListener listener;

    public PhienDiemDanhAdapter(List<PhienDiemDanh> list, OnPhienListener listener) {
        this.list = new ArrayList<>(list);
        this.listener = listener;
    }

    public void updateData(List<PhienDiemDanh> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phien_diem_danh, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PhienDiemDanh item = list.get(position);
        holder.tvMaLHP.setText(item.getMaLHP() + " • Buổi " + item.getSoThuTuBuoi());

        String title = (item.getGhiChu() != null && !item.getGhiChu().trim().isEmpty())
                ? item.getGhiChu().trim()
                : ("Buổi " + item.getSoThuTuBuoi() + ": " + item.getTenMonHoc());
        holder.tvMaPhien.setText(title);

        holder.tvNgayTao.setText("📅 Ngày: " + item.getNgayTao() + " | 🔑 Mã QR: " + item.getMaQR());
        String phong = (item.getPhongHoc() != null && !item.getPhongHoc().isEmpty()) ? item.getPhongHoc() : "A-04.05";
        holder.tvToaDo.setText("📍 Phòng: " + phong + " • Bán kính GPS: " + (int) item.getBanKinhChoPhepMet()
                + "m (" + item.getViDoGV() + ", " + item.getKinhDoGV() + ")");

        if (item.isTrangThaiMo()) {
            holder.tvTrangThai.setText("ĐANG MỞ");
            holder.tvTrangThai.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.success));
            holder.btnClose.setVisibility(View.VISIBLE);
            holder.btnClose.setText("Đóng Phiên");
            holder.btnClose.setTextColor(Color.parseColor("#DC2626"));
        } else {
            String rawSt = item.getTrangThaiRaw() != null ? item.getTrangThaiRaw() : "DaDong";
            holder.tvTrangThai.setText("DaKetThuc".equalsIgnoreCase(rawSt) ? "ĐÃ KẾT THÚC" : "ĐÃ ĐÓNG");
            holder.tvTrangThai.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary));
            holder.btnClose.setVisibility(View.VISIBLE);
            holder.btnClose.setText("Mở Lại Phiên");
            holder.btnClose.setTextColor(Color.parseColor("#2563EB"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onShowQr(item);
        });

        holder.btnViewStudents.setOnClickListener(v -> {
            if (listener != null) listener.onViewStudents(item);
        });

        holder.btnClose.setOnClickListener(v -> {
            if (listener != null) listener.onClosePhien(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeletePhien(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaLHP, tvTrangThai, tvMaPhien, tvNgayTao, tvToaDo;
        MaterialButton btnViewStudents, btnClose;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaLHP = itemView.findViewById(R.id.tvPhienMaLHP);
            tvTrangThai = itemView.findViewById(R.id.tvPhienTrangThai);
            tvMaPhien = itemView.findViewById(R.id.tvPhienMa);
            tvNgayTao = itemView.findViewById(R.id.tvPhienNgayTao);
            tvToaDo = itemView.findViewById(R.id.tvPhienToaDo);
            btnViewStudents = itemView.findViewById(R.id.btnPhienViewStudents);
            btnClose = itemView.findViewById(R.id.btnPhienClose);
            btnDelete = itemView.findViewById(R.id.btnPhienDelete);
        }
    }
}
