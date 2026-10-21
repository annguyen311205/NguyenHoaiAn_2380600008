import 'package:flutter/material.dart';
import '../../services/mock_data_repository.dart';
import '../../services/session_manager.dart';

class QuetQRDiemDanhScreen extends StatefulWidget {
  const QuetQRDiemDanhScreen({super.key});

  @override
  State<QuetQRDiemDanhScreen> createState() => _QuetQRDiemDanhScreenState();
}

class _QuetQRDiemDanhScreenState extends State<QuetQRDiemDanhScreen> with SingleTickerProviderStateMixin {
  late AnimationController _animController;

  // Anti-cheat Simulation Parameters
  bool _biometricPassed = false;
  bool _simulateInClass = true; // In class (< 50m) or Far away (> 50m)
  bool _simulateCorrectDevice = true; // Registered device or stranger device
  final String _selectedPhien = 'PH_LHP001_B2';

  @override
  void initState() {
    super.initState();
    _animController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    )..repeat(reverse: true);

    // Auto-prompt biometric check on start
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _promptBiometricCheck();
    });
  }

  void _promptBiometricCheck() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) {
        return AlertDialog(
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          title: Row(
            children: const [
              Icon(Icons.fingerprint, color: Color(0xFF1E3A8A), size: 28),
              SizedBox(width: 8),
              Text('Xác Thực Sinh Trắc Học', style: TextStyle(fontSize: 16)),
            ],
          ),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: const [
              Text(
                'Lớp bảo mật 1: Vui lòng chạm cảm biến vân tay hoặc xác thực khuôn mặt để mở ống kính quét QR điểm danh.',
                style: TextStyle(fontSize: 13),
              ),
              SizedBox(height: 16),
              Icon(Icons.fingerprint, size: 64, color: Color(0xFF1E3A8A)),
            ],
          ),
          actions: [
            ElevatedButton(
              onPressed: () {
                Navigator.pop(context);
                setState(() => _biometricPassed = true);
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF1E3A8A),
                foregroundColor: Colors.white,
              ),
              child: const Text('XÁC NHẬN VÂN TAY (OK)'),
            ),
          ],
        );
      },
    );
  }

  void _executeScan() {
    final user = SessionManager().currentUser;
    final maSV = user?.MaND ?? '2380600008';

    // 1. Coordinates based on simulation
    // GV coords: 10.762622, 106.660172
    final double viDoSV = _simulateInClass ? 10.762635 : 10.765500;
    final double kinhDoSV = _simulateInClass ? 106.660180 : 106.664000;

    // 2. Device ID
    final String deviceId = _simulateCorrectDevice
        ? (user?.MaThietBi.isNotEmpty == true ? user!.MaThietBi : 'DEVICE_PIXEL7_PRO_AN')
        : 'DEVICE_UNAUTHORIZED_PROXY';

    // 3. Dynamic QR Token
    final qrToken = MockDataRepository().getDynamicQRToken(_selectedPhien);

    // Call Anti-cheat Attendance Engine
    final result = MockDataRepository().diemDanhSinhVien(
      maPhien: _selectedPhien,
      qrCode: qrToken,
      maSV: maSV,
      viDoSV: viDoSV,
      kinhDoSV: kinhDoSV,
      maThietBiSV: deviceId,
    );

    final bool success = result['success'] == true;
    final String message = result['message'] ?? '';

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          title: Row(
            children: [
              Icon(
                success ? Icons.check_circle_rounded : Icons.gpp_bad_rounded,
                color: success ? Colors.green : Colors.red,
                size: 28,
              ),
              const SizedBox(width: 8),
              Expanded(
                child: Text(
                  success ? 'ĐIỂM DANH THÀNH CÔNG' : 'CẢNH BÁO GIAN LẬN',
                  style: TextStyle(
                    color: success ? Colors.green.shade800 : Colors.red.shade800,
                    fontWeight: FontWeight.bold,
                    fontSize: 16,
                  ),
                ),
              ),
            ],
          ),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(message, style: const TextStyle(fontSize: 14)),
              const Divider(height: 24),
              Text('• Sinh viên: ${user?.HoTen ?? "Nguyễn Hoài An"} ($maSV)', style: const TextStyle(fontSize: 12)),
              Text('• Phiên quét: $_selectedPhien', style: const TextStyle(fontSize: 12)),
              Text('• Mã thiết bị: $deviceId', style: const TextStyle(fontSize: 12)),
              Text('• Vị trí GPS: $viDoSV, $kinhDoSV', style: const TextStyle(fontSize: 12)),
            ],
          ),
          actions: [
            ElevatedButton(
              onPressed: () {
                Navigator.pop(context);
                if (success) {
                  Navigator.pop(context, true);
                }
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: success ? Colors.green : Colors.red,
                foregroundColor: Colors.white,
              ),
              child: const Text('ĐÃ HIỂU'),
            ),
          ],
        );
      },
    );
  }

  @override
  void dispose() {
    _animController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        title: const Text('Quét QR Điểm Danh Chống Gian Lận'),
        backgroundColor: Colors.black,
        foregroundColor: Colors.white,
      ),
      body: SafeArea(
        child: Column(
          children: [
            // Scanner Viewfinder Area
            Expanded(
              flex: 5,
              child: Stack(
                alignment: Alignment.center,
                children: [
                  // Viewfinder Frame
                  Container(
                    width: 250,
                    height: 250,
                    decoration: BoxDecoration(
                      border: Border.all(color: const Color(0xFF60A5FA), width: 2.5),
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Center(
                      child: Icon(
                        Icons.qr_code_scanner,
                        size: 90,
                        color: Colors.white.withOpacity(0.25),
                      ),
                    ),
                  ),

                  // Animated Scanning Laser
                  AnimatedBuilder(
                    animation: _animController,
                    builder: (context, child) {
                      return Positioned(
                        top: (MediaQuery.of(context).size.height * 0.1) + (_animController.value * 210),
                        child: Container(
                          width: 230,
                          height: 3,
                          decoration: BoxDecoration(
                            color: Colors.greenAccent,
                            boxShadow: [
                              BoxShadow(
                                color: Colors.greenAccent.withOpacity(0.8),
                                blurRadius: 8,
                                spreadRadius: 2,
                              ),
                            ],
                          ),
                        ),
                      );
                    },
                  ),

                  // Overlay message
                  Positioned(
                    bottom: 20,
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                      decoration: BoxDecoration(
                        color: Colors.black.withOpacity(0.7),
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: Text(
                        _biometricPassed ? 'Hướng camera về phía mã QR của Giảng viên' : 'Đang chờ xác thực sinh trắc học...',
                        style: const TextStyle(color: Colors.white, fontSize: 13),
                      ),
                    ),
                  ),
                ],
              ),
            ),

            // Anti-cheat Control & Testing Panel
            Expanded(
              flex: 6,
              child: Container(
                padding: const EdgeInsets.all(20),
                decoration: const BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
                ),
                child: SingleChildScrollView(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      const Text(
                        'Kiểm Thử Chống Điểm Danh Hộ (3 Lớp)',
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                          color: Color(0xFF1E3A8A),
                        ),
                      ),
                      const SizedBox(height: 12),

                      // GPS Simulation Switch
                      SwitchListTile(
                        dense: true,
                        contentPadding: EdgeInsets.zero,
                        title: const Text('Lớp GPS: Trong phòng học (< 50m)', style: TextStyle(fontWeight: FontWeight.w600)),
                        subtitle: Text(_simulateInClass ? 'Cách giảng viên ~1.8 mét (Hợp lệ)' : 'Cách giảng viên ~410 mét (Vượt quá 50m -> BỊ CHẶN)'),
                        value: _simulateInClass,
                        activeColor: const Color(0xFF1E3A8A),
                        onChanged: (val) => setState(() => _simulateInClass = val),
                      ),

                      // Device ID Simulation Switch
                      SwitchListTile(
                        dense: true,
                        contentPadding: EdgeInsets.zero,
                        title: const Text('Lớp Thiết Bị: Máy chính chủ', style: TextStyle(fontWeight: FontWeight.w600)),
                        subtitle: Text(_simulateCorrectDevice ? 'DEVICE_PIXEL7_PRO_AN (Trùng khớp)' : 'DEVICE_UNKNOWN (Khác máy -> BỊ CHẶN HỘ)'),
                        value: _simulateCorrectDevice,
                        activeColor: const Color(0xFF1E3A8A),
                        onChanged: (val) => setState(() => _simulateCorrectDevice = val),
                      ),
                      const SizedBox(height: 16),

                      // Execute Scan Button
                      ElevatedButton.icon(
                        onPressed: _biometricPassed ? _executeScan : _promptBiometricCheck,
                        icon: const Icon(Icons.camera_alt),
                        label: Text(
                          _biometricPassed ? 'BẤM ĐỂ QUÉT MÃ QR' : 'MỞ KHÓA VÂN TAY TRƯỚC',
                          style: const TextStyle(fontSize: 15, fontWeight: FontWeight.bold),
                        ),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF1E3A8A),
                          foregroundColor: Colors.white,
                          padding: const EdgeInsets.symmetric(vertical: 14),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
