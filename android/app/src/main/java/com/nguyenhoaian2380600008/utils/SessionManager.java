package com.nguyenhoaian2380600008.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.nguyenhoaian2380600008.api.ApiClient;
import com.nguyenhoaian2380600008.models.NguoiDung;

public class SessionManager {
    private static final String PREF_NAME = "SmartAttendanceSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_DATA = "userData";
    private static final String KEY_DEVICE_ID = "deviceId";
    private static final String KEY_AUTH_TOKEN = "authToken";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Gson gson;
    private static SessionManager instance;

    public SessionManager(Context context) {
        pref = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
        String savedToken = pref.getString(KEY_AUTH_TOKEN, "");
        if (savedToken != null && !savedToken.isEmpty()) {
            ApiClient.setAuthToken(savedToken);
        }
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void saveUserLogin(NguoiDung user, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_DATA, gson.toJson(user));
        String finalToken = (user != null && user.getToken() != null && !user.getToken().isEmpty())
                ? user.getToken() : token;
        if (finalToken != null) {
            editor.putString(KEY_AUTH_TOKEN, finalToken);
            ApiClient.setAuthToken(finalToken);
        }
        if (user != null && user.getMaThietBi() != null) {
            editor.putString(KEY_DEVICE_ID, user.getMaThietBi());
        }
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public NguoiDung getCurrentUser() {
        String json = pref.getString(KEY_USER_DATA, null);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, NguoiDung.class);
    }

    public String getUserRole() {
        NguoiDung user = getCurrentUser();
        return user != null ? user.getVaiTro() : null;
    }

    public String getAuthToken() {
        return pref.getString(KEY_AUTH_TOKEN, "");
    }

    public void setDeviceId(String deviceId) {
        editor.putString(KEY_DEVICE_ID, deviceId);
        editor.apply();
    }

    public String getDeviceId() {
        return pref.getString(KEY_DEVICE_ID, "");
    }

    public void logout() {
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USER_DATA);
        editor.remove(KEY_AUTH_TOKEN);
        editor.apply();
    }
}
