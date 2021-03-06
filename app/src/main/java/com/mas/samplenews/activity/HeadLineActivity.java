package com.mas.samplenews.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mas.samplenews.R;
import com.mas.samplenews.adapter.NewsAdapter;
import com.mas.samplenews.model.NewsModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mas.samplenews.BuildConfig.GET_TOP_HEADLINES;

public class HeadLineActivity extends AppCompatActivity implements NewsAdapter.onSelectData {

    RecyclerView rvHeadNews;
    NewsAdapter newsAdapter;
    List<NewsModel> newsModelList = new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_line);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menampilkan data");

        rvHeadNews = findViewById(R.id.rvNews);
        rvHeadNews.setHasFixedSize(true);
        rvHeadNews.setLayoutManager(new LinearLayoutManager(this));

        setupTollbar();
        loadJSON();
    }

    private void setupTollbar(){
        Toolbar toolbar = findViewById(R.id.tbNews);
        toolbar.setTitle("Berita Utama");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadJSON(){
        progressDialog.show();
        AndroidNetworking.get(GET_TOP_HEADLINES)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            JSONArray jsonArray = response.getJSONArray("articles");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                NewsModel newsModel = new NewsModel();
                                newsModel.setTitleNews(jsonObject.getString("title"));
                                newsModel.setUrlNews(jsonObject.getString("url"));
                                newsModel.setPublishedATNews(jsonObject.getString("publishedAt"));
                                newsModel.setUrlToImageNews(jsonObject.getString("urlToImage"));

                                newsModelList.add(newsModel);
                                showNews();
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(HeadLineActivity.this,
                                    "Gagal menampilkan data!!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(HeadLineActivity.this,
                                "Tidak ada jaringan Internet", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showNews(){
        newsAdapter = new NewsAdapter(HeadLineActivity.this, newsModelList, this);
        rvHeadNews.setAdapter(newsAdapter);
    }

    @Override
    public void onSelected(NewsModel newsModel) {
        startActivity(new Intent(HeadLineActivity.this, OpenNewsActivity.class)
                .putExtra("url", newsModel.getUrlNews()));
    }
}
