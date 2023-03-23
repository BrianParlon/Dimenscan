package com.example.dimenscan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeskListing extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk_listing);


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
            progressBar.startAnimation(AnimationUtils.loadAnimation(DeskListing.this, android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(DeskListing.this, android.R.anim.fade_out));
            adapter.notifyDataSetChanged();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            Document doc,doc2;
            String name,img;
            try {
                doc = Jsoup.connect("https://flanagans.ie/collections/furniture/study/office-desks/?pa_width-cm=84&pa_depth-cm=48&pa_height-cm=76").get();
                Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");

//                doc2 = Jsoup.connect("https://flanagans.ie/collections/furniture/study/office-desks/?pa_width-cm=84&pa_depth-cm=48&pa_height-cm=76").get();
                Elements texts = doc.select("div.title-wrapper");
                int i =0;
                for (Element image : images) {
                    if (image.attr("src")
                            .equalsIgnoreCase("https://flanagans.ie/wp-content/uploads/2022/03/Final-Logo.png")) {
                        System.out.println("none");
                    } else {
                        //images
                        System.out.println("Image Source: " + image.attr("src"));
                        String imgUrl = image.attr("src");

                        //title
                        String title = texts.select("div.title-wrapper").select("a").eq(i).text();
                        System.out.println("Desk name " + title);

                        String deskUrl = texts.select("div.title-wrapper").select("a").eq(i).attr("href");
                        System.out.println(deskUrl);

                        Document deskDetails = Jsoup.connect(deskUrl).get();
                        Element dimensions = deskDetails.select("div.woocommerce-Tabs-panel--description").first();

                        String dimensionsText = dimensions.text();
                        Pattern Wpattern = Pattern.compile("W(\\d+)cm");
                        Matcher Wmatcher = Wpattern.matcher(dimensionsText);

                        String width=null;
                        if (Wmatcher.find()) {
                             width = Wmatcher.group(1);
                            System.out.println("Width: " + width);
                        } else {
                            System.out.println("Width not found");
                        }

                        Pattern Dpattern = Pattern.compile("D(\\d+)cm");
                        Matcher Dmatcher = Dpattern.matcher(dimensionsText);

                        String depth = null;
                        if (Dmatcher.find()) {
                            depth = Dmatcher.group(1);
                            System.out.println("depth: " + depth);
                        } else {
                            System.out.println("depth not found");
                        }

                        //adds items so that they can be viewed in the recyclerview
                        parseItems.add(new ParseItem(imgUrl, title, depth,width));
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