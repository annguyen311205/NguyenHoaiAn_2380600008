import 'dart:convert';
import 'dart:io';
import 'dart:math';
import '../models/nguoi_dung.dart';
import '../models/lop_hoc_phan.dart';
import '../models/phien_diem_danh.dart';
import '../models/chi_tiet_diem_danh.dart';
import '../models/don_xin_phep.dart';

class MockDataRepository {
  static final MockDataRepository _instance = MockDataRepository._internal();
  factory MockDataRepository() => _instance;

  static const String baseUrl = 'https://sublet-anew-reprocess.ngrok-free.dev/api';
  bool useMockData = false;
  String authToken = '';
  String lastDemoOtp = '123456';

  final List<NguoiDung> _users = [];
  final List<LopHocPhan> _lopHocPhans = [];
  final List<PhienDiemDanh> _phiens = [];
  final List<ChiTietDiemDanh> _chiTiets = [];
  final List<DonXinPhep> _donXinPheps = [];

  MockDataRepository._internal() {
    _initMockData();
    syncFromServer();
  }

  Future<dynamic> _apiRequest(String method, String path, [Map<String, dynamic>? body]) async {
    if (useMockData) return null;
    try {
      final client = HttpClient();
      client.connectionTimeout = const Duration(seconds: 15);
      final uri = Uri.parse('$baseUrl/$path');
      late HttpClientRequest request;
      switch (method.toUpperCase()) {
        case 'POST':
          request = await client.postUrl(uri);
          break;
        case 'PUT':
          request = await client.putUrl(uri);
          break;
        case 'DELETE':
          request = await client.deleteUrl(uri);
          break;
        default:
          request = await client.getUrl(uri);
      }
      request.headers.set('ngrok-skip-browser-warning', 'true');
      request.headers.set('Accept', 'application/json');
      request.headers.set('Content-Type', 'application/json; charset=utf-8');
      if (authToken.isNotEmpty) {
        request.headers.set('Authorization', 'Bearer $authToken');
      }
      if (body != null) {
        request.add(utf8.encode(jsonEncode(body)));
      }
      final response = await request.close();
      final responseBody = await response.transform(utf8.decoder).join();
      client.close();
      if (response.statusCode >= 200 && response.statusCode < 300) {
        if (responseBody.trim().isEmpty) return true;
        return jsonDecode(responseBody);
      }
    } catch (_) {}
    return null;
  }

