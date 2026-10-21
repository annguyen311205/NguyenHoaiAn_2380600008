package com.nguyenhoaian2380600008.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.nguyenhoaian2380600008.R;
import com.nguyenhoaian2380600008.models.ChiTietDiemDanh;
import java.util.ArrayList;
import java.util.List;

public class ChiTietDiemDanhAdapter extends RecyclerView.Adapter<ChiTietDiemDanhAdapter.ViewHolder> {

    public interface OnChiTietStatusClickListener {
        void onStatusClick(ChiTietDiemDanh chiTiet);
    }

    private final List<ChiTietDiemDanh> list;
    private final OnChiTietStatusClickListener listener;

    public ChiTietDiemDanhAdapter(List<ChiTietDiemDanh> list, OnChiTietStatusClickListener listener) {
        this.list = new ArrayList<>(list);
        this.listener = listener;
    }

    public void updateData(List<ChiTietDiemDanh> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chi_tiet_diem_danh, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChiTietDiemDanh item = list.get(position);
        holder.tvTenSV.setText(item.getTenSV());
        holder.tvMaSV.setText("MSSV: " + item.getMaSV());

        String timeInfo = (item.getThoiGianDiemDanh() != null && !item.getThoiGianDiemDanh().isEmpty())
                ? "🕒 " + item.getThoiGianDiemDanh() + " - Cách " + item.getKhoangCachMet() + "m"
                : "Chưa điểm danh qua thiết bị";
        holder.tvThoiGian.setText(timeInfo);

        // Hiển thị trạng thái theo màu sắc trực quan
        Context context = holder.itemView.getContext();
        String trangThai = item.getTrangThai() != null ? item.getTrangThai() : "VangKhongPhep";
        switch (trangThai) {
            case "CoMat":
                holder.btnTrangThai.setText("Có mặt");
                holder.btnTrangThai.setTextColor(Color.parseColor("#065F46"));
                holder.btnTrangThai.setBackgroundColor(Color.parseColor("#D1FAE5"));
                break;
            case "DiTre":
                holder.btnTrangThai.setText("Đi trễ");
                holder.btnTrangThai.setTextColor(Color.parseColor("#92400E"));
                holder.btnTrangThai.setBackgroundColor(Color.parseColor("#FEF3C7"));
                break;
            case "VangCoPhep":
                holder.btnTrangThai.setText("Vắng có phép");
                holder.btnTrangThai.setTextColor(Color.parseColor("#1E40AF"));
                holder.btnTrangThai.setBackgroundColor(Color.parseColor("#DBEAFE"));
                break;
            case "VangKhongPhep":
            default:
                holder.btnTrangThai.setText("Vắng không phép");
                holder.btnTrangThai.setTextColor(Color.parseColor("#991B1B"));
                holder.btnTrangThai.setBackgroundColor(Color.parseColor("#FEE2E2"));
                break;
        }

        holder.btnTrangThai.setOnClickListener(v -> {
            if (listener != null) listener.onStatusClick(item);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onStatusClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenSV, tvMaSV, tvThoiGian;
        MaterialButton btnTrangThai;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenSV = itemView.findViewById(R.id.tvCtTenSV);
            tvMaSV = itemView.findViewById(R.id.tvCtMaSV);
            tvThoiGian = itemView.findViewById(R.id.tvCtThoiGian);
            btnTrangThai = itemView.findViewById(R.id.btnCtTrangThai);
        }
    }
}
