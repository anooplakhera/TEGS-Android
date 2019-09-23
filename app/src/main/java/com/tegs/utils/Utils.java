package com.tegs.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tegs.DatabaseHandler.AppDatabase;
import com.tegs.DatabaseHandler.DBQuesMethodSync;
import com.tegs.DatabaseHandler.DBSetAnsMethodSync;
import com.tegs.QuestionAnswerModel.QuestionAnswerModel;
import com.tegs.R;
import com.tegs.activities.LoginActivity;
import com.tegs.global.TegsApplication;
import com.tegs.model.GetPdfData;
import com.tegs.model.PdfChildData;
import com.tegs.retrofit.RequestParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created
 * by heena on 4/1/18.
 */

public class Utils {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static String PREF_AUTH_TOKEN = "auth_token";
    private static String COMPANY_NAME = "company_name";
    private static String Email = "email";
    private static String Password = "Password";
    private static String PREF_USER_ID = "User-Id";
    private static String PREF_USER_IMAGE = "image";
    private static String PREF_QUES_ANSWER_JSON = "question_answer_data";
    private static String PREF_QUES_TIBrochuTLE = "ques_submit_title";
    private static String Spare_Data = "Spare_Data";
    private static ProgressDialog mProgressDialog;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public Utils(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /*
    Using in Dialog Screen's for custom width
     */
    public static int getScreenWidth(Activity activity) {
        Point p = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(p);
        return p.x;
    }

    /*
    Using in Dialog Screen's for custom height
     */
    public static int getScreenheight(Activity activity) {
        Point p = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(p);
        return p.y;
    }

    /**
     * This method is used for retrofit conversion from string to RequestBody
     *
     * @param s specify string data
     * @return return object of RequestBody
     */
    public static RequestBody getRequestBody(String s) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), s);
    }

    /**
     * This method is used to get RequestBody from file
     *
     * @param file specify file object
     * @return return object of RequestBody
     */
    public static RequestBody getRequestBody(File file) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), file);
    }

    /*
    Using in Activities for JSON Body
     */
    public static RequestBody getJSONRequestBody(HashMap<String, String> stringHashMap) {
        JSONObject jsonObject = new JSONObject();
        if (stringHashMap != null && stringHashMap.size() > 0) {
            try {

                for (Map.Entry<String, String> entry : stringHashMap.entrySet()) {
                    jsonObject.put(entry.getKey(), entry.getValue());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonObject));
    }


    public static HashMap<String, String> getRequestMap(boolean isHeader) {

        Utils utils = new Utils(TegsApplication.getAppInstance());
        HashMap<String, String> map = new HashMap<>();
        if (isHeader) {
            map.put(RequestParameters.XAPI_KEY, RequestParameters.XAPI_KEY_VALUE);
            map.put(RequestParameters.APP_VERSION, RequestParameters.APP_VERSION_VALUE);
            map.put(RequestParameters.PREF_AUTH_TOKEN, utils.getPrefAuthToken());
            map.put(RequestParameters.PREF_USER_ID, String.valueOf(utils.getUserId()));
        } else {
            map.put(RequestParameters.XAPI_KEY, RequestParameters.XAPI_KEY_VALUE);
            map.put(RequestParameters.APP_VERSION, RequestParameters.APP_VERSION_VALUE);
        }

        return map;
    }

    /*
    For Checking the Internet Connectivity
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /*
    For Checking Permission While accessing Gallery
     */
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /*
    Hide KeyBoard
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.findViewById(android.R.id.content).getWindowToken(), 0);
    }

    /*
    Show Progress Dialog
     */
    public static void showProgressDialog(Activity mActivtiy) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mActivtiy);
            mProgressDialog.setMessage(mActivtiy.getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    mProgressDialog = null;
//                }
//            });
            mProgressDialog.show();
        }
    }

    /*
    Dismiss Progress Dialog
     */
    public static void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }

    /*
    Show Snack Bar
     */
    public static void showSnackBar(Activity activity, String message) {
        if (activity != null) {
            ViewGroup view = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            Snackbar mySnackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG).setActionTextColor(Color.RED);

            //setting text color for snackbar
            ViewGroup viewGroup = (ViewGroup) mySnackbar.getView();
            viewGroup.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorAccent));
            View viewTv = mySnackbar.getView();
            TextView tv = (TextView) viewTv.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(ContextCompat.getColor(activity, android.R.color.white));
            mySnackbar.show();
        }

