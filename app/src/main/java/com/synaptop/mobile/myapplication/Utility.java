package com.synaptop.mobile.myapplication;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.synaptop.mobile.myapplication.data.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by FM on 5/11/2016.
 */
public class Utility {


    //parse the .plist file to Json and store it in an ArrayList
    public static ArrayList<Places> loadJSONFromAsset(Activity ac) {
        ArrayList<Places> mList = new ArrayList<>();
        String json = null;
        try {
            InputStream is = ac.getAssets().open("list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            JSONArray mArray = new JSONArray(json);
            for (int i = 0; i < mArray.length(); i++) {
                JSONObject mInside = mArray.getJSONObject(i);
                float lng = (float) mInside.getDouble("lng");
                float lat = (float) mInside.getDouble("lat");
                String url = mInside.getString("url");
                String title = mInside.getString("title");
                Places location = new Places(lng, lat, url, title);
                mList.add(location);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mList;
    }

}
