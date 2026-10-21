import 'package:flutter/material.dart';
import '../../models/lop_hoc_phan.dart';
import '../../services/mock_data_repository.dart';
import '../../services/session_manager.dart';
import '../auth/login_screen.dart';

class AdminMainScreen extends StatefulWidget {
  const AdminMainScreen({super.key});

  @override
  State<AdminMainScreen> createState() => _AdminMainScreenState();
}

class _AdminMainScreenState extends State<AdminMainScreen> {
  final _searchController = TextEditingController();
  List<LopHocPhan> _filteredList = [];

  @override
  void initState() {
    super.initState();
    _refreshList();
  }

  void _refreshList() {
    setState(() {
      _filteredList = MockDataRepository().searchLopHocPhan(_searchController.text);
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

  void _showAddEditDialog([LopHocPhan? existing]) {
    final isEdit = existing != null;
    final maLHPController = TextEditingController(text: existing?.MaLHP ?? '');
    final tenLHPController = TextEditingController(text: existing?.TenLHP ?? '');
    final tenMonHocController = TextEditingController(text: existing?.TenMonHoc ?? '');
    final maGVController = TextEditingController(text: existing?.MaGV ?? 'gv01');
    final tenGVController = TextEditingController(text: existing?.TenGV ?? 'ThS. Nguyễn Văn A');
    final phongHocController = TextEditingController(text: existing?.PhongHoc ?? 'A4.02');
    final soBuoiVangController = TextEditingController(text: existing != null ? existing.SoBuoiVangToiDa.toString() : '3');
    final siSoController = TextEditingController(text: existing != null ? existing.SiSo.toString() : '45');

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          title: Text(
            isEdit ? 'Chỉnh Sửa Lớp Học Phần' : 'Thêm Mới Lớp Học Phần',
            style: const TextStyle(color: Color(0xFF1E3A8A), fontWeight: FontWeight.bold),
          ),
          content: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextField(
                  controller: maLHPController,
                  enabled: !isEdit,
                  decoration: const InputDecoration(labelText: 'Mã Lớp Học Phần (MaLHP)'),
                ),
                TextField(
                  controller: tenLHPController,
                  decoration: const InputDecoration(labelText: 'Tên Lớp Học Phần (TenLHP)'),
                ),
                TextField(
                  controller: tenMonHocController,
                  decoration: const InputDecoration(labelText: 'Tên Môn Học (TenMonHoc)'),
                ),
                TextField(
                  controller: tenGVController,
                  decoration: const InputDecoration(labelText: 'Giảng Viên Phụ Trách (TenGV)'),
                ),
                TextField(
                  controller: phongHocController,
                  decoration: const InputDecoration(labelText: 'Phòng Học (PhongHoc)'),
                ),
                TextField(
                  controller: soBuoiVangController,
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(labelText: 'Số buổi vắng tối đa'),
                ),
                TextField(
                  controller: siSoController,
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(labelText: 'Sĩ Số Lớp'),
                ),
              ],
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('HỦY'),
            ),
            ElevatedButton(
              onPressed: () {
                if (maLHPController.text.trim().isEmpty || tenLHPController.text.trim().isEmpty) {
                  return;
                }
                final lhp = LopHocPhan(
                  MaLHP: maLHPController.text.trim(),
                  TenLHP: tenLHPController.text.trim(),
                  TenMonHoc: tenMonHocController.text.trim(),
                  MaGV: maGVController.text.trim(),
                  TenGV: tenGVController.text.trim(),
                  PhongHoc: phongHocController.text.trim(),
                  SoBuoiVangToiDa: int.tryParse(soBuoiVangController.text.trim()) ?? 3,
                  SiSo: int.tryParse(siSoController.text.trim()) ?? 45,
                );

                if (isEdit) {
                  MockDataRepository().updateLopHocPhan(lhp);
                } else {
                  MockDataRepository().addLopHocPhan(lhp);
                }
                Navigator.pop(context);
                _refreshList();
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF1E3A8A),
                foregroundColor: Colors.white,
              ),
              child: Text(isEdit ? 'LƯU' : 'THÊM'),
            ),
          ],
        );
      },
    );
  }

  void _confirmDelete(LopHocPhan lhp) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Xác Nhận Xóa'),
        content: Text('Bạn có chắc chắn muốn xóa lớp học phần "${lhp.TenLHP}" (${lhp.MaLHP}) không?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('HỦY')),
          ElevatedButton(
            onPressed: () {
              MockDataRepository().deleteLopHocPhan(lhp.MaLHP);
              Navigator.pop(context);
              _refreshList();
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Đã xóa lớp học phần thành công!'), backgroundColor: Colors.red),
              );
            },
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red, foregroundColor: Colors.white),
            child: const Text('XÓA'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final stats = MockDataRepository().getAdminStats();

    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text('Quản Trị Viên (Admin)'),
        backgroundColor: const Color(0xFF1E3A8A),
        foregroundColor: Colors.white,
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            tooltip: 'Đăng xuất',
            onPressed: _handleLogout,
          ),
        ],
      ),
      body: Column(
        children: [
          // Stat Summary Cards
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            color: const Color(0xFF1E3A8A),
            child: Row(
              children: [
                _buildStatBox('Lớp HP', '${stats['tongLHP']}', Icons.class_outlined),
                const SizedBox(width: 8),
                _buildStatBox('Giảng Viên', '${stats['tongGiangVien']}', Icons.person_pin_outlined),
                const SizedBox(width: 8),
                _buildStatBox('Sinh Viên', '${stats['tongSinhVien']}', Icons.school_outlined),
              ],
            ),
          ),

          // Search Bar
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: TextField(
              controller: _searchController,
              onChanged: (_) => _refreshList(),
              decoration: InputDecoration(
                hintText: 'Tìm kiếm lớp học phần (Tên, Mã, Phòng)...',
                prefixIcon: const Icon(Icons.search, color: Color(0xFF1E3A8A)),
                suffixIcon: _searchController.text.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear),
                        onPressed: () {
                          _searchController.clear();
                          _refreshList();
                        },
                      )
                    : null,
                filled: true,
                fillColor: Colors.white,
                contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(16),
                  borderSide: BorderSide.none,
                ),
              ),
            ),
          ),

          // List of LopHocPhan
          Expanded(
            child: _filteredList.isEmpty
                ? const Center(
                    child: Text('Không tìm thấy lớp học phần nào.', style: TextStyle(color: Colors.grey)),
                  )
                : ListView.builder(
                    padding: const EdgeInsets.symmetric(horizontal: 16),
                    itemCount: _filteredList.length,
                    itemBuilder: (context, index) {
                      final item = _filteredList[index];
                      return Card(
                        margin: const EdgeInsets.only(bottom: 12),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                        elevation: 1.5,
                        child: InkWell(
                          borderRadius: BorderRadius.circular(16),
                          onTap: () => _showAddEditDialog(item),
                          child: Padding(
                            padding: const EdgeInsets.all(16.0),
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
                                        item.MaLHP,
                                        style: const TextStyle(
                                          color: Color(0xFF1E3A8A),
                                          fontWeight: FontWeight.bold,
                                          fontSize: 12,
                                        ),
                                      ),
                                    ),
                                    const Spacer(),
                                    IconButton(
                                      icon: const Icon(Icons.edit_outlined, color: Colors.blue, size: 20),
                                      onPressed: () => _showAddEditDialog(item),
                                      tooltip: 'Chỉnh sửa',
                                    ),
                                    IconButton(
                                      icon: const Icon(Icons.delete_outline, color: Colors.red, size: 20),
                                      onPressed: () => _confirmDelete(item),
                                      tooltip: 'Xóa lớp',
                                    ),
                                  ],
                                ),
                                const SizedBox(height: 6),
                                Text(
                                  item.TenLHP,
                                  style: const TextStyle(
                                    fontSize: 16,
                                    fontWeight: FontWeight.bold,
                                    color: Color(0xFF0F172A),
                                  ),
                                ),
                                const SizedBox(height: 4),
                                Text(
                                  'Môn: ${item.TenMonHoc}',
                                  style: TextStyle(color: Colors.grey.shade700, fontSize: 13),
                                ),
                                const Divider(height: 18),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  children: [
                                    Row(
                                      children: [
                                        const Icon(Icons.meeting_room_outlined, size: 16, color: Colors.grey),
                                        const SizedBox(width: 4),
                                        Text(item.PhongHoc, style: const TextStyle(fontSize: 12)),
                                      ],
                                    ),
                                    Row(
                                      children: [
                                        const Icon(Icons.people_outline, size: 16, color: Colors.grey),
                                        const SizedBox(width: 4),
                                        Text('Sĩ số: ${item.SiSo}', style: const TextStyle(fontSize: 12)),
                                      ],
                                    ),
                                    Row(
                                      children: [
                                        const Icon(Icons.warning_amber_rounded, size: 16, color: Colors.orange),
                                        const SizedBox(width: 4),
                                        Text('Vắng tối đa: ${item.SoBuoiVangToiDa}', style: const TextStyle(fontSize: 12)),
                                      ],
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                        ),
                      );
                    },
                  ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => _showAddEditDialog(),
        backgroundColor: const Color(0xFF1E3A8A),
        foregroundColor: Colors.white,
        icon: const Icon(Icons.add),
        label: const Text('Thêm Lớp Mới'),
      ),
    );
  }

  Widget _buildStatBox(String label, String value, IconData icon) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 8),
        decoration: BoxDecoration(
          color: Colors.white.withOpacity(0.12),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          children: [
            Icon(icon, color: Colors.white, size: 20),
            const SizedBox(height: 4),
            Text(
              value,
              style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
            ),
            Text(
              label,
              style: TextStyle(color: Colors.white.withOpacity(0.8), fontSize: 11),
            ),
          ],
        ),
      ),
    );
  }
}
