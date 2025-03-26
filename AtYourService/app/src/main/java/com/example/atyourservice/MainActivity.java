package com.example.atyourservice;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

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

    public void saveImageToGallery(View v) {
        // Step 1: Obtain the Bitmap from the ImageView
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false);

        // Step 2: Determine the storage location
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        String imageName = "image_" + System.currentTimeMillis() + ".jpg";
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            // For Android 9 and below
            File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(imagesDir, imageName);
            imageUri = Uri.fromFile(imageFile);
        }

        // Step 3: Save the image
        try (OutputStream outputStream = contentResolver.openOutputStream(imageUri)) {
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Step 4: Update the gallery
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));

        try (OutputStream outputStream = contentResolver.openOutputStream(imageUri)) {
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();

                // Show success message
                Toast.makeText(MainActivity.this, "Image saved successfully!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();

            // Show failure message
            Toast.makeText(MainActivity.this, "Failed to save image. Please try again.", Toast.LENGTH_SHORT).show();
        }
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