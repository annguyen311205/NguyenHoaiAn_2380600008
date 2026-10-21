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

public class DonXinPhepAdapter extends RecyclerView.Adapter<DonXinPhepAdapter.ViewHolder> {

    public interface OnDonXinPhepListener {
        void onEdit(DonXinPhep don);
        void onDelete(DonXinPhep don);
    }

    private final List<DonXinPhep> list;
    private final OnDonXinPhepListener listener;

    public DonXinPhepAdapter(List<DonXinPhep> list, OnDonXinPhepListener listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_don_xin_phep, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonXinPhep item = list.get(position);
        holder.tvLHP.setText(item.getTenLHP() != null ? item.getTenLHP() : item.getMaLHP());
        holder.tvNgayNghi.setText("📅 Ngày xin nghỉ: " + item.getNgayNghi());
        holder.tvLyDo.setText("Lý do: " + item.getLyDo());

        String status = item.getTrangThaiDuyet() != null ? item.getTrangThaiDuyet() : "ChoDuyet";
        if ("DaDuyet".equalsIgnoreCase(status)) {
            holder.tvTrangThai.setText("ĐÃ DUYỆT");
            holder.tvTrangThai.setTextColor(Color.parseColor("#065F46"));
            holder.tvTrangThai.setBackgroundColor(Color.parseColor("#D1FAE5"));
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.GONE);
        } else if ("TuChoi".equalsIgnoreCase(status)) {
            holder.tvTrangThai.setText("TỪ CHỐI");
            holder.tvTrangThai.setTextColor(Color.parseColor("#991B1B"));
            holder.tvTrangThai.setBackgroundColor(Color.parseColor("#FEE2E2"));
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.GONE);
        } else {
            holder.tvTrangThai.setText("CHỜ DUYỆT");
            holder.tvTrangThai.setTextColor(Color.parseColor("#92400E"));
            holder.tvTrangThai.setBackgroundColor(Color.parseColor("#FEF3C7"));
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.VISIBLE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLHP, tvNgayNghi, tvLyDo, tvTrangThai;
        LinearLayout layoutActions;
        MaterialButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLHP = itemView.findViewById(R.id.tvItemSvDonLHP);
            tvNgayNghi = itemView.findViewById(R.id.tvItemSvDonNgayNghi);
            tvLyDo = itemView.findViewById(R.id.tvItemSvDonLyDo);
            tvTrangThai = itemView.findViewById(R.id.tvItemSvDonTrangThai);
            layoutActions = itemView.findViewById(R.id.layoutSvDonActions);
            btnEdit = itemView.findViewById(R.id.btnSvEditDon);
            btnDelete = itemView.findViewById(R.id.btnSvDeleteDon);
        }
    }
}
