package com.example.atyourservice.Models;

public interface UnsplashCallback {
    void onSuccess(UnsplashPhoto photo);
    void onFailure(Exception e);
}
