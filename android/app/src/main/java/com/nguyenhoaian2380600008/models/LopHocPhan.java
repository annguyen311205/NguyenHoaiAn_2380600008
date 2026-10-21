package com.nguyenhoaian2380600008.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class LopHocPhan implements Serializable {
    @SerializedName("MaLHP")
    private String MaLHP;

    @SerializedName("TenLHP")
    private String TenLHP;

    @SerializedName("TenMonHoc")
    private String TenMonHoc;

    @SerializedName("MaGV")
    private String MaGV;

    @SerializedName("TenGV")
    private String TenGV;

    @SerializedName("PhongHoc")
    private String PhongHoc;

    @SerializedName("SoBuoiVangToiDa")
    private int SoBuoiVangToiDa;

    @SerializedName("SiSo")
    private int SiSo;

    @SerializedName("id")
    private int id;

    @SerializedName("monHocId")
    private int monHocId = 1;

    @SerializedName("maMon")
    private String maMon = "LTDD01";

    @SerializedName("soTinChi")
    private int soTinChi = 3;

    @SerializedName("hocKy")
    private String hocKy = "HK1";

    @SerializedName("namHoc")
    private String namHoc = "2026-2027";

    @SerializedName("soBuoi")
    private int soBuoi = 15;

    public LopHocPhan() {
    }

    public LopHocPhan(String maLHP, String tenLHP, String tenMonHoc, String maGV, String tenGV, String phongHoc, int soBuoiVangToiDa, int siSo) {
        this.MaLHP = maLHP;
        this.TenLHP = tenLHP;
        this.TenMonHoc = tenMonHoc;
        this.MaGV = maGV;
        this.TenGV = tenGV;
        this.PhongHoc = phongHoc;
        this.SoBuoiVangToiDa = soBuoiVangToiDa;
        this.SiSo = siSo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMonHocId() {
        return monHocId;
    }

    public void setMonHocId(int monHocId) {
        this.monHocId = monHocId;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
    }

    public String getHocKy() {
        return hocKy;
    }

    public void setHocKy(String hocKy) {
        this.hocKy = hocKy;
    }

    public String getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(String namHoc) {
        this.namHoc = namHoc;
    }

    public int getSoBuoi() {
        return soBuoi;
    }

    public void setSoBuoi(int soBuoi) {
        this.soBuoi = soBuoi;
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

    public String getTenMonHoc() {
        return TenMonHoc;
    }

    public void setTenMonHoc(String tenMonHoc) {
        this.TenMonHoc = tenMonHoc;
    }

    public String getMaGV() {
        return MaGV;
    }

    public void setMaGV(String maGV) {
        this.MaGV = maGV;
    }

    public String getTenGV() {
        return TenGV;
    }

    public void setTenGV(String tenGV) {
        this.TenGV = tenGV;
    }

    public String getPhongHoc() {
        return PhongHoc;
    }

    public void setPhongHoc(String phongHoc) {
        this.PhongHoc = phongHoc;
    }

    public int getSoBuoiVangToiDa() {
        return SoBuoiVangToiDa;
    }

    public void setSoBuoiVangToiDa(int soBuoiVangToiDa) {
        this.SoBuoiVangToiDa = soBuoiVangToiDa;
    }

    public int getSiSo() {
        return SiSo;
    }

    public void setSiSo(int siSo) {
        this.SiSo = siSo;
    }
}
