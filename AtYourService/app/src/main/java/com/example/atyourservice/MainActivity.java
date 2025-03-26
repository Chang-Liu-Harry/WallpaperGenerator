package com.example.atyourservice;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.atyourservice.Helpers.UnsplashHelper;
import com.example.atyourservice.Models.UnsplashCallback;
import com.example.atyourservice.Models.UnsplashPhoto;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView tvDescription;
    private ProgressBar progressBar;

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

        // find UI elements
        imageView = findViewById(R.id.imageView);
        tvDescription = findViewById(R.id.tvDescription);
        progressBar = findViewById(R.id.progressBar);
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

    public void onGenerateClick(View v) {
        // show loading spinner while we fetch the image
        progressBar.setVisibility(View.VISIBLE);
        v.setEnabled(false);

        UnsplashHelper.getRandomPhoto(new UnsplashCallback() {
            @Override
            public void onSuccess(UnsplashPhoto photo) {
                // if we successfully fetched img
                // gran the image and description
                String imageUrl = photo.getUrls().getRegular();
                String rawSlug = photo.getAlternativeSlugs() != null ?
                        photo.getAlternativeSlugs().getEn() :
                        "There is no description for this image";

                // load image into imageview
                Glide.with(MainActivity.this)
                        .load(imageUrl)
                        .into(imageView);

                // load text
                tvDescription.setText(formatSlug(rawSlug));

                // diable spinner , enable button
                progressBar.setVisibility(View.GONE);
                v.setEnabled(true);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Failed to generate random image. " +
                        "Please try again!", Toast.LENGTH_SHORT).show();

                // Hide loading spinner and re-enable button
                progressBar.setVisibility(View.GONE);
                v.setEnabled(true);
            }
        });
    }

    String formatSlug(String slug) {
        if (slug == null || slug.isEmpty()) return "";

        String[] parts = slug.split("-");
        if (parts.length <= 1) {
            return turnToCaps(slug.replace("-", " "));
        }

        StringBuilder cleaned = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            cleaned.append(parts[i]).append(" ");
        }

        return turnToCaps(cleaned.toString().trim());
    }

    String turnToCaps(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}