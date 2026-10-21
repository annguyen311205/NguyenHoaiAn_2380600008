import 'dart:async';
import 'package:flutter/material.dart';
import '../../models/phien_diem_danh.dart';
import '../../models/chi_tiet_diem_danh.dart';
import '../../services/mock_data_repository.dart';
import '../../widgets/qr_display_widget.dart';

class TaoPhienQRScreen extends StatefulWidget {
  final PhienDiemDanh phien;
  const TaoPhienQRScreen({super.key, required this.phien});

  @override
  State<TaoPhienQRScreen> createState() => _TaoPhienQRScreenState();
}

class _TaoPhienQRScreenState extends State<TaoPhienQRScreen> {
  late String _currentQRToken;
  late int _secondsLeft;
  Timer? _timer;
  List<ChiTietDiemDanh> _attendees = [];

  @override
  void initState() {
    super.initState();
    _updateToken();
    _refreshAttendees();
    _timer = Timer.periodic(const Duration(seconds: 1), (t) {
      if (!mounted) return;
      setState(() {
        _secondsLeft = MockDataRepository().getSecondsRemainingInToken();
        if (_secondsLeft == 15 || _secondsLeft <= 1) {
          _updateToken();
        }
      });
      _refreshAttendees();
    });
  }

  void _updateToken() {
    _currentQRToken = MockDataRepository().getDynamicQRToken(widget.phien.MaPhien);
    _secondsLeft = MockDataRepository().getSecondsRemainingInToken();
  }

  void _refreshAttendees() {
    final list = MockDataRepository().getChiTietByPhien(widget.phien.MaPhien);
    if (list.length != _attendees.length) {
      setState(() {
        _attendees = list;
      });
    }
  }

