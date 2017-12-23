package com.example.admin.clientlogin.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.admin.clientlogin.activities.MainActivity;
import com.example.admin.clientlogin.activities.SplashActivity;
import com.example.admin.clientlogin.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 12/21/2017.
 */

public class DataManager {
    private static final String SERVER_URL_GETAPPINFO = "http://192.168.1.29/mobile_app/public/application/index/getappinfo";
    private static final String SERVER_URL_LOGIN = "http://192.168.1.29/mobile_app/public/application/index/checkuserdetails-web";
    private String PARAM_USERNAME = "Username";
    private String PARAM_PASSWORD = "Password";
    private String PARAM_CLIENTID = "ClientId";
    private String PARAM_USERID = "UserId";
    public static final String MyPREFERENCES = "MyPrefs";

    private Context mContext;
    private static DataManager mInstance;

    private DataManager() {
        mContext = AppController.getAppContext();
    }

    public static final DataManager getInstance() {
        if (mInstance == null) {
            mInstance = new DataManager();
        }
        return mInstance;
    }

    public void clientcheck(final String Clientname, final String UsernameCL, final String Mobilenumber, final String DeviceID, final String DeviceName) {
        JSONObject params = new JSONObject();
        try {
            params.put("ClientName", Clientname);
            params.put("Username", UsernameCL);
            params.put("Mobile", Mobilenumber);
            params.put("DeviceId", DeviceID);
            params.put("DeviceName", DeviceName);
            params.put("ModuleType", "2");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("params",String.valueOf( params));
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, SERVER_URL_GETAPPINFO
                , params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        String status = response.getString("Status");
//                        int clientid = response.getInt("ClientId");
                        if (status.equals("valid")) {
                            String action = response.getString("Action");
                            if (action.equals("Pending")) {
                                AppController.getInstance().login.onClientSuccess(Clientname, UsernameCL, Mobilenumber, DeviceID, DeviceName, action);
                            }
                        } else if (status.equals("invalid")) {

                            Toast.makeText(AppController.getAppContext(), "Enter Valid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(AppController.getAppContext(), "Check Network Connection", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error in sending requst

                Toast.makeText(AppController.getAppContext(), "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonReq.setRetryPolicy(policy);
        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq, "");

    }

    public void logincheck(final String Username, final String Password, final int ClientId) {

        JSONObject params = new JSONObject();
        try {
            params.put(PARAM_CLIENTID, ClientId);
            params.put(PARAM_USERNAME, Username);
            params.put(PARAM_PASSWORD, Password);
        } catch (Exception ex) {
        }
        Log.i("loginparam", String.valueOf(params));

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, SERVER_URL_LOGIN
                , params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int userId = 0;
                if (response != null) {
                    try {
                        String Status = response.getString("Status");
                        if (Status.equals("valid")) {
                            userId = response.getInt("userId");
                            AppController.getInstance().login.onSuccess(Username, Password, userId);
//                            loadTodo(ClientId,userId,Username,Password,"login");
//                            loadHotcases(ClientId,userId,Username,Password,"login");
//                            loadLogs(ClientId,userId,Username,Password,"login");
                        } else if (Status.equals("invalid")) {

                            Toast.makeText(AppController.getAppContext(), "Username or password incorrect", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error in sending requst

                Toast.makeText(AppController.getAppContext(), "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonReq.setRetryPolicy(policy);
        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq, "");
    }


    public void onActionCheck(String clname_sp, String cluser_sp, String mobile_sp, String deviceid_sp, String devicename_sp) {
        JSONObject params = new JSONObject();
        try {
            params.put("ClientName", clname_sp);
            params.put("Username", cluser_sp);
            params.put("Mobile", mobile_sp);
            params.put("DeviceId", deviceid_sp);
            params.put("DeviceName", devicename_sp);
            params.put("ModuleType", "2");
        } catch (Exception ex) {
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, SERVER_URL_GETAPPINFO
                , params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        String status = response.getString("Status");
                        if (status.equals("valid")) {
                            String action = response.getString("Action");
                            if (action.equals("Pending")) {
                                AppController.getInstance().splash.pending();
                            } else if (action.equals("Approved")) {
                                int clientid = response.getInt("ClientId");
                                AppController.getInstance().splash.approved(clientid);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(AppController.getAppContext(), "Check Network Connection", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error in sending requst
                Toast.makeText(AppController.getAppContext(), "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonReq.setRetryPolicy(policy);
        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq, "");

    }

    public void logincheckforsplash(final String Username, final String Password, final int ClientId) {

        JSONObject params = new JSONObject();
        try {
            params.put(PARAM_CLIENTID, ClientId);
            params.put(PARAM_USERNAME, Username);
            params.put(PARAM_PASSWORD, Password);
        } catch (Exception ex) {
        }
        Log.i("loginparam", String.valueOf(params));
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, SERVER_URL_LOGIN
                , params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                int userId = 0;
                if (response != null) {
                    try {
                        String Status = response.getString("Status");
                        if (Status.equals("valid")) {
                            userId = response.getInt("userId");
                            AppController.getInstance().splash.onSuccesssplash(Username, Password, userId);
//                            loadTodo(ClientId,userId,Username,Password,"splash");
//                            loadHotcases(ClientId,userId,Username,Password,"splash");
//                            loadLogs(ClientId,userId,Username,Password,"splash");
                        } else if (Status.equals("invalid")) {
                            Toast.makeText(AppController.getAppContext(), "Username or password incorrect", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error in sending requst
                Toast.makeText(AppController.getAppContext(), "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonReq.setRetryPolicy(policy);
        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq, "");
    }


}