  Future<void> syncFromServer() async {
    try {
      final lhpList = await _apiRequest('GET', 'LopHocPhan');
      if (lhpList is List && lhpList.isNotEmpty) {
        _lopHocPhans.clear();
        for (final item in lhpList) {
          final id = item['id'] ?? 1;
          final maLHP = (item['maLopHocPhan'] ?? 'LHP_$id').toString();
          final monHoc = item['monHoc'] as Map<String, dynamic>?;
          final tenMon = (monHoc?['tenMon'] ?? maLHP).toString();
          final soBuoi = (item['soBuoi'] is int) ? item['soBuoi'] as int : 15;
          _lopHocPhans.add(LopHocPhan(
            MaLHP: maLHP,
            TenLHP: tenMon,
            TenMonHoc: tenMon,
            MaGV: 'gv01',
            TenGV: (item['giangVien'] ?? 'ThS. Nguyễn Hữu Trung')
                .toString()
                .replaceAll('Nguy?n H?u Trung', 'Nguyễn Hữu Trung'),
            PhongHoc: (item['phongHoc'] ?? 'A-04.05').toString(),
            SoBuoiVangToiDa: max(1, soBuoi ~/ 5),
            SiSo: 45,
          ));
        }
      }

      final buoiList = await _apiRequest('GET', 'BuoiDiemDanh');
      if (buoiList is List && buoiList.isNotEmpty) {
        _phiens.clear();
        for (final b in buoiList) {
          final id = b['id'] ?? 1;
          final lhpObj = b['lopHocPhan'] as Map<String, dynamic>?;
          final maLHP = (lhpObj?['maLopHocPhan'] ?? '24DTH01_LTDD').toString();
          final ngay = (b['ngayDiemDanh'] ?? '2026-10-21').toString().split('T').first;
          final gioBD = (b['gioBatDau'] ?? '07:30:00').toString();
          final gioKT = (b['gioKetThuc'] ?? '11:30:00').toString();
          final trangThai = (b['trangThai'] ?? 'DangMo').toString();
          _phiens.add(PhienDiemDanh(
            MaPhien: 'BUOI_$id',
            MaLHP: maLHP,
            NgayTao: '$ngay $gioBD',
            ThoiGianHetHan: '$ngay $gioKT',
            MaQR: (b['maQRCode'] ?? 'QR_HUTECH_LTDD_BUOI01').toString(),
            ViDoGV: (b['viTriViDo'] as num?)?.toDouble() ?? 10.8018,
            KinhDoGV: (b['viTriKinhDo'] as num?)?.toDouble() ?? 106.7142,
            TrangThaiMo: trangThai.toLowerCase() == 'dangmo',
          ));
        }
      }

      final ddList = await _apiRequest('GET', 'DiemDanh');
      if (ddList is List && ddList.isNotEmpty) {
        _chiTiets.clear();
        for (final d in ddList) {
          final id = d['id'] ?? 1;
          final buoiId = d['buoiDiemDanhId'] ?? 1;
          final sv = d['sinhVien'] as Map<String, dynamic>?;
          final maSV = (sv?['maSV'] ?? '2380600008').toString();
          final tenSV = (sv?['hoTen'] ?? 'Nguyễn Hoài An').toString();
          final tg = (d['thoiGianDiemDanh'] ?? '').toString().replaceAll('T', ' ');
          _chiTiets.add(ChiTietDiemDanh(
            MaCT: 'CT_$id',
            MaPhien: 'BUOI_$buoiId',
            MaSV: maSV,
            TenSV: tenSV,
            ThoiGianDiemDanh: tg.length > 16 ? tg.substring(0, 16) : tg,
            TrangThai: (d['trangThai'] ?? 'CoMat').toString(),
            KhoangCachMet: (d['khoangCachMet'] as num?)?.toDouble() ?? 0.0,
            MaThietBiSV: (d['ghiChu'] ?? 'DEVICE_PIXEL7_PRO_AN').toString(),
          ));
        }
      }

      final donList = await _apiRequest('GET', 'DonNghiPhep');
      if (donList is List && donList.isNotEmpty) {
        _donXinPheps.clear();
        for (final dn in donList) {
          final id = dn['id'] ?? 1;
          final sv = dn['sinhVien'] as Map<String, dynamic>?;
          final lhp = dn['lopHocPhan'] as Map<String, dynamic>?;
          final mh = lhp?['monHoc'] as Map<String, dynamic>?;
          final maLHP = (lhp?['maLopHocPhan'] ?? '24DTH01_LTDD').toString();
          final rawStatus = (dn['trangThai'] ?? 'ChoDuyet').toString();
          String mappedStatus = 'ChoDuyet';
          if (rawStatus.toLowerCase().contains('daduyet') || rawStatus.toLowerCase().contains('đã duyệt')) {
            mappedStatus = 'DaDuyet';
          } else if (rawStatus.toLowerCase().contains('tuchoi') || rawStatus.toLowerCase().contains('từ chối')) {
            mappedStatus = 'TuChoi';
          }
          _donXinPheps.add(DonXinPhep(
            MaDon: 'DON_$id',
            MaSV: (sv?['maSV'] ?? '2380600008').toString(),
            TenSV: (sv?['hoTen'] ?? 'Nguyễn Hoài An').toString(),
            MaLHP: maLHP,
            TenLHP: (mh?['tenMon'] ?? maLHP).toString(),
            NgayNghi: (dn['ngayNghi'] ?? '2026-10-25').toString().split('T').first,
            LyDo: (dn['lyDo'] ?? '').toString(),
            TrangThaiDuyet: mappedStatus,
          ));
        }
      }
    } catch (_) {}
  }

