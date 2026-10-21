package com.nguyenhoaian2380600008.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ChiTietDiemDanh implements Serializable {
    @SerializedName("MaCT")
    private String MaCT;

    @SerializedName("MaPhien")
    private String MaPhien;

    @SerializedName("MaSV")
    private String MaSV;

    @SerializedName("TenSV")
    private String TenSV;

    @SerializedName("ThoiGianDiemDanh")
    private String ThoiGianDiemDanh;

    @SerializedName("TrangThai")
    private String TrangThai; // "CoMat", "DiTre", "VangCoPhep", "VangKhongPhep"

    @SerializedName("KhoangCachMet")
    private double KhoangCachMet;

    @SerializedName("MaThietBiSV")
    private String MaThietBiSV;

    @SerializedName("id")
    private int id;

    @SerializedName("buoiDiemDanhId")
    private int buoiDiemDanhId;

    @SerializedName("sinhVienId")
    private int sinhVienId;

    @SerializedName("phuongThuc")
    private String phuongThuc = "QR_Code";

    @SerializedName("hopLe")
    private boolean hopLe = true;

    @SerializedName("soThuTuBuoi")
    private int soThuTuBuoi = 1;

    @SerializedName("tenBuoi")
    private String tenBuoi = "";

    @SerializedName("maLopHocPhan")
    private String maLopHocPhan = "24DTH01_LTDD";

    @SerializedName("tenMonHoc")
    private String tenMonHoc = "Lập trình thiết bị di động";

    @SerializedName("phongHoc")
    private String phongHoc = "A-04.05";

    @SerializedName("ghiChu")
    private String ghiChu = "";

    public ChiTietDiemDanh() {
    }

    public ChiTietDiemDanh(String maCT, String maPhien, String maSV, String tenSV, String thoiGianDiemDanh, String trangThai, double khoangCachMet, String maThietBiSV) {
        this.MaCT = maCT;
        this.MaPhien = maPhien;
        this.MaSV = maSV;
        this.TenSV = tenSV;
        this.ThoiGianDiemDanh = thoiGianDiemDanh;
        this.TrangThai = trangThai;
        this.KhoangCachMet = khoangCachMet;
        this.MaThietBiSV = maThietBiSV;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuoiDiemDanhId() {
        return buoiDiemDanhId;
    }

    public void setBuoiDiemDanhId(int buoiDiemDanhId) {
        this.buoiDiemDanhId = buoiDiemDanhId;
    }

    public int getSinhVienId() {
        return sinhVienId;
    }

    public void setSinhVienId(int sinhVienId) {
        this.sinhVienId = sinhVienId;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public boolean isHopLe() {
        return hopLe;
    }

    public void setHopLe(boolean hopLe) {
        this.hopLe = hopLe;
    }

    public int getSoThuTuBuoi() {
        return soThuTuBuoi;
    }

    public void setSoThuTuBuoi(int soThuTuBuoi) {
        this.soThuTuBuoi = soThuTuBuoi;
    }

    public String getTenBuoi() {
        return tenBuoi;
    }

    public void setTenBuoi(String tenBuoi) {
        this.tenBuoi = tenBuoi;
    }

    public String getMaLopHocPhan() {
        return maLopHocPhan;
    }

    public void setMaLopHocPhan(String maLopHocPhan) {
        this.maLopHocPhan = maLopHocPhan;
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public void setTenMonHoc(String tenMonHoc) {
        this.tenMonHoc = tenMonHoc;
    }

    public String getPhongHoc() {
        return phongHoc;
    }

    public void setPhongHoc(String phongHoc) {
        this.phongHoc = phongHoc;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getMaCT() {
        return MaCT;
    }

    public void setMaCT(String maCT) {
        this.MaCT = maCT;
    }

    public String getMaPhien() {
        return MaPhien;
    }

    public void setMaPhien(String maPhien) {
        this.MaPhien = maPhien;
    }

    public String getMaSV() {
        return MaSV;
    }

    public void setMaSV(String maSV) {
        this.MaSV = maSV;
    }

    public String getTenSV() {
        return TenSV;
    }

    public void setTenSV(String tenSV) {
        this.TenSV = tenSV;
    }

    public String getThoiGianDiemDanh() {
        return ThoiGianDiemDanh;
    }

    public void setThoiGianDiemDanh(String thoiGianDiemDanh) {
        this.ThoiGianDiemDanh = thoiGianDiemDanh;
    }

    public String getTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(String trangThai) {
        this.TrangThai = trangThai;
    }

    public double getKhoangCachMet() {
        return KhoangCachMet;
    }

    public void setKhoangCachMet(double khoangCachMet) {
        this.KhoangCachMet = khoangCachMet;
    }

    public String getMaThietBiSV() {
        return MaThietBiSV;
    }

    public void setMaThietBiSV(String maThietBiSV) {
        this.MaThietBiSV = maThietBiSV;
    }
}
