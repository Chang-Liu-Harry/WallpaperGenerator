package com.example.atyourservice.Helpers;

import android.os.Handler;
import android.os.Looper;

import com.example.atyourservice.Models.UnsplashCallback;
import com.example.atyourservice.Models.UnsplashPhoto;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

public class UnsplashHelper {
    private static final String UNSPLASH_URL = "https://api.unsplash.com/photos/random";
    private static final String ACCESS_KEY = "fe1c22ded50ede0397b7eb80b44e7f27289c95c2b57d964a11e55a57ee3fc571";

    public static void getRandomPhoto(UnsplashCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL url = new URL(UNSPLASH_URL + "?client_id=" + ACCESS_KEY);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    UnsplashPhoto photo = new Gson().fromJson(response.toString(), UnsplashPhoto.class);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(photo));
                } else {
                    throw new Exception("HTTP error code: " + responseCode);
                }
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(e));
            }
        });
    }
}
