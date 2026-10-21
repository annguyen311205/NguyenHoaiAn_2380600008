package com.nguyenhoaian2380600008.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class DonXinPhep implements Serializable {
    @SerializedName("MaDon")
    private String MaDon;

    @SerializedName("MaSV")
    private String MaSV;

    @SerializedName("TenSV")
    private String TenSV;

    @SerializedName("MaLHP")
    private String MaLHP;

    @SerializedName("TenLHP")
    private String TenLHP;

    @SerializedName("NgayNghi")
    private String NgayNghi;

    @SerializedName("LyDo")
    private String LyDo;

    @SerializedName("TrangThaiDuyet")
    private String TrangThaiDuyet; // "ChoDuyet", "DaDuyet", "TuChoi"

    @SerializedName("id")
    private int id;

    @SerializedName("sinhVienId")
    private int sinhVienId;

    @SerializedName("lopHocPhanId")
    private int lopHocPhanId;

    @SerializedName("minhChungUrl")
    private String minhChungUrl = "";

    @SerializedName("phanHoiGiaoVien")
    private String phanHoiGiaoVien = "";

    public DonXinPhep() {
    }

    public DonXinPhep(String maDon, String maSV, String tenSV, String maLHP, String tenLHP, String ngayNghi, String lyDo, String trangThaiDuyet) {
        this.MaDon = maDon;
        this.MaSV = maSV;
        this.TenSV = tenSV;
        this.MaLHP = maLHP;
        this.TenLHP = tenLHP;
        this.NgayNghi = ngayNghi;
        this.LyDo = lyDo;
        this.TrangThaiDuyet = trangThaiDuyet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSinhVienId() {
        return sinhVienId;
    }

    public void setSinhVienId(int sinhVienId) {
        this.sinhVienId = sinhVienId;
    }

    public int getLopHocPhanId() {
        return lopHocPhanId;
    }

    public void setLopHocPhanId(int lopHocPhanId) {
        this.lopHocPhanId = lopHocPhanId;
    }

    public String getMinhChungUrl() {
        return minhChungUrl;
    }

    public void setMinhChungUrl(String minhChungUrl) {
        this.minhChungUrl = minhChungUrl;
    }

    public String getPhanHoiGiaoVien() {
        return phanHoiGiaoVien;
    }

    public void setPhanHoiGiaoVien(String phanHoiGiaoVien) {
        this.phanHoiGiaoVien = phanHoiGiaoVien;
    }

    public String getMaDon() {
        return MaDon;
    }

    public void setMaDon(String maDon) {
        this.MaDon = maDon;
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

    public String getMaLHP() {
        return MaLHP;
    }

    public void setMaLHP(String maLHP) {
        this.MaLHP = maLHP;
    }

    public String getTenLHP() {
        return TenLHP;
    }

    public void setTenLHP(String tenLHP) {
        this.TenLHP = tenLHP;
    }

    public String getNgayNghi() {
        return NgayNghi;
    }

    public void setNgayNghi(String ngayNghi) {
        this.NgayNghi = ngayNghi;
    }

    public String getLyDo() {
        return LyDo;
    }

    public void setLyDo(String lyDo) {
        this.LyDo = lyDo;
    }

    public String getTrangThaiDuyet() {
        return TrangThaiDuyet;
    }

    public void setTrangThaiDuyet(String trangThaiDuyet) {
        this.TrangThaiDuyet = trangThaiDuyet;
    }
}
