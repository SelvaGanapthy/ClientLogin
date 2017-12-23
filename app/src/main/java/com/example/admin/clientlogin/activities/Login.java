package com.example.admin.clientlogin.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.admin.clientlogin.R;
import com.example.admin.clientlogin.app.AppController;
import com.example.admin.clientlogin.utils.DataManager;

public class Login extends AppCompatActivity {
    RelativeLayout layout_login, layout_getclient, layout_verifypending;
    EditText edtUsername, edtPassword, edtUsernameCL, edtClientname, edtMobile;
    String cl_name, cl_user, cl_mobile, cl_deviceid, cl_devicename, cl_action;
    SharedPreferences sp;
    Button btnLogin, btnVerify, btnClose;
    String actionStatus;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String clientname = "clientname";
    public static final String clientid = "clientid";
    public static final String cl_username = "cl_username";
    public static final String mobile = "mobile";
    public static final String deviceid = "deviceid";
    public static final String devicename = "devicename";
    public static final String action = "action";
    public static String username = "username";
    public static String password = "password";
    public static String userid = "userid";
    private int ClientId;
    String r_username, r_password;
    String deluserpass;
    int cl_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppController.getInstance().login = this;
        layout_getclient = (RelativeLayout) findViewById(R.id.layout_getclient);
        layout_login = (RelativeLayout) findViewById(R.id.layout_login);
        layout_verifypending = (RelativeLayout) findViewById(R.id.layout_verifypending);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtUsernameCL = (EditText) findViewById(R.id.edtUsernameCL);
        edtClientname = (EditText) findViewById(R.id.edtClientname);
        edtMobile = (EditText) findViewById(R.id.edtMobile);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnClose = (Button) findViewById(R.id.btnClose);
        sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        actionStatus = sp.getString("action", null);
        ClientId = sp.getInt(clientid, 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        deluserpass = getIntent().getStringExtra("deluserpass");

        if (actionStatus != null) {
            if (actionStatus.equals("Pending")) {
                layout_getclient.setVisibility(View.GONE);
                layout_verifypending.setVisibility(View.VISIBLE);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            } else if (actionStatus.equals("Approved")) {
                r_username = sp.getString(username, null);
                r_password = sp.getString(password, null);
                if (deluserpass == null) {
                    if (r_username != null && r_password != null) {
                        layout_getclient.setVisibility(View.GONE);
                        layout_login.setVisibility(View.VISIBLE);
                        Login();
// DataManager.getInstance().logincheck(r_username, r_password, ClientId);

                    } else {
                        layout_getclient.setVisibility(View.GONE);
                        layout_login.setVisibility(View.VISIBLE);
                        Login();
                    }
                } else {
                    if (deluserpass.equals("yes")) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove(username);
                        editor.remove(password);
                        editor.commit();
                    }
                    layout_getclient.setVisibility(View.GONE);
                    layout_login.setVisibility(View.VISIBLE);
                    Login();
                }
            }
        } else {
            VerifyClient();
        }

    }

    public void onSuccess(String user, String pass, int userId) {

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(username, user);
        editor.putString(password, pass);
        editor.putInt(userid, userId);
        editor.commit();
        r_username = sp.getString(username, null);
        r_password = sp.getString(password, null);
        Intent i = new Intent(Login.this, MainActivity.class);
        i.putExtra("username", user);
        i.putExtra("password", pass);
//        i.putExtra("todoList",todoList);
//        i.putExtra("logsList",logsList);
//        i.putExtra("hotcasesList",hotcasesList);
        startActivity(i);
        finish();
    }


    private void Login() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Username = edtUsername.getText().toString();
                String Password = edtPassword.getText().toString();
                if (TextUtils.isEmpty(Username)) {
                    edtUsername.setError("Please enter Username");
                    edtUsername.requestFocus();
                }
                if (TextUtils.isEmpty(Password)) {
                    edtPassword.setError("Please enter Password");
                    edtPassword.requestFocus();
                }
                // startActivity(new Intent(LoginCRM.this, MainActivityNavigation.class));
                DataManager.getInstance().logincheck(Username, Password, ClientId);
            }
        });
    }

    private void VerifyClient() {
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Clientname = edtClientname.getText().toString();
                String UsernameCL = edtUsernameCL.getText().toString();
                String Mobilenumber = edtMobile.getText().toString();
                String DeveiceID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                String DeviceName = getDeviceName();
                if (TextUtils.isEmpty(Clientname)) {
                    edtClientname.setError("Please enter Username");
                    edtClientname.requestFocus();
                }
                if (TextUtils.isEmpty(UsernameCL)) {
                    edtUsernameCL.setError("Please enter Password");
                    edtUsernameCL.requestFocus();
                }
                if (TextUtils.isEmpty(Mobilenumber)) {
                    edtMobile.setError("Please enter Mobilenumber");
                    edtMobile.requestFocus();
                }
                DataManager.getInstance().clientcheck(Clientname, UsernameCL, Mobilenumber, DeveiceID, DeviceName);
            }
        });
    }


    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }


    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }


    public void onClientSuccess(String c_name, String c_username, String c_mobile, String c_deviceid, String c_devicename, String c_action) {
        cl_name = c_name;
        cl_user = c_username;
        cl_mobile = c_mobile;
        cl_deviceid = c_deviceid;
        cl_devicename = c_devicename;
        cl_action = c_action;

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(clientname, cl_name);
        editor.putString(cl_username, cl_user);
        editor.putString(mobile, cl_mobile);
        editor.putString(deviceid, cl_deviceid);
        editor.putString(devicename, cl_devicename);
        editor.putString(action, cl_action);
        editor.commit();
        if (sp.getString(action, null).equals("Pending")) {
            layout_getclient.setVisibility(View.GONE);
            layout_verifypending.setVisibility(View.VISIBLE);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

}
