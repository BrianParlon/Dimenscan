package com.example.dimenscan;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dimenscan.ParseAdapter;
import com.example.dimenscan.ParseItem;
import com.example.dimenscan.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Listing extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParseAdapter(parseItems, this);
        recyclerView.setAdapter(adapter);

        Content content = new Content();
        content.execute();
    }




    private class Content extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(Listing.this, android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(Listing.this, android.R.anim.fade_out));
            adapter.notifyDataSetChanged();
        }


        @Override
        protected Void doInBackground(Void... voids) {
        Document doc,doc2;
            try {
//                String url = "https://www.cinemaqatar.com/";
//
//                Document doc = Jsoup.connect(url).get();
//
//                Elements data = doc.select("span.thumbnail");
//                int size = data.size();
//                Log.d("doc", "doc: "+doc);
//                Log.d("data", "data: "+data);
//                Log.d("size", ""+size);
//                for (int i = 0; i < size; i++) {
//                    String imgUrl = data.select("span.thumbnail")
//                            .select("img")
//                            .eq(i)
//                            .attr("src");
//
//                    String title = data.select("h4.gridminfotitle")
//                            .select("span")
//                            .eq(i)
//                            .text();
//
////                    String detailUrl = data.select("h4.gridminfotitle")
////                            .select("a")
////                            .eq(i)
////                            .attr("href");
//
//                    parseItems.add(new ParseItem(imgUrl, title));
//                    Log.d("items", "img: " + imgUrl + " . title: " + title);
//                }
                doc = Jsoup.connect(
                                "https://flanagans.ie/collections/furniture/study/office-desks/?pa_width-cm=84&pa_depth-cm=48&pa_height-cm=76")
                        .get();
                Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");

                doc2 = Jsoup.connect("https://flanagans.ie/collections/furniture/study/office-desks/?pa_width-cm=84&pa_depth-cm=48&pa_height-cm=76").get();
                Elements texts = doc2.select("div.title-wrapper");
                int i =0;
                for (Element image : images) {

                    if (image.attr("src")
                            .equalsIgnoreCase("https://flanagans.ie/wp-content/uploads/2022/03/Final-Logo.png")) {
                        System.out.println("none");
                    } else {
                        System.out.println("Image Source: " + image.attr("src"));
                        String imgUrl = image.attr("src");


                        String title = texts.select("div.title-wrapper").select("a").eq(i).text();

                        System.out.println("Desk name " + title);
                        String txt = texts.text();

                        parseItems.add(new ParseItem(imgUrl, title));
                        Log.d("items", "img: " + imgUrl + " . title: " + title);

                        i++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}
//parseItems.add(new ParseItem(imgUrl/*, title*/));
//                        Log.d("items", "img: " + imgUrl /*+ " . title: " + title*/);