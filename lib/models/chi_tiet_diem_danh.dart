class ChiTietDiemDanh {
  String MaCT;
  String MaPhien;
  String MaSV;
  String TenSV;
  String ThoiGianDiemDanh;
  String TrangThai; // "CoMat", "DiTre", "VangCoPhep", "VangKhongPhep"
  double KhoangCachMet;
  String MaThietBiSV;

  ChiTietDiemDanh({
    required this.MaCT,
    required this.MaPhien,
    required this.MaSV,
    required this.TenSV,
    required this.ThoiGianDiemDanh,
    required this.TrangThai,
    required this.KhoangCachMet,
    required this.MaThietBiSV,
  });

  factory ChiTietDiemDanh.fromJson(Map<String, dynamic> json) {
    return ChiTietDiemDanh(
      MaCT: json['MaCT'] ?? '',
      MaPhien: json['MaPhien'] ?? '',
      MaSV: json['MaSV'] ?? '',
      TenSV: json['TenSV'] ?? '',
      ThoiGianDiemDanh: json['ThoiGianDiemDanh'] ?? '',
      TrangThai: json['TrangThai'] ?? 'CoMat',
      KhoangCachMet: (json['KhoangCachMet'] is num) ? (json['KhoangCachMet'] as num).toDouble() : double.tryParse(json['KhoangCachMet']?.toString() ?? '15.0') ?? 15.0,
      MaThietBiSV: json['MaThietBiSV'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'MaCT': MaCT,
      'MaPhien': MaPhien,
      'MaSV': MaSV,
      'TenSV': TenSV,
      'ThoiGianDiemDanh': ThoiGianDiemDanh,
      'TrangThai': TrangThai,
      'KhoangCachMet': KhoangCachMet,
      'MaThietBiSV': MaThietBiSV,
    };
  }
}
