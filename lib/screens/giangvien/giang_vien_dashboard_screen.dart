import 'package:flutter/material.dart';
import '../../models/lop_hoc_phan.dart';
import '../../models/phien_diem_danh.dart';
import '../../models/don_xin_phep.dart';
import '../../services/mock_data_repository.dart';
import '../../services/session_manager.dart';
import '../auth/login_screen.dart';
import 'tao_phien_qr_screen.dart';

class GiangVienDashboardScreen extends StatefulWidget {
  const GiangVienDashboardScreen({super.key});

  @override
  State<GiangVienDashboardScreen> createState() => _GiangVienDashboardScreenState();
}

class _GiangVienDashboardScreenState extends State<GiangVienDashboardScreen> {
  int _currentTabIndex = 0;
  List<LopHocPhan> _myClasses = [];
  List<DonXinPhep> _donList = [];

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  void _loadData() {
    final user = SessionManager().currentUser;
    final allLhp = MockDataRepository().getAllLopHocPhan();
    setState(() {
      _myClasses = allLhp.where((l) => l.MaGV == (user?.MaND ?? 'gv01')).toList();
      if (_myClasses.isEmpty) _myClasses = allLhp; // Fallback to all classes if test
      _donList = MockDataRepository().getAllDonXinPhep();
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

  void _showCreatePhienDialog(LopHocPhan lhp) {
    final maPhienController = TextEditingController(
      text: 'PH_${lhp.MaLHP}_${DateTime.now().day}_${DateTime.now().month}',
    );

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        title: const Text('Mở Phiên Điểm Danh Mới', style: TextStyle(color: Color(0xFF1E3A8A), fontWeight: FontWeight.bold)),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Lớp: ${lhp.TenLHP}', style: const TextStyle(fontWeight: FontWeight.w600)),
            const SizedBox(height: 12),
            TextField(
              controller: maPhienController,
              decoration: const InputDecoration(labelText: 'Mã Phiên Điểm Danh (MaPhien)'),
            ),
            const SizedBox(height: 12),
            Container(
              padding: const EdgeInsets.all(10),
              decoration: BoxDecoration(
                color: Colors.blue.shade50,
                borderRadius: BorderRadius.circular(8),
              ),
              child: const Text(
                '📍 Tọa độ GPS phòng học: 10.762622, 106.660172\n(Sinh viên cần quét trong bán kính 50m)',
                style: TextStyle(fontSize: 12),
              ),
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('HỦY')),
          ElevatedButton(
            onPressed: () {
              if (maPhienController.text.trim().isEmpty) return;
              final newPhien = PhienDiemDanh(
                MaPhien: maPhienController.text.trim(),
                MaLHP: lhp.MaLHP,
                NgayTao: DateTime.now().toString().substring(0, 16),
                ThoiGianHetHan: DateTime.now().add(const Duration(minutes: 90)).toString().substring(0, 16),
                MaQR: 'QR_${maPhienController.text.trim()}',
                ViDoGV: 10.762622,
                KinhDoGV: 106.660172,
                TrangThaiMo: true,
              );
              MockDataRepository().createPhien(newPhien);
              Navigator.pop(context);
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => TaoPhienQRScreen(phien: newPhien)),
              );
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF1E3A8A),
              foregroundColor: Colors.white,
            ),
            child: const Text('BẮT ĐẦU ĐIỂM DANH'),
          ),
        ],
      ),
    );
  }

  void _openClassSessions(LopHocPhan lhp) {
    final phiens = MockDataRepository().getPhienByLHP(lhp.MaLHP);
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (context) {
        return Container(
          padding: const EdgeInsets.all(20),
          constraints: BoxConstraints(maxHeight: MediaQuery.of(context).size.height * 0.7),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    child: Text(
                      'Phiên Điểm Danh: ${lhp.TenLHP}',
                      style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Color(0xFF1E3A8A)),
                    ),
                  ),
                  IconButton(icon: const Icon(Icons.close), onPressed: () => Navigator.pop(context)),
                ],
              ),
              const Divider(),
              ElevatedButton.icon(
                onPressed: () {
                  Navigator.pop(context);
                  _showCreatePhienDialog(lhp);
                },
                icon: const Icon(Icons.qr_code_2),
                label: const Text('Tạo Phiên Điểm Danh Mới'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF1E3A8A),
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                ),
              ),
              const SizedBox(height: 12),
              Expanded(
                child: phiens.isEmpty
                    ? const Center(child: Text('Chưa có phiên điểm danh nào cho lớp này.'))
                    : ListView.builder(
                        itemCount: phiens.length,
                        itemBuilder: (context, idx) {
                          final p = phiens[idx];
                          return Card(
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                            child: ListTile(
                              leading: CircleAvatar(
                                backgroundColor: p.TrangThaiMo ? Colors.green.shade100 : Colors.grey.shade200,
                                child: Icon(
                                  Icons.qr_code,
                                  color: p.TrangThaiMo ? Colors.green : Colors.grey,
                                ),
                              ),
                              title: Text(p.MaPhien, style: const TextStyle(fontWeight: FontWeight.bold)),
                              subtitle: Text('Tạo lúc: ${p.NgayTao}\nTrạng thái: ${p.TrangThaiMo ? "Đang mở" : "Đã đóng"}'),
                              trailing: ElevatedButton(
                                onPressed: () {
                                  Navigator.pop(context);
                                  Navigator.push(
                                    context,
                                    MaterialPageRoute(builder: (context) => TaoPhienQRScreen(phien: p)),
                                  );
                                },
                                style: ElevatedButton.styleFrom(
                                  backgroundColor: const Color(0xFF1E3A8A),
                                  foregroundColor: Colors.white,
                                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                                ),
                                child: const Text('Vào QR'),
                              ),
                            ),
                          );
                        },
                      ),
              ),
            ],
          ),
        );
      },
    );
  }

  void _handleDuyetDon(DonXinPhep don, String status) {
    MockDataRepository().duyetDonXinPhep(don.MaDon, status);
    _loadData();
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(status == 'DaDuyet' ? 'Đã duyệt đơn nghỉ phép!' : 'Đã từ chối đơn!'),
        backgroundColor: status == 'DaDuyet' ? Colors.green : Colors.red,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final user = SessionManager().currentUser;

    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Giảng Viên', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            Text(user?.HoTen ?? 'ThS. Nguyễn Văn A', style: const TextStyle(fontSize: 12, color: Colors.white70)),
          ],
        ),
        backgroundColor: const Color(0xFF1E3A8A),
        foregroundColor: Colors.white,
        actions: [
          IconButton(icon: const Icon(Icons.logout), onPressed: _handleLogout, tooltip: 'Đăng xuất'),
        ],
      ),
      body: _currentTabIndex == 0 ? _buildClassListTab() : _buildDonXinPhepTab(),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentTabIndex,
        selectedItemColor: const Color(0xFF1E3A8A),
        onTap: (idx) => setState(() => _currentTabIndex = idx),
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.class_outlined), label: 'Lớp Học Phần'),
          BottomNavigationBarItem(icon: Icon(Icons.assignment_outlined), label: 'Duyệt Đơn Nghỉ'),
        ],
      ),
    );
  }

  Widget _buildClassListTab() {
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: _myClasses.length,
      itemBuilder: (context, idx) {
        final lhp = _myClasses[idx];
        return Card(
          margin: const EdgeInsets.only(bottom: 12),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          elevation: 2,
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                      decoration: BoxDecoration(
                        color: const Color(0xFF1E3A8A).withOpacity(0.1),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Text(
                        lhp.MaLHP,
                        style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF1E3A8A)),
                      ),
                    ),
                    const Spacer(),
                    Text('Sĩ số: ${lhp.SiSo}', style: const TextStyle(color: Colors.grey, fontWeight: FontWeight.bold)),
                  ],
                ),
                const SizedBox(height: 8),
                Text(lhp.TenLHP, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                const SizedBox(height: 4),
                Text('Môn học: ${lhp.TenMonHoc}', style: TextStyle(color: Colors.grey.shade700, fontSize: 13)),
                Text('Phòng: ${lhp.PhongHoc}', style: TextStyle(color: Colors.grey.shade700, fontSize: 13)),
                const Divider(height: 20),
                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    OutlinedButton.icon(
                      onPressed: () => _openClassSessions(lhp),
                      icon: const Icon(Icons.list_alt, size: 18),
                      label: const Text('Xem Các Phiên'),
                      style: OutlinedButton.styleFrom(
                        foregroundColor: const Color(0xFF1E3A8A),
                        side: const BorderSide(color: Color(0xFF1E3A8A)),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                      ),
                    ),
                    const SizedBox(width: 8),
                    ElevatedButton.icon(
                      onPressed: () => _showCreatePhienDialog(lhp),
                      icon: const Icon(Icons.qr_code_scanner, size: 18),
                      label: const Text('Mở Điểm Danh'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF1E3A8A),
                        foregroundColor: Colors.white,
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildDonXinPhepTab() {
    if (_donList.isEmpty) {
      return const Center(child: Text('Không có đơn xin phép nào cần duyệt.'));
    }

    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: _donList.length,
      itemBuilder: (context, idx) {
        final don = _donList[idx];
        Color statusColor = Colors.orange;
        String statusText = 'Chờ duyệt';
        if (don.TrangThaiDuyet == 'DaDuyet') {
          statusColor = Colors.green;
          statusText = 'Đã duyệt';
        } else if (don.TrangThaiDuyet == 'TuChoi') {
          statusColor = Colors.red;
          statusText = 'Từ chối';
        }

        return Card(
          margin: const EdgeInsets.only(bottom: 12),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          elevation: 2,
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      '${don.TenSV} (${don.MaSV})',
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                      decoration: BoxDecoration(
                        color: statusColor.withOpacity(0.15),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Text(
                        statusText,
                        style: TextStyle(color: statusColor, fontWeight: FontWeight.bold, fontSize: 12),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 6),
                Text('Lớp: ${don.TenLHP} (${don.MaLHP})', style: const TextStyle(fontSize: 13)),
                Text('Ngày nghỉ: ${don.NgayNghi}', style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w500)),
                const SizedBox(height: 4),
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(10),
                  decoration: BoxDecoration(
                    color: Colors.grey.shade100,
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text('Lý do: ${don.LyDo}', style: const TextStyle(fontSize: 13)),
                ),
                if (don.TrangThaiDuyet == 'ChoDuyet') ...[
                  const Divider(height: 20),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      OutlinedButton(
                        onPressed: () => _handleDuyetDon(don, 'TuChoi'),
                        style: OutlinedButton.styleFrom(
                          foregroundColor: Colors.red,
                          side: const BorderSide(color: Colors.red),
                        ),
                        child: const Text('Từ Chối'),
                      ),
                      const SizedBox(width: 10),
                      ElevatedButton(
                        onPressed: () => _handleDuyetDon(don, 'DaDuyet'),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.green,
                          foregroundColor: Colors.white,
                        ),
                        child: const Text('Duyệt Đơn'),
                      ),
                    ],
                  ),
                ],
              ],
            ),
          ),
        );
      },
    );
  }
}
