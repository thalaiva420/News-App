package com.example.news_application;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;

public interface RetrofitAPI {
    @Headers({
            "User-Agent: MyNewsApp/1.2 (Android)", // Replace with your app's info
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8",
            "Accept-Language: en-US,en;q=0.5"
    })
    @GET
    Call<NewsModal> getAllNews(@Url String url);

    @Headers({
            "User-Agent: MyNewsApp/1.2 (Android)", // Replace with your app's info
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8",
            "Accept-Language: en-US,en;q=0.5"
    })
    @GET
    Call<NewsModal> getNewsByCategory(@Url String url);
}
