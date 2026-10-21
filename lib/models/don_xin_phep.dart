class DonXinPhep {
  String MaDon;
  String MaSV;
  String TenSV;
  String MaLHP;
  String TenLHP;
  String NgayNghi;
  String LyDo;
  String TrangThaiDuyet; // "ChoDuyet", "DaDuyet", "TuChoi"

  DonXinPhep({
    required this.MaDon,
    required this.MaSV,
    required this.TenSV,
    required this.MaLHP,
    required this.TenLHP,
    required this.NgayNghi,
    required this.LyDo,
    required this.TrangThaiDuyet,
  });

  factory DonXinPhep.fromJson(Map<String, dynamic> json) {
    return DonXinPhep(
      MaDon: json['MaDon'] ?? '',
      MaSV: json['MaSV'] ?? '',
      TenSV: json['TenSV'] ?? '',
      MaLHP: json['MaLHP'] ?? '',
      TenLHP: json['TenLHP'] ?? '',
      NgayNghi: json['NgayNghi'] ?? '',
      LyDo: json['LyDo'] ?? '',
      TrangThaiDuyet: json['TrangThaiDuyet'] ?? 'ChoDuyet',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'MaDon': MaDon,
      'MaSV': MaSV,
      'TenSV': TenSV,
      'MaLHP': MaLHP,
      'TenLHP': TenLHP,
      'NgayNghi': NgayNghi,
      'LyDo': LyDo,
      'TrangThaiDuyet': TrangThaiDuyet,
    };
  }
}
