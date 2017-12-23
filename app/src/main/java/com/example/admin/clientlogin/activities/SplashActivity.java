package com.example.admin.clientlogin.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.admin.clientlogin.R;
import com.example.admin.clientlogin.app.AppController;
import com.example.admin.clientlogin.utils.DataManager;

import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {
    public static final int requestcode_permisson = 1;
    int cl_id;
    public final String clientid = "clientid";
    public final String mobile = "mobile";
    public final String devicename = "devicename";
    public final String deviceid = "deviceid";
    public final String clientname = "clientname";
    public final String action = "action";
    SharedPreferences sp;
    public final String MyPreference = "MyPreference";
    GifImageView splashimage;
    public static String username = "username";
    public static String password = "password";
    public static final String MyPREFERENCES = "MyPrefs";
    String r_user, r_pass;
    String clname_sp, cluser_sp, mobile_sp, deviceid_sp, devicename_sp, action_sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AppController.getInstance().splash = this;
        sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        r_user = sp.getString(username, null);
        r_pass = sp.getString(password, null);
        cl_id = sp.getInt(clientid, 0);
        clname_sp = sp.getString(clientname, null);
        mobile_sp = sp.getString(mobile, null);
        deviceid_sp = sp.getString(deviceid, null);
        devicename_sp = sp.getString(devicename, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        appstart();
    }

    private void appstart() {
        if (checkPermission()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (action_sp != null) {
                        if (action_sp.equals("Pending")) {
                            if (clname_sp != null && cluser_sp != null && mobile_sp != null && deviceid_sp != null && devicename_sp != null) {
                                DataManager.getInstance().onActionCheck(clname_sp, cluser_sp, mobile_sp, deviceid_sp, devicename_sp);
                            }
                        } else if (action_sp.equals("Approved")) {
                            if (r_user != null && r_pass != null && cl_id != 0) {
                                DataManager.getInstance().logincheckforsplash(r_user, r_pass, cl_id);
                            } else {
                                Intent i = new Intent(SplashActivity.this, Login.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    } else {
                        Intent i = new Intent(SplashActivity.this, Login.class);
                        startActivity(i);
                        finish();
                    }
                }
            }, 3000);
        } else {
            requestPermission();
        }
    }
    public void pending() {
        Intent i = new Intent(SplashActivity.this, Login.class);
        startActivity(i);
        finish();
    }

    public void approved(int c_id) {
        cl_id = c_id;
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(action);
        editor.putString(action, "Approved");
        editor.putInt(clientid, cl_id);
        editor.commit();
        Intent i = new Intent(SplashActivity.this, Login.class);
        startActivity(i);
        finish();
    }
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.PROCESS_OUTGOING_CALLS);
        return (result == PackageManager.PERMISSION_GRANTED) && (result1 == PackageManager.PERMISSION_GRANTED) && (result2 == PackageManager.PERMISSION_GRANTED) && (result3 == PackageManager.PERMISSION_GRANTED);
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.READ_PHONE_STATE}, requestcode_permisson);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case requestcode_permisson:
                if (grantResults.length > 0) {
                    boolean StoragePermisson = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermisson = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean outgoingcallPermisson = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readphonestatePermisson = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermisson && RecordPermisson && outgoingcallPermisson && readphonestatePermisson) {
                        Toast.makeText(SplashActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                        onSuccess();
                    } else {
                        Toast.makeText(SplashActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        onSuccess();
                    }
                }
                break;
        }
    }
    public void onSuccesssplash(String user, String pass, int userId) {
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        i.putExtra("username", user);
        i.putExtra("password", pass);
//        i.putExtra("todoList",todoList);
//        i.putExtra("logsList",logsList);
//        i.putExtra("hotcasesList",hotcasesList);
        startActivity(i);
        finish();
    }



    private void onSuccess() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (action_sp != null) {
                    if (action_sp.equals("Pending")) {
                        if (clname_sp != null && cluser_sp != null && mobile_sp != null && deviceid_sp != null && devicename_sp != null) {
                            DataManager.getInstance().onActionCheck(clname_sp, cluser_sp, mobile_sp, deviceid_sp, devicename_sp);
                        }
                    } else if (action_sp.equals("Approved")) {
                        if (r_user != null && r_pass != null && cl_id != 0) {
                            DataManager.getInstance().logincheckforsplash(r_user, r_pass, cl_id);
                        } else {
                            Intent i = new Intent(SplashActivity.this, Login.class);
                            startActivity(i);
                            finish();
                        }
                    }
                } else {

                    Intent i = new Intent(SplashActivity.this, Login.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 1000);
    }
}
