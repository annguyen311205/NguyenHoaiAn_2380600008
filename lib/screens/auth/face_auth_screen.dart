import 'dart:async';
import 'package:flutter/material.dart';
import '../../services/mock_data_repository.dart';
import '../../services/session_manager.dart';
import '../sinhvien/sinh_vien_main_screen.dart';

class FaceAuthScreen extends StatefulWidget {
  const FaceAuthScreen({super.key});

  @override
  State<FaceAuthScreen> createState() => _FaceAuthScreenState();
}

class _FaceAuthScreenState extends State<FaceAuthScreen> with SingleTickerProviderStateMixin {
  late AnimationController _animController;
  int _step = 0; // 0: detecting, 1: blink check, 2: matched
  Timer? _stepTimer;

  @override
  void initState() {
    super.initState();
    _animController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    )..repeat(reverse: true);

    _startSimulatedRecognition();
  }

  void _startSimulatedRecognition() {
    // Step 0: Face alignment
    _stepTimer = Timer(const Duration(milliseconds: 1400), () {
      if (!mounted) return;
      setState(() => _step = 1);

      // Step 1: Liveness check (Eye blink)
      _stepTimer = Timer(const Duration(milliseconds: 1800), () {
        if (!mounted) return;
        setState(() => _step = 2);

        // Step 2: Recognized student 2380600008
        _stepTimer = Timer(const Duration(milliseconds: 1200), () {
          if (!mounted) return;
          final user = MockDataRepository().findUserByStudentId('2380600008');
          if (user != null) {
            SessionManager().setCurrentUser(user);
            Navigator.pushAndRemoveUntil(
              context,
              MaterialPageRoute(builder: (context) => const SinhVienMainScreen()),
              (route) => false,
            );
          }
        });
      });
    });
  }

  @override
  void dispose() {
    _stepTimer?.cancel();
    _animController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        title: const Text('Nhận Diện Khuôn Mặt (ML Kit)'),
        backgroundColor: Colors.black,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: SafeArea(
        child: Column(
          children: [
            const SizedBox(height: 20),
            Text(
              _step == 0
                  ? 'Đang căn chỉnh khuôn mặt...'
                  : _step == 1
                      ? 'Vui lòng chớp mắt (Liveness Detection)...'
                      : '✅ Nhận diện thành công: Nguyễn Hoài An!',
              style: TextStyle(
                color: _step == 2 ? Colors.greenAccent : Colors.white,
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 10),
            Text(
              'Thuật toán AI kiểm tra mắt mở: L > 0.85, R > 0.88',
              style: TextStyle(color: Colors.white.withOpacity(0.6), fontSize: 13),
            ),
            const Spacer(),

            // Oval Camera Frame & Scanning Radar
            Center(
              child: Stack(
                alignment: Alignment.center,
                children: [
                  // Simulated Camera Feed (Dark gradient container with face silhouette)
                  Container(
                    width: 260,
                    height: 340,
                    decoration: BoxDecoration(
                      gradient: RadialGradient(
                        colors: [Colors.grey.shade800, Colors.grey.shade900, Colors.black],
                      ),
                      borderRadius: BorderRadius.circular(130),
                      border: Border.all(
                        color: _step == 2
                            ? Colors.greenAccent
                            : _step == 1
                                ? Colors.amber
                                : const Color(0xFF2563EB),
                        width: 3,
                      ),
                    ),
                    child: Center(
                      child: Icon(
                        _step == 2 ? Icons.check_circle_rounded : Icons.face,
                        size: 140,
                        color: _step == 2 ? Colors.greenAccent : Colors.white.withOpacity(0.7),
                      ),
                    ),
                  ),

                  // Animated Scanning Laser Bar
                  if (_step < 2)
                    AnimatedBuilder(
                      animation: _animController,
                      builder: (context, child) {
                        return Positioned(
                          top: 20 + (_animController.value * 280),
                          child: Container(
                            width: 240,
                            height: 3,
                            decoration: BoxDecoration(
                              color: const Color(0xFF60A5FA),
                              boxShadow: [
                                BoxShadow(
                                  color: Colors.blueAccent.withOpacity(0.8),
                                  blurRadius: 10,
                                  spreadRadius: 2,
                                ),
                              ],
                            ),
                          ),
                        );
                      },
                    ),
                ],
              ),
            ),

            const Spacer(),

            // ML Kit Stats Card
            Container(
              margin: const EdgeInsets.all(20),
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.grey.shade900,
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: Colors.grey.shade800),
              ),
              child: Column(
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text('Trạng thái:', style: TextStyle(color: Colors.grey)),
                      Text(
                        _step == 0
                            ? 'Đang quét...'
                            : _step == 1
                                ? 'Phát hiện chớp mắt thật'
                                : 'Đã khớp dữ liệu gốc',
                        style: TextStyle(
                          color: _step == 2 ? Colors.greenAccent : Colors.white,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text('Tài khoản nhận dạng:', style: TextStyle(color: Colors.grey)),
                      const Text(
                        '2380600008 (Nguyễn Hoài An)',
                        style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                      ),
                    ],
                  ),
                ],
              ),
            ),

            // Simulation Trigger Button
            Padding(
              padding: const EdgeInsets.only(bottom: 24, left: 24, right: 24),
              child: ElevatedButton.icon(
                onPressed: () {
                  setState(() => _step = 0);
                  _startSimulatedRecognition();
                },
                icon: const Icon(Icons.refresh),
                label: const Text('Quét Lại Khuôn Mặt'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF1E3A8A),
                  foregroundColor: Colors.white,
                  minimumSize: const Size.fromHeight(48),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