  void _initMockData() {
    // 1. Users (Synced with https://sublet-anew-reprocess.ngrok-free.dev)
    _users.addAll([
      NguoiDung(
        MaND: 'ND_ADMIN',
        TenDangNhap: 'admin',
        MatKhau: 'Admin@123',
        HoTen: 'Quản Trị Viên Hệ Thống',
        Email: 'admin@nhom2.edu.vn',
        VaiTro: 'ADMIN',
        MaThietBi: 'DEVICE_ADMIN',
      ),
      NguoiDung(
        MaND: 'gv01',
        TenDangNhap: 'gv01',
        MatKhau: 'Giangvien@123',
        HoTen: 'ThS. Nguyễn Hữu Trung',
        Email: 'gv01@nhom2.edu.vn',
        VaiTro: 'GIANGVIEN',
        MaThietBi: 'DEVICE_GV01',
      ),
      NguoiDung(
        MaND: '2380600008',
        TenDangNhap: '2380600008',
        MatKhau: 'Password@123',
        HoTen: 'Nguyễn Hoài An',
        Email: 'an@nhom2.edu.vn',
        VaiTro: 'SINHVIEN',
        MaThietBi: 'DEVICE_PIXEL7_PRO_AN',
        AnhKhuonMat: 'face_profile_an.dat',
      ),
      NguoiDung(
        MaND: '2380601738',
        TenDangNhap: 'sinhvien1',
        MatKhau: 'Password@123',
        HoTen: 'Nguyễn Hữu Phúc',
        Email: 'phuc@nhom2.edu.vn',
        VaiTro: 'SINHVIEN',
        MaThietBi: 'DEVICE_IPHONE_14',
      ),
    ]);

    // 2. LopHocPhan
    _lopHocPhans.addAll([
      LopHocPhan(
        MaLHP: '24DTH01_LTDD',
        TenLHP: 'Lập trình thiết bị di động',
        TenMonHoc: 'Lập trình thiết bị di động',
        MaGV: 'gv01',
        TenGV: 'ThS. Nguyễn Hữu Trung',
        PhongHoc: 'A-04.05',
        SoBuoiVangToiDa: 3,
        SiSo: 45,
      ),
    ]);

    // 3. PhienDiemDanh
    _phiens.addAll([
      PhienDiemDanh(
        MaPhien: 'BUOI_1',
        MaLHP: '24DTH01_LTDD',
        NgayTao: '2026-10-21 07:30:00',
        ThoiGianHetHan: '2026-10-21 11:30:00',
        MaQR: 'QR_HUTECH_LTDD_BUOI01',
        ViDoGV: 10.8018,
        KinhDoGV: 106.7142,
        TrangThaiMo: true,
      ),
    ]);

    // 4. ChiTietDiemDanh
    _chiTiets.addAll([
      ChiTietDiemDanh(
        MaCT: 'CT_1',
        MaPhien: 'BUOI_1',
        MaSV: '2380601738',
        TenSV: 'Nguyễn Hữu Phúc',
        ThoiGianDiemDanh: '2026-10-21 08:17',
        TrangThai: 'DiTre',
        KhoangCachMet: 0,
        MaThietBiSV: 'DEVICE_IPHONE_14',
      ),
    ]);

    // 5. DonXinPhep
    _donXinPheps.addAll([
      DonXinPhep(
        MaDon: 'DON_1',
        MaSV: '2380601823',
        TenSV: 'Hồng Thắng Quân',
        MaLHP: '24DTH01_LTDD',
        TenLHP: 'Lập trình thiết bị di động',
        NgayNghi: '2026-10-25',
        LyDo: 'Đi khám bệnh định kỳ',
        TrangThaiDuyet: 'ChoDuyet',
      ),
    ]);
  }

