package com.nguyenhoaian2380600008.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.nguyenhoaian2380600008.R;
import com.nguyenhoaian2380600008.models.DonXinPhep;
import java.util.ArrayList;
import java.util.List;

public class DuyetDonAdapter extends RecyclerView.Adapter<DuyetDonAdapter.ViewHolder> {

    public interface OnDuyetDonListener {
        void onDuyet(DonXinPhep don);
        void onTuChoi(DonXinPhep don);
    }

    private final List<DonXinPhep> list;
    private final OnDuyetDonListener listener;

    public DuyetDonAdapter(List<DonXinPhep> list, OnDuyetDonListener listener) {
        this.list = new ArrayList<>(list);
        this.listener = listener;
    }

    public void updateData(List<DonXinPhep> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_duyet_don, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonXinPhep item = list.get(position);
        holder.tvTenSV.setText(item.getTenSV());
        holder.tvMaSV.setText("MSSV: " + item.getMaSV());
        holder.tvLHP.setText("📚 Lớp: " + (item.getTenLHP() != null ? item.getTenLHP() : item.getMaLHP()));
        holder.tvNgayNghi.setText("📅 Ngày xin nghỉ: " + item.getNgayNghi());
        holder.tvLyDo.setText("Lý do: " + item.getLyDo());

        String status = item.getTrangThaiDuyet() != null ? item.getTrangThaiDuyet() : "ChoDuyet";
        if ("DaDuyet".equalsIgnoreCase(status)) {
            holder.tvTrangThaiBadge.setText("ĐÃ DUYỆT");
            holder.tvTrangThaiBadge.setTextColor(Color.parseColor("#065F46"));
            holder.tvTrangThaiBadge.setBackgroundColor(Color.parseColor("#D1FAE5"));
            holder.layoutActionDuyet.setVisibility(View.VISIBLE);
        } else if ("TuChoi".equalsIgnoreCase(status)) {
            holder.tvTrangThaiBadge.setText("TỪ CHỐI");
            holder.tvTrangThaiBadge.setTextColor(Color.parseColor("#991B1B"));
            holder.tvTrangThaiBadge.setBackgroundColor(Color.parseColor("#FEE2E2"));
            holder.layoutActionDuyet.setVisibility(View.VISIBLE);
        } else {
            holder.tvTrangThaiBadge.setText("CHỜ DUYỆT");
            holder.tvTrangThaiBadge.setTextColor(Color.parseColor("#92400E"));
            holder.tvTrangThaiBadge.setBackgroundColor(Color.parseColor("#FEF3C7"));
            holder.layoutActionDuyet.setVisibility(View.VISIBLE);
        }

        holder.btnDongYDuyet.setOnClickListener(v -> {
            if (listener != null) listener.onDuyet(item);
        });

        holder.btnTuChoiDon.setOnClickListener(v -> {
            if (listener != null) listener.onTuChoi(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenSV, tvMaSV, tvLHP, tvNgayNghi, tvLyDo, tvTrangThaiBadge;
        LinearLayout layoutActionDuyet;
        MaterialButton btnDongYDuyet, btnTuChoiDon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenSV = itemView.findViewById(R.id.tvDuyetTenSV);
            tvMaSV = itemView.findViewById(R.id.tvDuyetMaSV);
            tvLHP = itemView.findViewById(R.id.tvDuyetLHP);
            tvNgayNghi = itemView.findViewById(R.id.tvDuyetNgayNghi);
            tvLyDo = itemView.findViewById(R.id.tvDuyetLyDo);
            tvTrangThaiBadge = itemView.findViewById(R.id.tvDuyetTrangThaiBadge);
            layoutActionDuyet = itemView.findViewById(R.id.layoutActionDuyet);
            btnDongYDuyet = itemView.findViewById(R.id.btnDongYDuyet);
            btnTuChoiDon = itemView.findViewById(R.id.btnTuChoiDon);
        }
    }
}
