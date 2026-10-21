import 'package:flutter/material.dart';
import '../../models/lop_hoc_phan.dart';
import '../../services/mock_data_repository.dart';
import '../../services/session_manager.dart';
import '../auth/login_screen.dart';
import 'don_xin_phep_screen.dart';
import 'quet_qr_diem_danh_screen.dart';

class SinhVienMainScreen extends StatefulWidget {
  const SinhVienMainScreen({super.key});

  @override
  State<SinhVienMainScreen> createState() => _SinhVienMainScreenState();
}

class _SinhVienMainScreenState extends State<SinhVienMainScreen> {
  List<LopHocPhan> _classes = [];
  Map<String, Map<String, dynamic>> _statsMap = {};

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  void _loadData() {
    final user = SessionManager().currentUser;
    final maSV = user?.MaND ?? '2380600008';
    final allLhp = MockDataRepository().getAllLopHocPhan();

    final stats = <String, Map<String, dynamic>>{};
    for (var l in allLhp) {
      stats[l.MaLHP] = MockDataRepository().getThongKeVangSinhVien(maSV, l.MaLHP);
    }

    setState(() {
      _classes = allLhp;
      _statsMap = stats;
    });
  }

  void _handleLogout() {
    SessionManager().logout();
    Navigator.pushAndRemoveUntil(
      context,
      MaterialPageRoute(builder: (context) => const LoginScreen()),
      (route) => false,
    );
  }