//        if (activity != null) {
//            final AlertDialog alertDilog = new AlertDialog.Builder(activity, R.style.AlertDialogStyle).create();
//            alertDilog.setTitle(activity.getString(R.string.app_name));
//            alertDilog.setMessage(message);
//            alertDilog.setCancelable(false);
//            alertDilog.setButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    alertDilog.dismiss();
//                }
//            });
//
//            alertDilog.show();
//            alertDilog.getWindow().setLayout((int) (Utils.getScreenWidth(activity) * 0.9f), LinearLayout.LayoutParams.WRAP_CONTENT);
//        }
    }

    /*
    Shared Pref Saved Data
     */
    public void setPrefAuthToken(String authToken) {
        editor.putString(PREF_AUTH_TOKEN, authToken);
        Log.d("****AUTHTOKEN****", authToken);
        editor.commit();
    }

    public String getPrefAuthToken() {
        return sharedPreferences.getString(PREF_AUTH_TOKEN, "");
    }

    public void setCompanyName(String companyName) {
        editor.putString(COMPANY_NAME, companyName);
        Log.d("****Company Name****", companyName);
        editor.commit();
    }

    public String getCompanyName() {
        return sharedPreferences.getString(COMPANY_NAME, "");
    }

    public void setEmail(String companyName) {
        editor.putString(Email, companyName);
        Log.d("****Email****", companyName);
        editor.commit();
    }

    public String getEmail() {
        return sharedPreferences.getString(Email, "");
    }

    public void setPassword(String companyName) {
        editor.putString(Password, companyName);
        Log.d("****Password****", companyName);
        editor.commit();
    }

    public String getPassword() {
        return sharedPreferences.getString(Password, "");
    }


    public void setUserId(int id) {
        editor.putInt(PREF_USER_ID, id);
        Log.d("****UserId****", String.valueOf(id));
        editor.commit();
    }

    public int getUserId() {
        return sharedPreferences.getInt(PREF_USER_ID, 0);
    }

    public void setUserImage(String imgPath) {
        editor.putString(PREF_USER_IMAGE, String.valueOf(imgPath));
        editor.commit();
    }

    public String getUserImage() {
        return sharedPreferences.getString(PREF_USER_IMAGE, "");
    }

    public void setQuestionAnswerJsonData(String jsonData) {
        editor.putString(PREF_QUES_ANSWER_JSON, String.valueOf(jsonData));
        editor.commit();
    }

    public static QuestionAnswerModel getQuestionAnswerData() {
        String getQuestionAnswerData = sharedPreferences.getString(PREF_QUES_ANSWER_JSON, "");
        QuestionAnswerModel gson = new Gson().fromJson(getQuestionAnswerData, QuestionAnswerModel.class);
        return gson;
    }

    public static void logout(Context context) {
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
        DBQuesMethodSync.deleteAll(AppDatabase.getAppDatabase(context));
        DBSetAnsMethodSync.deleteAll(AppDatabase.getAppDatabase(context));
        ((Activity) context).finish();
    }

    public static String convertDateFormat(String oldFormat, String newFormat, String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
        try {
            Date date = sdf.parse(dateString);
            sdf = new SimpleDateFormat(newFormat);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String convertDateFormatWithSuffix(String oldFormat, String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
        try {
            Date date = sdf.parse(dateString);
            int day = date.getDate();
            AppLog.d("Day", String.valueOf(day));
            String dayWithSuffix = getDayNumberSuffix(day);
            sdf = new SimpleDateFormat(" dd'" + dayWithSuffix + "' MMMM yyyy");
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static List<PdfChildData> getDataAsCategory(String jsonArray, String category) {
        List<PdfChildData> setFilterList = new ArrayList<>();
        if (category != null) {
            GetPdfData brochuresData = new Gson().fromJson(jsonArray, GetPdfData.class);
            List<PdfChildData> pdfChildDataList = brochuresData.getData();

            if (category.equals("Everything")) {
                for (PdfChildData s : pdfChildDataList) {
                    setFilterList.add(s);
                }
            } else {
                for (PdfChildData s : pdfChildDataList) {
                    if (s.getCatetory().equals(category)) {
                        setFilterList.add(s);
                    }
                }
            }
        }
        return setFilterList;
    }

    public static List<String> getCatNameList(String jsonArray) {
        List<String> catList = new ArrayList<>();
        GetPdfData brochuresData = new Gson().fromJson(jsonArray, GetPdfData.class);
        List<PdfChildData> pdfChildDataList = brochuresData.getData();
        for (PdfChildData s : pdfChildDataList) {
            if (!catList.contains(s.getCatetory())) {
                catList.add(s.getCatetory());
            }
        }
        catList.add("Everything");
        return catList;
    }

    public static void watchYoutubeVideo(Activity activity, String youTubeUrl) {
//        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youTubeUrl)));
        if (!youTubeUrl.equals("")) {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youTubeUrl));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youTubeUrl));
            try {
                activity.startActivity(appIntent);
            } catch (Exception e) {
                activity.startActivity(webIntent);
            }
        }
    }
}
