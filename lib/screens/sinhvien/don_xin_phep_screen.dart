import 'package:flutter/material.dart';
import '../../models/don_xin_phep.dart';
import '../../models/lop_hoc_phan.dart';
import '../../services/mock_data_repository.dart';
import '../../services/session_manager.dart';

class DonXinPhepScreen extends StatefulWidget {
  const DonXinPhepScreen({super.key});

  @override
  State<DonXinPhepScreen> createState() => _DonXinPhepScreenState();
}

class _DonXinPhepScreenState extends State<DonXinPhepScreen> {
  List<DonXinPhep> _myRequests = [];

  @override
  void initState() {
    super.initState();
    _loadRequests();
  }

  void _loadRequests() {
    final user = SessionManager().currentUser;
    final maSV = user?.MaND ?? '2380600008';
    setState(() {
      _myRequests = MockDataRepository().getDonBySinhVien(maSV);
    });
  }

  void _showCreateDialog() {
    final user = SessionManager().currentUser;
    final classes = MockDataRepository().getAllLopHocPhan();
    LopHocPhan selectedClass = classes.isNotEmpty ? classes.first : LopHocPhan(
      MaLHP: 'LHP001',
      TenLHP: 'Lập trình Thiết bị Di động Nâng cao',
      TenMonHoc: 'Mobile App',
      MaGV: 'gv01',
      TenGV: 'ThS. Nguyễn Văn A',
      PhongHoc: 'A4.02',
      SoBuoiVangToiDa: 3,
      SiSo: 45,
    );
    final ngayNghiController = TextEditingController(text: DateTime.now().toString().substring(0, 10));
    final lyDoController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setDialogState) {
            return AlertDialog(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              title: const Text('Tạo Đơn Xin Phép Nghỉ Học', style: TextStyle(color: Color(0xFF1E3A8A), fontWeight: FontWeight.bold)),
              content: SingleChildScrollView(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    DropdownButtonFormField<LopHocPhan>(
                      value: selectedClass,
                      isExpanded: true,
                      decoration: const InputDecoration(labelText: 'Chọn lớp học phần'),
                      items: classes.map((l) {
                        return DropdownMenuItem(
                          value: l,
                          child: Text(l.TenLHP, overflow: TextOverflow.ellipsis),
                        );
                      }).toList(),
                      onChanged: (val) {
                        if (val != null) setDialogState(() => selectedClass = val);
                      },
                    ),
                    const SizedBox(height: 12),
                    TextField(
                      controller: ngayNghiController,
                      decoration: const InputDecoration(
                        labelText: 'Ngày nghỉ (YYYY-MM-DD)',
                        prefixIcon: Icon(Icons.calendar_today, size: 20),
                      ),
                    ),
                    const SizedBox(height: 12),
                    TextField(
                      controller: lyDoController,
                      maxLines: 3,
                      decoration: const InputDecoration(
                        labelText: 'Lý do vắng học (*)',
                        hintText: 'Nhập rõ lý do nghỉ và đính kèm minh chứng nếu có...',
                      ),
                    ),
                  ],
                ),
              ),
              actions: [
                TextButton(onPressed: () => Navigator.pop(context), child: const Text('HỦY')),
                ElevatedButton(
                  onPressed: () {
                    if (lyDoController.text.trim().isEmpty) return;
                    final don = DonXinPhep(
                      MaDon: 'DON_${DateTime.now().millisecondsSinceEpoch}',
                      MaSV: user?.MaND ?? '2380600008',
                      TenSV: user?.HoTen ?? 'Nguyễn Hoài An',
                      MaLHP: selectedClass.MaLHP,
                      TenLHP: selectedClass.TenLHP,
                      NgayNghi: ngayNghiController.text.trim(),
                      LyDo: lyDoController.text.trim(),
                      TrangThaiDuyet: 'ChoDuyet',
                    );
                    MockDataRepository().guiDonXinPhep(don);
                    Navigator.pop(context);
                    _loadRequests();
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(
                        content: Text('Đã gửi đơn xin phép tới giảng viên!'),
                        backgroundColor: Colors.green,
                      ),
                    );
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF1E3A8A),
                    foregroundColor: Colors.white,
                  ),
                  child: const Text('GỬI ĐƠN'),
                ),
              ],
            );
          },
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text('Đơn Xin Phép Của Tôi'),
        backgroundColor: const Color(0xFF1E3A8A),
        foregroundColor: Colors.white,
      ),
      body: _myRequests.isEmpty
          ? const Center(child: Text('Bạn chưa gửi đơn xin nghỉ nào.'))
          : ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: _myRequests.length,
              itemBuilder: (context, idx) {
                final d = _myRequests[idx];
                Color statusColor = Colors.orange;
                String statusLabel = 'Chờ duyệt';
                if (d.TrangThaiDuyet == 'DaDuyet') {
                  statusColor = Colors.green;
                  statusLabel = 'Đã duyệt';
                } else if (d.TrangThaiDuyet == 'TuChoi') {
                  statusColor = Colors.red;
                  statusLabel = 'Từ chối';
                }

                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                  elevation: 2,
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text(d.TenLHP, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                            Container(
                              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                              decoration: BoxDecoration(
                                color: statusColor.withOpacity(0.15),
                                borderRadius: BorderRadius.circular(12),
                              ),
                              child: Text(
                                statusLabel,
                                style: TextStyle(color: statusColor, fontWeight: FontWeight.bold, fontSize: 12),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 6),
                        Text('Ngày nghỉ: ${d.NgayNghi}', style: const TextStyle(fontSize: 13, color: Colors.black87)),
                        const SizedBox(height: 4),
                        Text('Lý do: ${d.LyDo}', style: TextStyle(fontSize: 13, color: Colors.grey.shade700)),
                      ],
                    ),
                  ),
                );
              },
            ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _showCreateDialog,
        backgroundColor: const Color(0xFF1E3A8A),
        foregroundColor: Colors.white,
        icon: const Icon(Icons.note_add),
        label: const Text('Tạo Đơn Mới'),
      ),
    );
  }
}
