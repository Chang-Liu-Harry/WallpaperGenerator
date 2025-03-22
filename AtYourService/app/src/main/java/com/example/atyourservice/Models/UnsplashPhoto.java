package com.example.atyourservice.Models;

public class UnsplashPhoto {
    private String id;
    private UnsplashUrls urls;
    private AlternativeSlugs alternative_slugs;
    private Integer likes;

    public String getId() { return id; }
    public UnsplashUrls getUrls() { return urls; }
    public Integer getLikes() { return likes; }

    public AlternativeSlugs getAlternativeSlugs() {
        return alternative_slugs;
    }
}
