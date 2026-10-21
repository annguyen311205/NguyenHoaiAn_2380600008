package com.nguyenhoaian2380600008.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nguyenhoaian2380600008.R;
import com.nguyenhoaian2380600008.models.LopHocPhan;
import java.util.ArrayList;
import java.util.List;

public class LopHocPhanAdapter extends RecyclerView.Adapter<LopHocPhanAdapter.ViewHolder> {

    public interface OnLopHocPhanListener {
        void onItemClick(LopHocPhan lhp);
        void onEditClick(LopHocPhan lhp);
        void onDeleteClick(LopHocPhan lhp);
    }

    private final List<LopHocPhan> listOriginal;
    private final List<LopHocPhan> listDisplay;
    private final OnLopHocPhanListener listener;

    public LopHocPhanAdapter(List<LopHocPhan> list, OnLopHocPhanListener listener) {
        this.listOriginal = new ArrayList<>(list);
        this.listDisplay = new ArrayList<>(list);
        this.listener = listener;
    }

    public void updateData(List<LopHocPhan> newList) {
        this.listOriginal.clear();
        this.listOriginal.addAll(newList);
        this.listDisplay.clear();
        this.listDisplay.addAll(newList);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        listDisplay.clear();
        if (query == null || query.trim().isEmpty()) {
            listDisplay.addAll(listOriginal);
        } else {
            String lower = query.trim().toLowerCase();
            for (LopHocPhan item : listOriginal) {
                if ((item.getTenLHP() != null && item.getTenLHP().toLowerCase().contains(lower)) ||
                    (item.getMaLHP() != null && item.getMaLHP().toLowerCase().contains(lower)) ||
                    (item.getTenMonHoc() != null && item.getTenMonHoc().toLowerCase().contains(lower)) ||
                    (item.getTenGV() != null && item.getTenGV().toLowerCase().contains(lower))) {
                    listDisplay.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lop_hoc_phan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LopHocPhan item = listDisplay.get(position);
        holder.tvMaLHP.setText("Mã LHP: " + item.getMaLHP());
        holder.tvTenLHP.setText(item.getTenMonHoc() != null ? item.getTenMonHoc() : item.getTenLHP());

        String maMon = item.getMaMon() != null ? item.getMaMon() : ("MH" + item.getMonHocId());
        String hocKy = item.getHocKy() != null ? item.getHocKy() : "HK1";
        String namHoc = item.getNamHoc() != null ? item.getNamHoc() : "2026-2027";
        holder.tvTenMonHoc.setText("📖 Mã môn: " + maMon + " (" + item.getSoTinChi() + " TC) • " + hocKy + " (" + namHoc + ")");

        holder.tvGiangVien.setText("👨‍🏫 GV: " + (item.getTenGV() != null ? item.getTenGV() : "Chưa phân công"));
        holder.tvPhongHoc.setText("📍 Phòng: " + (item.getPhongHoc() != null ? item.getPhongHoc() : "N/A"));
        holder.tvVangToiDa.setText("📅 Thời lượng: " + item.getSoBuoi() + " buổi");
        holder.tvSiSo.setText("👥 Đã đăng ký: " + item.getSiSo() + " SV");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onDeleteClick(item);
            return true;
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return listDisplay.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaLHP, tvTenLHP, tvTenMonHoc, tvGiangVien, tvPhongHoc, tvVangToiDa, tvSiSo;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaLHP = itemView.findViewById(R.id.tvItemMaLHP);
            tvTenLHP = itemView.findViewById(R.id.tvItemTenLHP);
            tvTenMonHoc = itemView.findViewById(R.id.tvItemTenMonHoc);
            tvGiangVien = itemView.findViewById(R.id.tvItemGiangVien);
            tvPhongHoc = itemView.findViewById(R.id.tvItemPhongHoc);
            tvVangToiDa = itemView.findViewById(R.id.tvItemVangToiDa);
            tvSiSo = itemView.findViewById(R.id.tvItemSiSo);
            btnEdit = itemView.findViewById(R.id.btnItemEditLHP);
            btnDelete = itemView.findViewById(R.id.btnItemDeleteLHP);
        }
    }
}
