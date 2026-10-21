import 'package:flutter_test/flutter_test.dart';
import 'package:nguyenhoaian_2380600008/main.dart';

void main() {
  testWidgets('AttendanceApp smoke test', (WidgetTester tester) async {
    await tester.pumpWidget(const AttendanceApp());
    expect(find.text('ĐIỂM DANH SINH VIÊN'), findsOneWidget);
  });
}
