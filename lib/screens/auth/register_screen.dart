import 'package:flutter/material.dart';
import '../../models/nguoi_dung.dart';
import '../../services/mock_data_repository.dart';
import '../../services/session_manager.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _maNDController = TextEditingController();
  final _tenDangNhapController = TextEditingController();
  final _matKhauController = TextEditingController();
  final _hoTenController = TextEditingController();
  final _emailController = TextEditingController();
  String _vaiTro = 'SINHVIEN';
  late String _maThietBi;

  String _errorMessage = '';

  @override
  void initState() {
    super.initState();
    _maThietBi = SessionManager().currentDeviceId;
  }

  void _handleRegister() {
    final maND = _maNDController.text.trim();
    final tenDangNhap = _tenDangNhapController.text.trim();
    final matKhau = _matKhauController.text.trim();
    final hoTen = _hoTenController.text.trim();
    final email = _emailController.text.trim();

    if (maND.isEmpty || tenDangNhap.isEmpty || matKhau.isEmpty || hoTen.isEmpty || email.isEmpty) {
      setState(() => _errorMessage = 'Vui lòng điền đầy đủ các thông tin bắt buộc!');
      return;
    }

    final newUser = NguoiDung(
      MaND: maND,
      TenDangNhap: tenDangNhap,
      MatKhau: matKhau,
      HoTen: hoTen,
      Email: email,
      VaiTro: _vaiTro,
      MaThietBi: _maThietBi,
    );

    final success = MockDataRepository().register(newUser);
    if (success) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Đăng ký tài khoản thành công! Vui lòng đăng nhập.'),
          backgroundColor: Colors.green,
        ),
      );
      Navigator.pop(context);
    } else {
      setState(() => _errorMessage = 'Tên đăng nhập đã tồn tại trên hệ thống!');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Đăng Ký Tài Khoản'),
        backgroundColor: const Color(0xFF1E3A8A),
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Card(
              elevation: 2,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
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
                        ),
                      ),

                    // MaND
                    TextField(
                      controller: _maNDController,
                      decoration: InputDecoration(
                        labelText: 'Mã người dùng / MSSV (*)',
                        hintText: 'Ví dụ: 2380600008',
                        prefixIcon: const Icon(Icons.badge_outlined, color: Color(0xFF1E3A8A)),
                        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                      ),
                    ),
                    const SizedBox(height: 14),

                    // HoTen
                    TextField(
                      controller: _hoTenController,
                      decoration: InputDecoration(
                        labelText: 'Họ và tên (*)',
                        hintText: 'Ví dụ: Nguyễn Hoài An',
                        prefixIcon: const Icon(Icons.person_outline, color: Color(0xFF1E3A8A)),
                        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                      ),
                    ),
                    const SizedBox(height: 14),

                    // Email
                    TextField(
                      controller: _emailController,
                      keyboardType: TextInputType.emailAddress,
                      decoration: InputDecoration(
                        labelText: 'Email (*)',
                        hintText: 'student@school.edu.vn',
                        prefixIcon: const Icon(Icons.email_outlined, color: Color(0xFF1E3A8A)),
                        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                      ),
                    ),
                    const SizedBox(height: 14),

                    // TenDangNhap
                    TextField(
                      controller: _tenDangNhapController,
                      decoration: InputDecoration(
                        labelText: 'Tên đăng nhập (*)',
                        prefixIcon: const Icon(Icons.account_circle_outlined, color: Color(0xFF1E3A8A)),
                        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                      ),
                    ),
                    const SizedBox(height: 14),

                    // MatKhau
                    TextField(
                      controller: _matKhauController,
                      obscureText: true,
                      decoration: InputDecoration(
                        labelText: 'Mật khẩu (*)',
                        prefixIcon: const Icon(Icons.lock_outline, color: Color(0xFF1E3A8A)),
                        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                      ),
                    ),
                    const SizedBox(height: 14),

                    // VaiTro
                    DropdownButtonFormField<String>(
                      value: _vaiTro,
                      decoration: InputDecoration(
                        labelText: 'Vai trò (*)',
                        prefixIcon: const Icon(Icons.security, color: Color(0xFF1E3A8A)),
                        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                      ),
                      items: const [
                        DropdownMenuItem(value: 'SINHVIEN', child: Text('Sinh Viên')),
                        DropdownMenuItem(value: 'GIANGVIEN', child: Text('Giảng Viên')),
                      ],
                      onChanged: (val) {
                        if (val != null) setState(() => _vaiTro = val);
                      },
                    ),
                    const SizedBox(height: 14),

                    // Device ID Lock Info
                    Container(
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: Colors.blue.shade50,
                        borderRadius: BorderRadius.circular(12),
                        border: Border.all(color: Colors.blue.shade200),
                      ),
                      child: Row(
                        children: [
                          const Icon(Icons.phonelink_lock, color: Color(0xFF1E3A8A), size: 22),
                          const SizedBox(width: 10),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                const Text(
                                  'Mã thiết bị liên kết tự động:',
                                  style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12),
                                ),
                                Text(
                                  _maThietBi,
                                  style: const TextStyle(fontSize: 12, color: Colors.black87),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 20),

                    // Submit Button
                    ElevatedButton(
                      onPressed: _handleRegister,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF1E3A8A),
                        foregroundColor: Colors.white,
                        padding: const EdgeInsets.symmetric(vertical: 14),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                      ),
                      child: const Text(
                        'HOÀN TẤT ĐĂNG KÝ',
                        style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold),
                      ),
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