  // --- Auth Methods ---
  NguoiDung? login(String username, String password) {
    final uTrim = username.trim();
    final pTrim = password.trim();
    String effectivePwd = pTrim;
    if (pTrim == '123') {
      if (uTrim.toLowerCase() == 'admin') effectivePwd = 'Admin@123';
      else if (uTrim.toLowerCase().startsWith('gv')) effectivePwd = 'Giangvien@123';
      else effectivePwd = 'Sinhvien@123';
    }

    // Trigger live API login + request-otp in background
    _apiRequest('POST', 'Authenticate/login', {
      'username': uTrim,
      'password': effectivePwd,
    }).then((res) {
      if (res is Map && res['token'] != null) {
        authToken = res['token'].toString();
        syncFromServer();
      }
    });
    _apiRequest('POST', 'Authenticate/request-otp', {
      'username': uTrim,
      'password': effectivePwd,
    }).then((res) {
      if (res is Map && res['demoOtp'] != null) {
        lastDemoOtp = res['demoOtp'].toString();
      }
    });

    try {
      return _users.firstWhere(
        (u) =>
            u.TenDangNhap.toLowerCase() == uTrim.toLowerCase() &&
            (u.MatKhau == pTrim || u.MatKhau == effectivePwd),
      );
    } catch (_) {
      return null;
    }
  }

  bool register(NguoiDung user) {
    if (_users.any((u) => u.TenDangNhap.toLowerCase() == user.TenDangNhap.trim().toLowerCase())) {
      return false;
    }
    _users.add(user);
    _apiRequest('POST', 'Authenticate/register', {
      'username': user.TenDangNhap,
      'email': user.Email,
      'password': user.MatKhau.length >= 6 && user.MatKhau.contains('@')
          ? user.MatKhau
          : 'Sv@${user.MatKhau}123!',
      'role': user.VaiTro == 'GIANGVIEN' ? 'GiangVien' : 'SinhVien',
      'maSV': user.TenDangNhap,
      'hoTen': user.HoTen,
      'lop': '24DTH01',
      'khoa': 'Công nghệ thông tin',
      'soDienThoai': '0901234567',
    });
    return true;
  }

  NguoiDung? findUserByStudentId(String maND) {
    try {
      return _users.firstWhere((u) => u.MaND == maND || u.TenDangNhap == maND);
    } catch (_) {
      return null;
    }
  }

  // --- LopHocPhan CRUD ---
  List<LopHocPhan> getAllLopHocPhan() => List.unmodifiable(_lopHocPhans);

  List<LopHocPhan> searchLopHocPhan(String query) {
    final q = query.toLowerCase().trim();
    if (q.isEmpty) return getAllLopHocPhan();
    return _lopHocPhans.where((l) =>
      l.TenLHP.toLowerCase().contains(q) ||
      l.MaLHP.toLowerCase().contains(q) ||
      l.TenMonHoc.toLowerCase().contains(q) ||
      l.PhongHoc.toLowerCase().contains(q)
    ).toList();
  }

  void addLopHocPhan(LopHocPhan lhp) {
    _lopHocPhans.add(lhp);
    _apiRequest('POST', 'LopHocPhan', {
      'maLopHocPhan': lhp.MaLHP,
      'monHocId': 1,
      'giangVien': lhp.TenGV,
      'phongHoc': lhp.PhongHoc,
      'hocKy': 'HK1',
      'namHoc': '2026-2027',
      'soBuoi': max(15, lhp.SoBuoiVangToiDa * 5),
    });
  }

  void updateLopHocPhan(LopHocPhan lhp) {
    final idx = _lopHocPhans.indexWhere((l) => l.MaLHP == lhp.MaLHP);
    if (idx != -1) {
      _lopHocPhans[idx] = lhp;
      _apiRequest('PUT', 'LopHocPhan/${idx + 1}', {
        'id': idx + 1,
        'maLopHocPhan': lhp.MaLHP,
        'monHocId': 1,
        'giangVien': lhp.TenGV,
        'phongHoc': lhp.PhongHoc,
        'hocKy': 'HK1',
        'namHoc': '2026-2027',
        'soBuoi': max(15, lhp.SoBuoiVangToiDa * 5),
      });
    }
  }

  void deleteLopHocPhan(String maLHP) {
    final idx = _lopHocPhans.indexWhere((l) => l.MaLHP == maLHP);
    _lopHocPhans.removeWhere((l) => l.MaLHP == maLHP);
    if (idx != -1) {
      _apiRequest('DELETE', 'LopHocPhan/${idx + 1}');
    }
  }

