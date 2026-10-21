import '../models/nguoi_dung.dart';

class SessionManager {
  static final SessionManager _instance = SessionManager._internal();
  factory SessionManager() => _instance;
  SessionManager._internal();

  NguoiDung? _currentUser;
  String _currentDeviceId = 'DEVICE_PIXEL7_PRO_AN';

  NguoiDung? get currentUser => _currentUser;
  String get currentDeviceId => _currentDeviceId;

  void setCurrentUser(NguoiDung? user) {
    _currentUser = user;
    if (user != null && user.MaThietBi.isNotEmpty) {
      _currentDeviceId = user.MaThietBi;
    }
  }

  void logout() {
    _currentUser = null;
  }

  bool get isLoggedIn => _currentUser != null;
}
