package com.nguyenhoaian2380600008.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // === 1. AUTHENTICATE (/api/Authenticate) ===
    @POST("Authenticate/login")
    Call<JsonObject> login(@Body Map<String, Object> body);

    @POST("Authenticate/request-otp")
    Call<JsonObject> requestOtp(@Body Map<String, Object> body);

    @POST("Authenticate/verify-otp")
    Call<JsonObject> verifyOtp(@Body Map<String, Object> body);

    @POST("Authenticate/register")
    Call<JsonObject> register(@Body Map<String, Object> body);

    // === 2. MÔN HỌC (/api/MonHoc) ===
    @GET("MonHoc")
    Call<JsonArray> getMonHocs();

    @GET("MonHoc/{id}")
    Call<JsonObject> getMonHocById(@Path("id") int id);

    @POST("MonHoc")
    Call<JsonObject> createMonHoc(@Body Map<String, Object> body);

    @PUT("MonHoc/{id}")
    Call<JsonElement> updateMonHoc(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("MonHoc/{id}")
    Call<JsonElement> deleteMonHoc(@Path("id") int id);

    // === 3. LỚP HỌC PHẦN (/api/LopHocPhan) ===
    @GET("LopHocPhan")
    Call<JsonArray> getLopHocPhans();

    @GET("LopHocPhan/{id}")
    Call<JsonObject> getLopHocPhanById(@Path("id") int id);

    @GET("LopHocPhan/{id}/sinh-viens")
    Call<JsonArray> getSinhViensByLopHocPhan(@Path("id") int id);

    @POST("LopHocPhan")
    Call<JsonObject> createLopHocPhan(@Body Map<String, Object> body);

    @PUT("LopHocPhan/{id}")
    Call<JsonElement> updateLopHocPhan(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("LopHocPhan/{id}")
    Call<JsonElement> deleteLopHocPhan(@Path("id") int id);

    @POST("LopHocPhan/{id}/dang-ky-sinh-vien/{sinhVienId}")
    Call<JsonObject> dangKySinhVienVaoLop(@Path("id") int id, @Path("sinhVienId") int sinhVienId);

    // === 4. BUỔI ĐIỂM DANH (/api/BuoiDiemDanh) ===
    @GET("BuoiDiemDanh")
    Call<JsonArray> getBuoiDiemDanhs();

    @GET("BuoiDiemDanh/{id}")
    Call<JsonObject> getBuoiDiemDanhById(@Path("id") int id);

    @GET("BuoiDiemDanh/by-lophocphan/{lopHocPhanId}")
    Call<JsonArray> getBuoiDiemDanhByLopHocPhan(@Path("lopHocPhanId") int lopHocPhanId);

    @POST("BuoiDiemDanh")
    Call<JsonObject> createBuoiDiemDanh(@Body Map<String, Object> body);

    @PUT("BuoiDiemDanh/{id}")
    Call<JsonElement> updateBuoiDiemDanh(@Path("id") int id, @Body Map<String, Object> body);

    @PUT("BuoiDiemDanh/{id}/dong-mo")
    Call<JsonObject> toggleBuoiDiemDanh(@Path("id") int id);

    @DELETE("BuoiDiemDanh/{id}")
    Call<JsonElement> deleteBuoiDiemDanh(@Path("id") int id);

    // === 5. ĐIỂM DANH (/api/DiemDanh) ===
    @GET("DiemDanh")
    Call<JsonArray> getDiemDanhs();

    @GET("DiemDanh/by-buoi/{buoiDiemDanhId}")
    Call<JsonArray> getDiemDanhByBuoi(@Path("buoiDiemDanhId") int buoiDiemDanhId);

    @GET("DiemDanh/by-sinh-vien/{sinhVienId}")
    Call<JsonArray> getDiemDanhBySinhVien(@Path("sinhVienId") int sinhVienId);

    @POST("DiemDanh/check-in")
    Call<JsonObject> checkInDiemDanh(@Body Map<String, Object> body);

    @PUT("DiemDanh/{id}")
    Call<JsonElement> updateDiemDanh(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("DiemDanh/{id}")
    Call<JsonElement> deleteDiemDanh(@Path("id") int id);

    // === 6. ĐƠN NGHỈ PHÉP (/api/DonNghiPhep) ===
    @GET("DonNghiPhep")
    Call<JsonArray> getDonNghiPheps();

    @GET("DonNghiPhep/by-sinh-vien/{sinhVienId}")
    Call<JsonArray> getDonNghiPhepBySinhVien(@Path("sinhVienId") int sinhVienId);

    @POST("DonNghiPhep")
    Call<JsonObject> createDonNghiPhep(@Body Map<String, Object> body);

    @PUT("DonNghiPhep/{id}/duyet")
    Call<JsonObject> duyetDonNghiPhep(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("DonNghiPhep/{id}")
    Call<JsonElement> deleteDonNghiPhep(@Path("id") int id);

    // === 7. SINH VIÊN (/api/SinhVien) ===
    @GET("SinhVien")
    Call<JsonArray> getSinhViens();

    @GET("SinhVien/{id}")
    Call<JsonObject> getSinhVienById(@Path("id") int id);

    @GET("SinhVien/by-masv/{maSv}")
    Call<JsonObject> getSinhVienByMaSv(@Path("maSv") String maSv);

    @GET("SinhVien/by-user/{userId}")
    Call<JsonObject> getSinhVienByUser(@Path("userId") String userId);

    @POST("SinhVien")
    Call<JsonObject> createSinhVien(@Body Map<String, Object> body);

    @PUT("SinhVien/{id}")
    Call<JsonElement> updateSinhVien(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("SinhVien/{id}")
    Call<JsonElement> deleteSinhVien(@Path("id") int id);

    // === 8. THỐNG KÊ (/api/ThongKe) ===
    @GET("ThongKe/tong-quan")
    Call<JsonObject> getThongKeTongQuan();

    @GET("ThongKe/lop-hoc-phan/{lopHocPhanId}")
    Call<JsonObject> getThongKeLopHocPhan(@Path("lopHocPhanId") int lopHocPhanId);

    @GET("ThongKe/sinh-vien/{sinhVienId}")
    Call<JsonObject> getThongKeSinhVien(@Path("sinhVienId") int sinhVienId);

    @GET("ThongKe/canh-bao-cam-thi/{lopHocPhanId}")
    Call<JsonObject> getCanhBaoCamThi(@Path("lopHocPhanId") int lopHocPhanId);
}
