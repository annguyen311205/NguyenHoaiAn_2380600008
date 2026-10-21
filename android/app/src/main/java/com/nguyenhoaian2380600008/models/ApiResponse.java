package com.nguyenhoaian2380600008.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ApiResponse<T> implements Serializable {
    @SerializedName("ThanhCong")
    private boolean ThanhCong;

    @SerializedName("ThongBao")
    private String ThongBao;

    @SerializedName("DuLieu")
    private T DuLieu;

    public ApiResponse() {
    }

    public ApiResponse(boolean thanhCong, String thongBao, T duLieu) {
        this.ThanhCong = thanhCong;
        this.ThongBao = thongBao;
        this.DuLieu = duLieu;
    }

    public boolean isThanhCong() {
        return ThanhCong;
    }

    public void setThanhCong(boolean thanhCong) {
        this.ThanhCong = thanhCong;
    }

    public String getThongBao() {
        return ThongBao;
    }

    public void setThongBao(String thongBao) {
        this.ThongBao = thongBao;
    }

    public T getDuLieu() {
        return DuLieu;
    }

    public void setDuLieu(T duLieu) {
        this.DuLieu = duLieu;
    }
}