  void _toggleSession() {
    final newStatus = !widget.phien.TrangThaiMo;
    MockDataRepository().updatePhienStatus(widget.phien.MaPhien, newStatus);
    setState(() {
      widget.phien.TrangThaiMo = newStatus;
    });
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(newStatus ? 'Đã mở lại phiên điểm danh' : 'Đã đóng phiên điểm danh!'),
        backgroundColor: newStatus ? Colors.green : Colors.red,
      ),
    );
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: Text('Điểm Danh QR: ${widget.phien.MaPhien}'),
        backgroundColor: const Color(0xFF1E3A8A),
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          children: [
            // Status & Timer card
            Card(
              elevation: 2,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Row(
                          children: [
                            Container(
                              width: 12,
                              height: 12,
                              decoration: BoxDecoration(
                                shape: BoxShape.circle,
                                color: widget.phien.TrangThaiMo ? Colors.green : Colors.red,
                              ),
                            ),
                            const SizedBox(width: 8),
                            Text(
                              widget.phien.TrangThaiMo ? 'PHIÊN ĐANG MỞ' : 'PHIÊN ĐÃ ĐÓNG',
                              style: TextStyle(
                                fontWeight: FontWeight.bold,
                                color: widget.phien.TrangThaiMo ? Colors.green : Colors.red,
                              ),
                            ),
                          ],
                        ),
                        ElevatedButton(
                          onPressed: _toggleSession,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: widget.phien.TrangThaiMo ? Colors.red.shade700 : Colors.green.shade700,
                            foregroundColor: Colors.white,
                            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                          ),
                          child: Text(widget.phien.TrangThaiMo ? 'Đóng Phiên' : 'Mở Phiên'),
                        ),
                      ],
                    ),
                    const Divider(height: 24),

                    // Rotating QR Widget
                    if (widget.phien.TrangThaiMo) ...[
                      QrDisplayWidget(
                        data: _currentQRToken,
                        size: 230,
                      ),
                      const SizedBox(height: 14),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const Icon(Icons.timer_outlined, color: Color(0xFF1E3A8A), size: 20),
                          const SizedBox(width: 6),
                          Text(
                            'Tự động xoay mã sau: $_secondsLeft giây',
                            style: const TextStyle(
                              fontSize: 15,
                              fontWeight: FontWeight.bold,
                              color: Color(0xFF1E3A8A),
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 6),
                      Text(
                        'Token: $_currentQRToken',
                        style: TextStyle(fontSize: 11, color: Colors.grey.shade600, fontFamily: 'monospace'),
                      ),
                    ] else
                      Container(
                        padding: const EdgeInsets.all(32),
                        child: Column(
                          children: const [
                            Icon(Icons.lock_clock, size: 64, color: Colors.grey),
                            SizedBox(height: 12),
                            Text(
                              'Phiên điểm danh hiện đang đóng.\nBấm "Mở Phiên" để tiếp tục tạo mã QR.',
                              textAlign: TextAlign.center,
                              style: TextStyle(color: Colors.grey),
                            ),
                          ],
                        ),
                      ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),

            // Anti-cheat & GPS Info
            Container(
              padding: const EdgeInsets.all(14),
              decoration: BoxDecoration(
                color: Colors.blue.shade50,
                borderRadius: BorderRadius.circular(14),
                border: Border.all(color: Colors.blue.shade200),
              ),
              child: Row(
                children: [
                  const Icon(Icons.shield_outlined, color: Color(0xFF1E3A8A), size: 28),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          'Cơ chế chống gian lận đa tầng:',
                          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: Color(0xFF1E3A8A)),
                        ),
                        const SizedBox(height: 2),
                        Text(
                          '• Mã QR tự động đổi mỗi 15s (chống chụp ảnh gửi ngoài phòng)\n• Vị trí GPS GV: ${widget.phien.ViDoGV}, ${widget.phien.KinhDoGV} (bán kính < 50m)\n• Khóa nhận diện mã máy DeviceID sinh viên',
                          style: const TextStyle(fontSize: 12, color: Colors.black87),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // Attendees List Section
            Card(
              elevation: 2,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text(
                          'Sinh Viên Đã Điểm Danh',
                          style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Color(0xFF1E3A8A)),
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                          decoration: BoxDecoration(
                            color: Colors.green.shade100,
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Text(
                            '${_attendees.where((a) => a.TrangThai == 'CoMat').length} SV',
                            style: TextStyle(fontWeight: FontWeight.bold, color: Colors.green.shade800),
                          ),
                        ),
                      ],
                    ),
                    const Divider(height: 20),
                    if (_attendees.isEmpty)
                      const Padding(
                        padding: EdgeInsets.symmetric(vertical: 24),
                        child: Center(
                          child: Text('Chưa có sinh viên nào điểm danh.', style: TextStyle(color: Colors.grey)),
                        ),
                      )
                    else
                      ListView.separated(
                        shrinkWrap: true,
                        physics: const NeverScrollableScrollPhysics(),
                        itemCount: _attendees.length,
                        separatorBuilder: (_, __) => const Divider(height: 1),
                        itemBuilder: (context, idx) {
                          final att = _attendees[idx];
                          return ListTile(
                            contentPadding: EdgeInsets.zero,
                            leading: CircleAvatar(
                              backgroundColor: att.TrangThai == 'CoMat' ? Colors.green.shade50 : Colors.red.shade50,
                              child: Icon(
                                att.TrangThai == 'CoMat' ? Icons.check : Icons.close,
                                color: att.TrangThai == 'CoMat' ? Colors.green : Colors.red,
                              ),
                            ),
                            title: Text(att.TenSV, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                            subtitle: Text(
                              'MSSV: ${att.MaSV} • Cách GV: ${att.KhoangCachMet.toStringAsFixed(1)}m\nThiết bị: ${att.MaThietBiSV}',
                              style: const TextStyle(fontSize: 12),
                            ),
                            trailing: Text(
                              att.ThoiGianDiemDanh,
                              style: const TextStyle(fontSize: 11, color: Colors.grey),
                            ),
                          );
                        },
                      ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
