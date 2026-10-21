package com.nguyenhoaian2380600008.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class NguoiDung implements Serializable {
    @SerializedName("MaND")
    private String MaND;

    @SerializedName("TenDangNhap")
    private String TenDangNhap;

    @SerializedName("MatKhau")
    private String MatKhau;

    @SerializedName("HoTen")
    private String HoTen;

    @SerializedName("Email")
    private String Email;

    @SerializedName("VaiTro")
    private String VaiTro; // "ADMIN", "GIANGVIEN", "SINHVIEN"

    @SerializedName("MaThietBi")
    private String MaThietBi;

    @SerializedName("AnhKhuonMat")
    private String AnhKhuonMat;

    @SerializedName("sinhVienId")
    private int SinhVienId;

    @SerializedName("maSV")
    private String MaSV;

    @SerializedName("token")
    private String Token;

    @SerializedName("demoOtp")
    private String DemoOtp;

    public NguoiDung() {
    }

    public NguoiDung(String maND, String tenDangNhap, String matKhau, String hoTen, String email, String vaiTro, String maThietBi, String anhKhuonMat) {
        this.MaND = maND;
        this.TenDangNhap = tenDangNhap;
        this.MatKhau = matKhau;
        this.HoTen = hoTen;
        this.Email = email;
        this.VaiTro = vaiTro;
        this.MaThietBi = maThietBi;
        this.AnhKhuonMat = anhKhuonMat;
    }

    public int getSinhVienId() {
        return SinhVienId;
    }

    public void setSinhVienId(int sinhVienId) {
        this.SinhVienId = sinhVienId;
    }

    public String getMaSV() {
        return MaSV;
    }

    public void setMaSV(String maSV) {
        this.MaSV = maSV;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        this.Token = token;
    }

    public String getDemoOtp() {
        return DemoOtp;
    }

    public void setDemoOtp(String demoOtp) {
        this.DemoOtp = demoOtp;
    }

    public String getMaND() {
        return MaND;
    }

    public void setMaND(String maND) {
        this.MaND = maND;
    }

    public String getTenDangNhap() {
        return TenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.TenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return MatKhau;
    }

    public void setMatKhau(String matKhau) {
        this.MatKhau = matKhau;
    }

    public String getHoTen() {
        if (HoTen != null && HoTen.contains("Nguy?n H?u Trung")) {
            return HoTen.replace("Nguy?n H?u Trung", "Nguyễn Hữu Trung");
        }
        return HoTen;
    }

    public void setHoTen(String hoTen) {
        this.HoTen = hoTen;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getVaiTro() {
        return VaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.VaiTro = vaiTro;
    }

    public String getMaThietBi() {
        return MaThietBi;
    }

    public void setMaThietBi(String maThietBi) {
        this.MaThietBi = maThietBi;
    }

    public String getAnhKhuonMat() {
        return AnhKhuonMat;
    }

    public void setAnhKhuonMat(String anhKhuonMat) {
        this.AnhKhuonMat = anhKhuonMat;
    }
}
