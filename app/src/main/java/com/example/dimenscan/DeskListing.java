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

                doc2 = Jsoup.connect("https://flanagans.ie/collections/furniture/study/office-desks/?pa_width-cm=84&pa_depth-cm=48&pa_height-cm=76").get();
                Elements texts = doc2.select("div.title-wrapper");
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

                        //adds items so that they can be viewed in the recyclerview
                        parseItems.add(new ParseItem(imgUrl, title));
                        Log.d("items", "img: " + imgUrl + " . title: " + title);
                        i++;
                    }
                }

//                //Get Document object after parsing the html from given url.
//                doc = Jsoup.connect("https://www.wayfair.ie/filters/furniture/sb2/rectangular-desk-desks-c1774332-a331~537-p86169~250~1800.html").get();
//
//                //Get images from document object.
//                Elements images =
//                        doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
//
//                doc2 = Jsoup.connect("https://www.wayfair.ie/filters/furniture/sb2/rectangular-desk-desks-c1774332-a331~537-p86169~250~1800.html").get();
//                Elements texts = doc2.select("h2.kb51y90_6101 kb51y91_6101");
//                //Iterate images and print image attributes.
//                int i =0;
//                for (Element image : images) {
//                    System.out.println("Image Source: " + image.attr("src"));
//                    String imgUrl = image.attr("src");
//
//                    String title = texts.select("h2.kb51y90_6101 kb51y91_6101").select("span.StyledBox-owpd5f-0 BoxV2___StyledStyledBox-sc-1wnmyqq-0 eQMeyi").eq(i).text();
//
//
//                    System.out.println("Desk name " + title);
//                    String txt = texts.text();
//
//
//                    parseItems.add(new ParseItem(imgUrl, title));
//                    Log.d("items", "img: " + imgUrl + " . title: " + title);
//                    i++;
//
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
