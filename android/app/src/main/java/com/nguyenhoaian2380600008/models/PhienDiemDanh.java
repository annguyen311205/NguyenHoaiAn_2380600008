package com.nguyenhoaian2380600008.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PhienDiemDanh implements Serializable {
    @SerializedName("MaPhien")
    private String MaPhien;

    @SerializedName("MaLHP")
    private String MaLHP;

    @SerializedName("NgayTao")
    private String NgayTao;

    @SerializedName("ThoiGianHetHan")
    private String ThoiGianHetHan;

    @SerializedName("MaQR")
    private String MaQR;

    @SerializedName("ViDoGV")
    private double ViDoGV;

    @SerializedName("KinhDoGV")
    private double KinhDoGV;

    @SerializedName("TrangThaiMo")
    private boolean TrangThaiMo;

    @SerializedName("id")
    private int id;

    @SerializedName("lopHocPhanId")
    private int lopHocPhanId;

    @SerializedName("soThuTuBuoi")
    private int soThuTuBuoi = 1;

    @SerializedName("banKinhChoPhepMet")
    private double banKinhChoPhepMet = 150.0;

    @SerializedName("ghiChu")
    private String ghiChu = "";

    @SerializedName("ngayDiemDanh")
    private String ngayDiemDanh = "";

    @SerializedName("gioBatDau")
    private String gioBatDau = "07:30:00";

    @SerializedName("gioKetThuc")
    private String gioKetThuc = "11:30:00";

    @SerializedName("trangThaiRaw")
    private String trangThaiRaw = "DangMo";

    @SerializedName("soLuotDiemDanh")
    private int soLuotDiemDanh = 0;

    @SerializedName("tenMonHoc")
    private String tenMonHoc = "";

    @SerializedName("phongHoc")
    private String phongHoc = "";

    public PhienDiemDanh() {
    }

    public PhienDiemDanh(String maPhien, String maLHP, String ngayTao, String thoiGianHetHan, String maQR, double viDoGV, double kinhDoGV, boolean trangThaiMo) {
        this.MaPhien = maPhien;
        this.MaLHP = maLHP;
        this.NgayTao = ngayTao;
        this.ThoiGianHetHan = thoiGianHetHan;
        this.MaQR = maQR;
        this.ViDoGV = viDoGV;
        this.KinhDoGV = kinhDoGV;
        this.TrangThaiMo = trangThaiMo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLopHocPhanId() {
        return lopHocPhanId;
    }

    public void setLopHocPhanId(int lopHocPhanId) {
        this.lopHocPhanId = lopHocPhanId;
    }

    public int getSoThuTuBuoi() {
        return soThuTuBuoi;
    }

    public void setSoThuTuBuoi(int soThuTuBuoi) {
        this.soThuTuBuoi = soThuTuBuoi;
    }

    public double getBanKinhChoPhepMet() {
        return banKinhChoPhepMet;
    }

    public void setBanKinhChoPhepMet(double banKinhChoPhepMet) {
        this.banKinhChoPhepMet = banKinhChoPhepMet;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getNgayDiemDanh() {
        return ngayDiemDanh;
    }

    public void setNgayDiemDanh(String ngayDiemDanh) {
        this.ngayDiemDanh = ngayDiemDanh;
    }

    public String getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(String gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public String getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(String gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    public String getTrangThaiRaw() {
        return trangThaiRaw;
    }

    public void setTrangThaiRaw(String trangThaiRaw) {
        this.trangThaiRaw = trangThaiRaw;
    }

    public int getSoLuotDiemDanh() {
        return soLuotDiemDanh;
    }

    public void setSoLuotDiemDanh(int soLuotDiemDanh) {
        this.soLuotDiemDanh = soLuotDiemDanh;
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

    public String getMaPhien() {
        return MaPhien;
    }

    public void setMaPhien(String maPhien) {
        this.MaPhien = maPhien;
    }

    public String getMaLHP() {
        return MaLHP;
    }

    public void setMaLHP(String maLHP) {
        this.MaLHP = maLHP;
    }

    public String getNgayTao() {
        return NgayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.NgayTao = ngayTao;
    }

    public String getThoiGianHetHan() {
        return ThoiGianHetHan;
    }

    public void setThoiGianHetHan(String thoiGianHetHan) {
        this.ThoiGianHetHan = thoiGianHetHan;
    }

    public String getMaQR() {
        return MaQR;
    }

    public void setMaQR(String maQR) {
        this.MaQR = maQR;
    }

    public double getViDoGV() {
        return ViDoGV;
    }

    public void setViDoGV(double viDoGV) {
        this.ViDoGV = viDoGV;
    }

    public double getKinhDoGV() {
        return KinhDoGV;
    }

    public void setKinhDoGV(double kinhDoGV) {
        this.KinhDoGV = kinhDoGV;
    }

    public boolean isTrangThaiMo() {
        return TrangThaiMo;
    }

    public void setTrangThaiMo(boolean trangThaiMo) {
        this.TrangThaiMo = trangThaiMo;
    }
}
