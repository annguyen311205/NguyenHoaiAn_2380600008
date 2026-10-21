class PhienDiemDanh {
  String MaPhien;
  String MaLHP;
  String NgayTao;
  String ThoiGianHetHan;
  String MaQR;
  double ViDoGV;
  double KinhDoGV;
  bool TrangThaiMo;

  PhienDiemDanh({
    required this.MaPhien,
    required this.MaLHP,
    required this.NgayTao,
    required this.ThoiGianHetHan,
    required this.MaQR,
    required this.ViDoGV,
    required this.KinhDoGV,
    required this.TrangThaiMo,
  });

  factory PhienDiemDanh.fromJson(Map<String, dynamic> json) {
    return PhienDiemDanh(
      MaPhien: json['MaPhien'] ?? '',
      MaLHP: json['MaLHP'] ?? '',
      NgayTao: json['NgayTao'] ?? '',
      ThoiGianHetHan: json['ThoiGianHetHan'] ?? '',
      MaQR: json['MaQR'] ?? '',
      ViDoGV: (json['ViDoGV'] is num) ? (json['ViDoGV'] as num).toDouble() : double.tryParse(json['ViDoGV']?.toString() ?? '10.762622') ?? 10.762622,
      KinhDoGV: (json['KinhDoGV'] is num) ? (json['KinhDoGV'] as num).toDouble() : double.tryParse(json['KinhDoGV']?.toString() ?? '106.660172') ?? 106.660172,
      TrangThaiMo: json['TrangThaiMo'] ?? true,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'MaPhien': MaPhien,
      'MaLHP': MaLHP,
      'NgayTao': NgayTao,
      'ThoiGianHetHan': ThoiGianHetHan,
      'MaQR': MaQR,
      'ViDoGV': ViDoGV,
      'KinhDoGV': KinhDoGV,
      'TrangThaiMo': TrangThaiMo,
    };
  }
}
