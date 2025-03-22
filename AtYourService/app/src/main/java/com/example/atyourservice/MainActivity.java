package com.example.atyourservice;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.atyourservice.Helpers.UnsplashHelper;
import com.example.atyourservice.Models.UnsplashCallback;
import com.example.atyourservice.Models.UnsplashPhoto;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void getPhoto(View v) {
        UnsplashHelper.getRandomPhoto(new UnsplashCallback() {
            @Override
            public void onSuccess(UnsplashPhoto photo) {
                // Handle the successful response
                String imageUrl = photo.getUrls().getRegular();
                System.out.println("Photo URL: " + imageUrl);
                System.out.println("English Slug: " + photo.getAlternativeSlugs().getEn());

                // Load Image to ImageView
                // Glide.with(context).load(imageUrl).into(imageView);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle the error
                e.printStackTrace();
            }
        });
    }
}