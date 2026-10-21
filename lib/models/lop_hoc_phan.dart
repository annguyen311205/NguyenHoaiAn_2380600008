class LopHocPhan {
  String MaLHP;
  String TenLHP;
  String TenMonHoc;
  String MaGV;
  String TenGV;
  String PhongHoc;
  int SoBuoiVangToiDa;
  int SiSo;

  LopHocPhan({
    required this.MaLHP,
    required this.TenLHP,
    required this.TenMonHoc,
    required this.MaGV,
    required this.TenGV,
    required this.PhongHoc,
    required this.SoBuoiVangToiDa,
    required this.SiSo,
  });

  factory LopHocPhan.fromJson(Map<String, dynamic> json) {
    return LopHocPhan(
      MaLHP: json['MaLHP'] ?? '',
      TenLHP: json['TenLHP'] ?? '',
      TenMonHoc: json['TenMonHoc'] ?? '',
      MaGV: json['MaGV'] ?? '',
      TenGV: json['TenGV'] ?? '',
      PhongHoc: json['PhongHoc'] ?? '',
      SoBuoiVangToiDa: json['SoBuoiVangToiDa'] is int ? json['SoBuoiVangToiDa'] : int.tryParse(json['SoBuoiVangToiDa']?.toString() ?? '3') ?? 3,
      SiSo: json['SiSo'] is int ? json['SiSo'] : int.tryParse(json['SiSo']?.toString() ?? '45') ?? 45,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'MaLHP': MaLHP,
      'TenLHP': TenLHP,
      'TenMonHoc': TenMonHoc,
      'MaGV': MaGV,
      'TenGV': TenGV,
      'PhongHoc': PhongHoc,
      'SoBuoiVangToiDa': SoBuoiVangToiDa,
      'SiSo': SiSo,
    };
  }
}