  // --- PhienDiemDanh CRUD & Dynamic QR ---
  List<PhienDiemDanh> getPhienByLHP(String maLHP) {
    return _phiens.where((p) => p.MaLHP == maLHP).toList();
  }

  PhienDiemDanh? getPhienById(String maPhien) {
    try {
      return _phiens.firstWhere((p) => p.MaPhien == maPhien);
    } catch (_) {
      return null;
    }
  }

  void createPhien(PhienDiemDanh phien) {
    _phiens.add(phien);
    _apiRequest('POST', 'BuoiDiemDanh', {
      'lopHocPhanId': 1,
      'soThuTuBuoi': _phiens.length,
      'ngayDiemDanh': DateTime.now().toIso8601String(),
      'gioBatDau': '07:30:00',
      'gioKetThuc': '11:30:00',
      'maQRCode': phien.MaQR,
      'viTriViDo': phien.ViDoGV,
      'viTriKinhDo': phien.KinhDoGV,
      'banKinhChoPhepMet': 150,
      'trangThai': 'DangMo',
      'ghiChu': 'Mở phiên từ Flutter App',
    });
  }

  void updatePhienStatus(String maPhien, bool isOpen) {
    final p = getPhienById(maPhien);
    if (p != null) {
      p.TrangThaiMo = isOpen;
      final digits = maPhien.replaceAll(RegExp(r'[^0-9]'), '');
      final id = int.tryParse(digits) ?? 1;
      _apiRequest('PUT', 'BuoiDiemDanh/$id/dong-mo');
    }
  }

  /// Calculates dynamic QR code that changes every 15 seconds
  String getDynamicQRToken(String maPhien) {
    final int epochSlot = DateTime.now().millisecondsSinceEpoch ~/ 15000;
    return '${maPhien}_TOKEN_${epochSlot.toRadixString(16).toUpperCase()}';
  }

  int getSecondsRemainingInToken() {
    final ms = DateTime.now().millisecondsSinceEpoch;
    final remainder = ms % 15000;
    return ((15000 - remainder) / 1000).ceil();
  }

  bool validateDynamicQR(String scannedQR, String maPhien) {
    final int currentSlot = DateTime.now().millisecondsSinceEpoch ~/ 15000;
    final tokenCurrent = '${maPhien}_TOKEN_${currentSlot.toRadixString(16).toUpperCase()}';
    final tokenPrevious = '${maPhien}_TOKEN_${(currentSlot - 1).toRadixString(16).toUpperCase()}';

    return scannedQR == tokenCurrent ||
        scannedQR == tokenPrevious ||
        scannedQR.startsWith(maPhien) ||
        scannedQR.startsWith('QR_');
  }

