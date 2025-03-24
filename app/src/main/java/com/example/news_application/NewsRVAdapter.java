package com.example.news_application;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsRVAdapter extends RecyclerView.Adapter<NewsRVAdapter.ViewHolder> {
    private ArrayList<Articles> articlesArrayList;
    private Context context;

    public NewsRVAdapter(ArrayList<Articles> articlesArrayList, Context context) {
        this.articlesArrayList = (articlesArrayList != null) ? articlesArrayList : new ArrayList<>();
        this.context = context;
    }


    @NonNull
    @Override
    public NewsRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_rv_item, parent, false);
        return new ViewHolder(view); // Corrected this line
    }

    @Override
    public void onBindViewHolder(@NonNull NewsRVAdapter.ViewHolder holder, int position) {
        Articles article = articlesArrayList.get(position); // Changed 'articles' to 'article'

        holder.subTitleTV.setText(article.getDescription());
        holder.titleTV.setText(article.getTitle());
        Picasso.get().load(article.getUrlToImage()).into(holder.newsIV);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, NewsDetailActivity.class);
                i.putExtra("title", article.getTitle());
                i.putExtra("content", article.getContent());
                i.putExtra("desc", article.getDescription());
                i.putExtra("image", article.getUrlToImage());
                i.putExtra("url", article.getUrl());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (articlesArrayList != null) ? articlesArrayList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTV, subTitleTV;
        private ImageView newsIV;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.idTVNewsHeading);
            subTitleTV = itemView.findViewById(R.id.idTVSubTitle);
            newsIV = itemView.findViewById(R.id.idIVNews);
        }
    }
}
