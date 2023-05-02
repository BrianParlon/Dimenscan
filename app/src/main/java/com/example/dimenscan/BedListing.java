package com.example.dimenscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

public class BedListing extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BedParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_listing);

        progressBar = findViewById(R.id.progress_circle);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BedParseAdapter(parseItems, this);
        recyclerView.setAdapter(adapter);

        BedListing.Content content = new BedListing.Content();
        content.execute();
    }




    private class Content extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(BedListing.this, android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(BedListing.this, android.R.anim.fade_out));
            adapter.notifyDataSetChanged();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            Document doc,doc2;
            Intent bedEntry = getIntent();
            String depth = bedEntry.getStringExtra("depth");
            String width = bedEntry.getStringExtra("width");
            String height =bedEntry.getStringExtra("height");
            try {
                doc = Jsoup.connect("https://flanagans.ie/collections/furniture/bedroom/beds/?pa_width-cm=" + width + "&pa_depth-cm=" + depth + "&pa_height-cm=" + height+"&instock_products=in").get();
                Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");

              //  doc2 = Jsoup.connect("https://flanagans.ie/collections/furniture/bedroom/beds/?pa_width-cm=" + width + "&pa_depth-cm=" + depth + "&pa_height-cm=" + height+"&instock_products=in").get();
                Elements texts = doc.select("div.title-wrapper");
                int i =0;
                for (Element image : images) {
                    if (image.attr("src")
                            .equalsIgnoreCase("https://flanagans.ie/wp-content/uploads/2022/03/Final-Logo.png")) {
//                        System.out.println("none");
                    } else {
//                        System.out.println("Image Source: " + image.attr("src"));
                        String imgUrl = image.attr("src");

                        String title = texts.select("div.title-wrapper").select("a").eq(i).text();

//                        System.out.println("Bed name " + title);
                       // String txt = texts.text();

                        String bedUrl = texts.select("div.title-wrapper").select("a").eq(i).attr("href");
//                        System.out.println(bedUrl);

                        Document beds = Jsoup.connect(bedUrl).get();
                        Element dimensions = beds.select("div.woocommerce-Tabs-panel--description").first();
                        String dimensionsText;

                        if(dimensions!=null) {
                            dimensionsText = dimensions.text();
                        }
                        else   {
                            dimensionsText ="information not found";
                        }

                        // Extract Width
                        Pattern wPattern = Pattern.compile("W(\\d+)cm");
                        Matcher wMatcher = wPattern.matcher(dimensionsText);
                        String bedWidth = "Not found";
                        if (wMatcher.find()) {
                            bedWidth = wMatcher.group(1);
                        }
//                        System.out.println("Width: " + bedWidth);

                        // Extract Depth
                        Pattern dPattern = Pattern.compile("D(\\d+)cm");
                        Matcher dMatcher = dPattern.matcher(dimensionsText);
                        String bedDepth = "Not found";
                        if (dMatcher.find()) {
                            bedDepth = dMatcher.group(1);
                        }
//                        System.out.println("Depth: " + bedDepth);

                        //adds items so that they can be viewed in the recyclerview
                        //need to change the items added to adapter below to bring over width, height ,url and price
                        parseItems.add(new ParseItem(imgUrl, title,bedWidth,bedDepth,bedUrl));
//                        Log.d("items", "img: " + imgUrl + " . title: " + title);
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