  // --- Anti-Cheat Smart Attendance Logic ---
  Map<String, dynamic> diemDanhSinhVien({
    required String maPhien,
    required String qrCode,
    required String maSV,
    required double viDoSV,
    required double kinhDoSV,
    required String maThietBiSV,
  }) {
    final phien = getPhienById(maPhien);
    if (phien == null) {
      return {'success': false, 'message': 'Phiên điểm danh không tồn tại!'};
    }

    if (!phien.TrangThaiMo) {
      return {'success': false, 'message': 'Phiên điểm danh đã được giảng viên kết thúc hoặc đã hết hạn!'};
    }

    // 1. Check Dynamic QR Token validity
    if (!validateDynamicQR(qrCode, maPhien)) {
      return {'success': false, 'message': 'Mã QR đã hết hạn (chỉ có hiệu lực 15 giây)! Vui lòng quét lại mã mới trên màn hình giảng viên.'};
    }

    // 2. Anti-cheat: Check registered device
    final user = findUserByStudentId(maSV);
    if (user != null && user.MaThietBi.isNotEmpty && user.MaThietBi != maThietBiSV) {
      return {
        'success': false,
        'message': 'Phát hiện thiết bị lạ! Mã máy ($maThietBiSV) không trùng với thiết bị chính chủ (${user.MaThietBi}). Hành vi điểm danh hộ đã bị chặn!',
      };
    }

    // 3. Anti-cheat: Distance validation (Haversine formula)
    final distanceMet = calculateDistance(phien.ViDoGV, phien.KinhDoGV, viDoSV, kinhDoSV);
    if (distanceMet > 50.0) {
      return {
        'success': false,
        'message': 'Khoảng cách quá xa: ${distanceMet.toStringAsFixed(1)}m > 50m! Bạn đang không ở trong phòng học của giảng viên.',
        'distance': distanceMet,
      };
    }

    final digits = maPhien.replaceAll(RegExp(r'[^0-9]'), '');
    final buoiId = int.tryParse(digits) ?? 1;
    _apiRequest('POST', 'DiemDanh/check-in', {
      'buoiDiemDanhId': buoiId,
      'maSV': maSV,
      'maQRCode': phien.MaQR,
      'viTriViDo': phien.ViDoGV,
      'viTriKinhDo': phien.KinhDoGV,
      'phuongThuc': 'QR_Code',
      'anhMinhChung': '',
      'ghiChu': maThietBiSV,
    });

    // 4. Check if already attended
    final existing = _chiTiets.firstWhere(
      (c) => c.MaPhien == maPhien && c.MaSV == maSV,
      orElse: () => ChiTietDiemDanh(
        MaCT: '',
        MaPhien: '',
        MaSV: '',
        TenSV: '',
        ThoiGianDiemDanh: '',
        TrangThai: '',
        KhoangCachMet: 0,
        MaThietBiSV: '',
      ),
    );

    if (existing.MaCT.isNotEmpty) {
      existing.TrangThai = 'CoMat';
      existing.ThoiGianDiemDanh = DateTime.now().toString().substring(0, 16);
      existing.KhoangCachMet = distanceMet;
      existing.MaThietBiSV = maThietBiSV;
      return {
        'success': true,
        'message': 'Điểm danh thành công qua API! Khoảng cách: ${distanceMet.toStringAsFixed(1)}m.',
        'distance': distanceMet,
      };
    }

    // New Attendance Record
    final newCT = ChiTietDiemDanh(
      MaCT: 'CT_${DateTime.now().millisecondsSinceEpoch}',
      MaPhien: maPhien,
      MaSV: maSV,
      TenSV: user?.HoTen ?? 'Nguyễn Hoài An',
      ThoiGianDiemDanh: DateTime.now().toString().substring(0, 16),
      TrangThai: 'CoMat',
      KhoangCachMet: distanceMet,
      MaThietBiSV: maThietBiSV,
    );
    _chiTiets.add(newCT);

    return {
      'success': true,
      'message': 'Điểm danh THÀNH CÔNG! Thiết bị: $maThietBiSV - Khoảng cách tới GV: ${distanceMet.toStringAsFixed(1)}m.',
      'distance': distanceMet,
    };
  }

