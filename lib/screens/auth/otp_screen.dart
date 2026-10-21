import 'dart:async';
import 'package:flutter/material.dart';
import '../../models/nguoi_dung.dart';
import '../../services/session_manager.dart';
import '../admin/admin_main_screen.dart';
import '../giangvien/giang_vien_dashboard_screen.dart';
import '../sinhvien/sinh_vien_main_screen.dart';

class OtpScreen extends StatefulWidget {
  final NguoiDung user;
  const OtpScreen({super.key, required this.user});

  @override
  State<OtpScreen> createState() => _OtpScreenState();
}

class _OtpScreenState extends State<OtpScreen> {
  final _otpController = TextEditingController(text: '123456');
  int _countdown = 60;
  Timer? _timer;
  String _errorMessage = '';

  @override
  void initState() {
    super.initState();
    _startTimer();
    // Simulate incoming OTP notification
    WidgetsBinding.instance.addPostFrameCallback((_) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('🔔 [Mã OTP Thử Nghiệm]: 123456 (hiệu lực 60s)'),
          backgroundColor: Color(0xFF1E3A8A),
          duration: Duration(seconds: 5),
        ),
      );
    });
  }

  void _startTimer() {
    _timer?.cancel();
    setState(() => _countdown = 60);
    _timer = Timer.periodic(const Duration(seconds: 1), (t) {
      if (_countdown > 0) {
        setState(() => _countdown--);
      } else {
        t.cancel();
      }
    });
  }

  void _verifyOtp() {
    final code = _otpController.text.trim();
    if (code.length == 6) {
      SessionManager().setCurrentUser(widget.user);

      Widget destination;
      switch (widget.user.VaiTro) {
        case 'ADMIN':
          destination = const AdminMainScreen();
          break;
        case 'GIANGVIEN':
          destination = const GiangVienDashboardScreen();
          break;
        case 'SINHVIEN':
        default:
          destination = const SinhVienMainScreen();
          break;
      }

      Navigator.pushAndRemoveUntil(
        context,
        MaterialPageRoute(builder: (context) => destination),
        (route) => false,
      );
    } else {
      setState(() => _errorMessage = 'Mã OTP không chính xác! Vui lòng nhập 123456.');
    }
  }

  @override
  void dispose() {
    _timer?.cancel();
    _otpController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Xác Thực 2 Lớp (OTP)'),
        backgroundColor: const Color(0xFF1E3A8A),
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const SizedBox(height: 20),
            Center(
              child: Container(
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  color: const Color(0xFF1E3A8A).withOpacity(0.1),
                  shape: BoxShape.circle,
                ),
                child: const Icon(
                  Icons.mark_email_read_outlined,
                  size: 64,
                  color: Color(0xFF1E3A8A),
                ),
              ),
            ),
            const SizedBox(height: 24),
            Text(
              'Xác thực tài khoản',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: const Color(0xFF1E3A8A),
                  ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 8),
            Text(
              'Hệ thống đã gửi mã OTP 6 chữ số đến email:\n${widget.user.Email}',
              style: const TextStyle(color: Colors.black54, fontSize: 14),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 24),

            if (_errorMessage.isNotEmpty)
              Container(
                padding: const EdgeInsets.all(12),
                margin: const EdgeInsets.only(bottom: 16),
                decoration: BoxDecoration(
                  color: Colors.red.shade50,
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: Colors.red.shade300),
                ),
                child: Text(
                  _errorMessage,
                  style: const TextStyle(color: Colors.red, fontSize: 13),
                  textAlign: TextAlign.center,
                ),
              ),

            // OTP Input Field
            TextField(
              controller: _otpController,
              keyboardType: TextInputType.number,
              textAlign: TextAlign.center,
              maxLength: 6,
              style: const TextStyle(fontSize: 28, letterSpacing: 12, fontWeight: FontWeight.bold),
              decoration: InputDecoration(
                counterText: '',
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(16)),
                filled: true,
                fillColor: Colors.grey.shade50,
              ),
            ),
            const SizedBox(height: 16),

            // Countdown
            Text(
              _countdown > 0
                  ? 'Mã có hiệu lực trong: $_countdown giây'
                  : 'Mã đã hết hạn, vui lòng gửi lại!',
              style: TextStyle(
                color: _countdown > 0 ? const Color(0xFF1E3A8A) : Colors.red,
                fontWeight: FontWeight.w600,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 24),

            // Verify Button
            ElevatedButton(
              onPressed: _verifyOtp,
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF1E3A8A),
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 14),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
              child: const Text(
                'XÁC NHẬN & ĐĂNG NHẬP',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
            ),
            const SizedBox(height: 12),

            TextButton(
              onPressed: _countdown == 0 ? _startTimer : null,
              child: const Text('Gửi lại mã OTP'),
            ),
          ],
        ),
      ),
    );
  }
}