  @override
  Widget build(BuildContext context) {
    final user = SessionManager().currentUser;
    final maSV = user?.MaND ?? '2380600008';
    final hoTen = user?.HoTen ?? 'Nguyễn Hoài An';
    final maThietBi = SessionManager().currentDeviceId;

    // Check if any class has warning
    LopHocPhan? warnedClass;
    Map<String, dynamic>? warningStat;
    for (var l in _classes) {
      final st = _statsMap[l.MaLHP];
      if (st != null && st['isCanhBao'] == true) {
        warnedClass = l;
        warningStat = st;
        break;
      }
    }

    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Cổng Sinh Viên', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            Text('$maSV - $hoTen', style: const TextStyle(fontSize: 12, color: Colors.white70)),
          ],
        ),
        backgroundColor: const Color(0xFF1E3A8A),
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.assignment_outlined),
            tooltip: 'Đơn xin phép',
            onPressed: () {
              Navigator.push(context, MaterialPageRoute(builder: (context) => const DonXinPhepScreen())).then((_) => _loadData());
            },
          ),
          IconButton(
            icon: const Icon(Icons.logout),
            tooltip: 'Đăng xuất',
            onPressed: _handleLogout,
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Student Device Info Card
            Card(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              elevation: 1.5,
              child: Padding(
                padding: const EdgeInsets.all(14.0),
                child: Row(
                  children: [
                    CircleAvatar(
                      radius: 24,
                      backgroundColor: const Color(0xFF1E3A8A).withOpacity(0.1),
                      child: const Icon(Icons.person, color: Color(0xFF1E3A8A), size: 28),
                    ),
                    const SizedBox(width: 14),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(hoTen, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                          Text('MSSV: $maSV', style: TextStyle(color: Colors.grey.shade700, fontSize: 13)),
                          const SizedBox(height: 2),
                          Row(
                            children: [
                              const Icon(Icons.phonelink_lock, size: 14, color: Colors.green),
                              const SizedBox(width: 4),
                              Expanded(
                                child: Text(
                                  'Mã máy: $maThietBi',
                                  style: const TextStyle(fontSize: 11, color: Colors.green, fontWeight: FontWeight.bold),
                                  overflow: TextOverflow.ellipsis,
                                ),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 14),

            // RED WARNING CARD (Absence >= 2/3 maximum)
            if (warnedClass != null && warningStat != null)
              Container(
                margin: const EdgeInsets.only(bottom: 16),
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: const Color(0xFFFEF2F2),
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: const Color(0xFFEF4444), width: 1.5),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.red.withOpacity(0.12),
                      blurRadius: 10,
                      offset: const Offset(0, 4),
                    ),
                  ],
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: const [
                        Icon(Icons.warning_rounded, color: Color(0xFFDC2626), size: 26),
                        SizedBox(width: 8),
                        Text(
                          'CẢNH BÁO NGUY CƠ CẤM THI!',
                          style: TextStyle(
                            color: Color(0xFFDC2626),
                            fontWeight: FontWeight.bold,
                            fontSize: 15,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'Học phần: ${warnedClass.TenLHP} (${warnedClass.MaLHP})\nBạn đã vắng: ${warningStat['soBuoiVang']} / ${warningStat['soBuoiVangToiDa']} buổi vắng tối đa.',
                      style: const TextStyle(color: Color(0xFF991B1B), fontSize: 13, height: 1.3),
                    ),
                    const SizedBox(height: 10),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: [
                        OutlinedButton(
                          onPressed: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(builder: (context) => const DonXinPhepScreen()),
                            ).then((_) => _loadData());
                          },
                          style: OutlinedButton.styleFrom(
                            foregroundColor: const Color(0xFFDC2626),
                            side: const BorderSide(color: Color(0xFFDC2626)),
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                          ),
                          child: const Text('Gửi Đơn Xin Phép Ngay'),
                        ),
                      ],
                    ),
                  ],
                ),
              ),

            // Section Title
            const Text(
              'Tiến Độ Điểm Danh Từng Lớp Học Phần',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Color(0xFF1E3A8A)),
            ),
            const SizedBox(height: 10),

            // Class list with progress bars
            ..._classes.map((lhp) {
              final stat = _statsMap[lhp.MaLHP] ?? {
                'soBuoiVang': 0,
                'soBuoiVangToiDa': lhp.SoBuoiVangToiDa,
                'soBuoiCoMat': 0,
                'tiLePhanTram': 0.0,
                'isCanhBao': false,
              };
              final int v = stat['soBuoiVang'];
              final int maxV = stat['soBuoiVangToiDa'];
              final double ratio = (v / (maxV > 0 ? maxV : 1)).clamp(0.0, 1.0);

              return Card(
                margin: const EdgeInsets.only(bottom: 12),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                elevation: 1.5,
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Expanded(
                            child: Text(
                              lhp.TenLHP,
                              style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                            ),
                          ),
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                            decoration: BoxDecoration(
                              color: v >= 2 ? Colors.red.shade100 : Colors.green.shade100,
                              borderRadius: BorderRadius.circular(8),
                            ),
                            child: Text(
                              'Vắng: $v / $maxV buổi',
                              style: TextStyle(
                                fontSize: 12,
                                fontWeight: FontWeight.bold,
                                color: v >= 2 ? Colors.red.shade800 : Colors.green.shade800,
                              ),
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 4),
                      Text('GV: ${lhp.TenGV} • Phòng: ${lhp.PhongHoc}', style: TextStyle(color: Colors.grey.shade600, fontSize: 12)),
                      const SizedBox(height: 12),
                      ClipRRect(
                        borderRadius: BorderRadius.circular(6),
                        child: LinearProgressIndicator(
                          value: ratio,
                          minHeight: 8,
                          backgroundColor: Colors.grey.shade200,
                          valueColor: AlwaysStoppedAnimation<Color>(
                            ratio >= 0.66 ? Colors.red : (ratio > 0 ? Colors.orange : Colors.green),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              );
            }),
          ],
        ),
      ),
      bottomNavigationBar: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          boxShadow: [
            BoxShadow(color: Colors.black.withOpacity(0.06), blurRadius: 10, offset: const Offset(0, -3)),
          ],
        ),
        child: ElevatedButton.icon(
          onPressed: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const QuetQRDiemDanhScreen()),
            ).then((_) => _loadData());
          },
          icon: const Icon(Icons.qr_code_scanner, size: 22),
          label: const Text(
            'QUÉT QR ĐIỂM DANH (CHỐNG GIAN LẬN)',
            style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold),
          ),
          style: ElevatedButton.styleFrom(
            backgroundColor: const Color(0xFF1E3A8A),
            foregroundColor: Colors.white,
            padding: const EdgeInsets.symmetric(vertical: 14),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          ),
        ),
      ),
    );
  }
}