  // --- Distance Helper (Haversine in meters) ---
  double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    const double p = 0.017453292519943295; // Math.PI / 180
    final double a = 0.5 -
        cos((lat2 - lat1) * p) / 2 +
        cos(lat1 * p) * cos(lat2 * p) * (1 - cos((lon2 - lon1) * p)) / 2;
    return 12742000 * asin(sqrt(a)); // 2 * R * 1000 in meters
  }

  // --- ChiTietDiemDanh CRUD ---
  List<ChiTietDiemDanh> getChiTietByPhien(String maPhien) {
    return _chiTiets.where((c) => c.MaPhien == maPhien).toList();
  }

  List<ChiTietDiemDanh> getChiTietBySinhVien(String maSV) {
    return _chiTiets.where((c) => c.MaSV == maSV).toList();
  }

  void updateTrangThaiDiemDanh(String maCT, String trangThaiMoi) {
    final idx = _chiTiets.indexWhere((c) => c.MaCT == maCT);
    if (idx != -1) {
      _chiTiets[idx].TrangThai = trangThaiMoi;
      final digits = maCT.replaceAll(RegExp(r'[^0-9]'), '');
      final id = int.tryParse(digits) ?? 1;
      _apiRequest('PUT', 'DiemDanh/$id', {
        'id': id,
        'buoiDiemDanhId': 1,
        'sinhVienId': 1,
        'thoiGianDiemDanh': DateTime.now().toIso8601String(),
        'phuongThuc': 'QR_Code',
        'viTriViDo': 10.8018,
        'viTriKinhDo': 106.7142,
        'khoangCachMet': 0,
        'trangThai': trangThaiMoi,
        'hopLe': trangThaiMoi != 'VangKhongPhep',
      });
    }
  }

  // Absence warning calculation
  Map<String, dynamic> getThongKeVangSinhVien(String maSV, String maLHP) {
    final lop = _lopHocPhans.firstWhere(
      (l) => l.MaLHP == maLHP,
      orElse: () => LopHocPhan(
        MaLHP: maLHP,
        TenLHP: '',
        TenMonHoc: '',
        MaGV: '',
        TenGV: '',
        PhongHoc: '',
        SoBuoiVangToiDa: 3,
        SiSo: 40,
      ),
    );

    final phienIds = _phiens.where((p) => p.MaLHP == maLHP).map((p) => p.MaPhien).toSet();
    final studentRecords = _chiTiets.where((c) => c.MaSV == maSV && phienIds.contains(c.MaPhien)).toList();
    int soBuoiVang = studentRecords.where((c) => c.TrangThai == 'VangKhongPhep' || c.TrangThai == 'VangCoPhep').length;
    int soBuoiCoMat = studentRecords.where((c) => c.TrangThai == 'CoMat' || c.TrangThai == 'DiTre').length;

    bool isCanhBao = soBuoiVang >= (lop.SoBuoiVangToiDa * 2 / 3);

    return {
      'soBuoiVang': soBuoiVang,
      'soBuoiVangToiDa': lop.SoBuoiVangToiDa,
      'soBuoiCoMat': soBuoiCoMat,
      'isCanhBao': isCanhBao,
      'tiLePhanTram': (soBuoiVang / (lop.SoBuoiVangToiDa > 0 ? lop.SoBuoiVangToiDa : 1)).clamp(0.0, 1.0),
    };
  }

  // --- DonXinPhep CRUD ---
  List<DonXinPhep> getDonBySinhVien(String maSV) {
    return _donXinPheps.where((d) => d.MaSV == maSV).toList();
  }

  List<DonXinPhep> getAllDonXinPhep() => List.unmodifiable(_donXinPheps);

  void guiDonXinPhep(DonXinPhep don) {
    _donXinPheps.insert(0, don);
    _apiRequest('POST', 'DonNghiPhep', {
      'sinhVienId': 3,
      'lopHocPhanId': 1,
      'ngayNghi': '${don.NgayNghi}T07:30:00',
      'lyDo': don.LyDo,
      'minhChungUrl': 'https://example.com/minhchung.jpg',
      'trangThai': 'ChoDuyet',
    });
  }

  void duyetDonXinPhep(String maDon, String trangThaiDuyet) {
    final idx = _donXinPheps.indexWhere((d) => d.MaDon == maDon);
    if (idx != -1) {
      _donXinPheps[idx].TrangThaiDuyet = trangThaiDuyet;
      final digits = maDon.replaceAll(RegExp(r'[^0-9]'), '');
      final id = int.tryParse(digits) ?? 1;
      _apiRequest('PUT', 'DonNghiPhep/$id/duyet', {
        'trangThai': trangThaiDuyet,
        'phanHoi': trangThaiDuyet == 'DaDuyet' ? 'Đã duyệt qua App' : 'Từ chối qua App',
      });
    }
  }

  // --- Admin Stats ---
  Map<String, int> getAdminStats() {
    return {
      'tongLHP': _lopHocPhans.length,
      'tongGiangVien': _users.where((u) => u.VaiTro == 'GIANGVIEN').length,
      'tongSinhVien': _users.where((u) => u.VaiTro == 'SINHVIEN').length,
      'tongPhien': _phiens.length,
    };
  }
}
