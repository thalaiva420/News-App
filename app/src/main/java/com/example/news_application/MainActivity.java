package com.example.news_application;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements CategoryRVAdapter.CategoryClickInterface {

    private RecyclerView newsRV, categoryRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles> articlesArrayList;
    private ArrayList<CategoryRVModal> categoryRVModalArrayList;
    private CategoryRVAdapter categoryRVAdapter;
    private NewsRVAdapter newsRVAdapter;

    private static final String BASE_URL = "https://newsapi.org/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusBarColor();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        newsRV = findViewById(R.id.idRVNews);
        categoryRV = findViewById(R.id.idRVCategories);
        loadingPB = findViewById(R.id.idPBLoading);

        articlesArrayList = new ArrayList<>();
        categoryRVModalArrayList = new ArrayList<>();

        newsRVAdapter = new NewsRVAdapter(articlesArrayList, this);
        categoryRVAdapter = new CategoryRVAdapter(categoryRVModalArrayList, this, this::onCategoryClick);

        newsRV.setLayoutManager(new LinearLayoutManager(this));
        newsRV.setAdapter(newsRVAdapter); // Use newsRVAdapter here!
        categoryRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // Optional horizontal layout
        categoryRV.setAdapter(categoryRVAdapter);

        getCategories();
        getNews("All");

    }
    private void statusBarColor() {
        Window window = MainActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.action2));
    }

    private void getCategories() {
        categoryRVModalArrayList.add(new CategoryRVModal("All", "all")); // Use a simpler value here. We'll construct the URL later.
        categoryRVModalArrayList.add(new CategoryRVModal("Technology", "technology"));
        categoryRVModalArrayList.add(new CategoryRVModal("Science", "science"));
        categoryRVModalArrayList.add(new CategoryRVModal("General", "general"));
        categoryRVModalArrayList.add(new CategoryRVModal("Business", "business"));
        categoryRVModalArrayList.add(new CategoryRVModal("Entertainment", "entertainment"));
        categoryRVModalArrayList.add(new CategoryRVModal("Health", "health"));
        categoryRVAdapter.notifyDataSetChanged();
    }

    private void getNews(String category) {
        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear(); // Clear existing articles

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        Call<NewsModal> call;
        String apiKey = getString(R.string.news_api_key);  // Get API key from strings.xml
        if (category.equals("All")) {
            call = retrofitAPI.getAllNews("v2/top-headlines?country=us&apiKey=" + apiKey); // Correct URL
        } else {
            // Construct the URL correctly
            String categoryUrl = "v2/top-headlines?country=us&category=" + category + "&apiKey=" + apiKey; // Correct URL
            call = retrofitAPI.getNewsByCategory(categoryUrl);
        }

        call.enqueue(new Callback<NewsModal>() {
            @Override
            public void onResponse(Call<NewsModal> call, Response<NewsModal> response) {
                loadingPB.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    NewsModal newsModal = response.body();

                    if (newsModal != null) {
                        ArrayList<Articles> articles = newsModal.getArticles();

                        if (articles != null) {
                            articlesArrayList.clear(); // Clear the list before adding new items
                            articlesArrayList.addAll(articles);  // Use addAll for efficiency

                            newsRVAdapter.notifyDataSetChanged(); // Notify adapter of changes
                        } else {
                            Log.e("MainActivity", "Articles list is null");
                            Toast.makeText(MainActivity.this, "No articles found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("MainActivity", "NewsModal is null");
                        Toast.makeText(MainActivity.this, "Failed to retrieve news data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("MainActivity", "Response not successful: " + response.code());
                    try {
                        Log.e("MainActivity", "Error Body: " + response.errorBody().string());  // Log the error body
                    } catch (Exception e) {
                        Log.e("MainActivity", "Error Body Exception: " + e.getMessage());
                    }
                    Toast.makeText(MainActivity.this, "Failed to retrieve news data. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewsModal> call, Throwable t) {
                loadingPB.setVisibility(View.GONE); // Ensure loading is hidden on failure
                Log.e("MainActivity", "API call failed: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Failed to fetch news: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCategoryClick(int position) {
        String category = categoryRVModalArrayList.get(position).getCategory();
        getNews(category);
    }
}
