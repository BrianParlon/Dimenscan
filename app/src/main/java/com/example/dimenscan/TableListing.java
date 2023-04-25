package com.example.dimenscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableListing extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TableParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();
    private ProgressBar progressBar;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_listing);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TableParseAdapter(parseItems, this);
        recyclerView.setAdapter(adapter);
        this.context= context;

        TableListing.Content content = new TableListing.Content();
        content.execute();
    }




    private class Content extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(TableListing.this, android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(TableListing.this, android.R.anim.fade_out));
            adapter.notifyDataSetChanged();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            Document doc,doc2;
            try {
                doc = Jsoup.connect("https://flanagans.ie/collections/furniture/living-room/coffee-tables/").get();
                Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");

                doc2 = Jsoup.connect("https://flanagans.ie/collections/furniture/living-room/coffee-tables/").get();
                Elements texts = doc2.select("div.title-wrapper");
                int i =0;
                for (Element image : images) {
                    if (image.attr("src")
                            .equalsIgnoreCase("https://flanagans.ie/wp-content/uploads/2022/03/Final-Logo.png")) {
                   //     System.out.println("none");
                    } else {
//                        System.out.println("Image Source: " + image.attr("src"));
                        String imgUrl = image.attr("src");

                        String title = texts.select("div.title-wrapper").select("a").eq(i).text();

//                        System.out.println("Table name " + title);


                        String tableUrl = texts.select("div.title-wrapper").select("a").eq(i).attr("href");
//                        System.out.println(tableUrl);

                        Document tables = Jsoup.connect(tableUrl).get();
                        Element dimensions = tables.select("div.woocommerce-Tabs-panel--description").first();
                        String dimensionsText = dimensions.text();

                        // Extract Width
                        Pattern wPattern = Pattern.compile("W(\\d+)cm");
                        Matcher wMatcher = wPattern.matcher(dimensionsText);
                        String tableWidth = "Not found";
                        if (wMatcher.find()) {
                            tableWidth = wMatcher.group(1);
                        }
//                        System.out.println("Width: " + tableWidth);

                        // Extract Depth
                        Pattern dPattern = Pattern.compile("D(\\d+)cm");
                        Matcher dMatcher = dPattern.matcher(dimensionsText);
                        String tableDepth = "Not found";
                        if (dMatcher.find()) {
                            tableDepth = dMatcher.group(1);
                        }
//                        System.out.println("Depth: " + tableDepth);

                        //adds items so that they can be viewed in the recyclerview
                        //need to change the items added to adapter below to bring over width, height ,url and price
                        parseItems.add(new ParseItem(imgUrl, title,tableWidth,tableDepth,tableUrl));
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
