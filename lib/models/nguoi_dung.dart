class NguoiDung {
  String MaND;
  String TenDangNhap;
  String MatKhau;
  String HoTen;
  String Email;
  String VaiTro; // "ADMIN", "GIANGVIEN", "SINHVIEN"
  String MaThietBi;
  String AnhKhuonMat;

  NguoiDung({
    required this.MaND,
    required this.TenDangNhap,
    required this.MatKhau,
    required this.HoTen,
    required this.Email,
    required this.VaiTro,
    this.MaThietBi = '',
    this.AnhKhuonMat = '',
  });

  factory NguoiDung.fromJson(Map<String, dynamic> json) {
    return NguoiDung(
      MaND: json['MaND'] ?? '',
      TenDangNhap: json['TenDangNhap'] ?? '',
      MatKhau: json['MatKhau'] ?? '',
      HoTen: json['HoTen'] ?? '',
      Email: json['Email'] ?? '',
      VaiTro: json['VaiTro'] ?? 'SINHVIEN',
      MaThietBi: json['MaThietBi'] ?? '',
      AnhKhuonMat: json['AnhKhuonMat'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'MaND': MaND,
      'TenDangNhap': TenDangNhap,
      'MatKhau': MatKhau,
      'HoTen': HoTen,
      'Email': Email,
      'VaiTro': VaiTro,
      'MaThietBi': MaThietBi,
      'AnhKhuonMat': AnhKhuonMat,
    };
  }
}
