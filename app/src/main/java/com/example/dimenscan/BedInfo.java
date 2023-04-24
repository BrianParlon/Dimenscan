package com.example.dimenscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.content.Context;
import android.content.Intent;
import android.media.AsyncPlayer;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.InvalidMarkException;

public class BedInfo extends AppCompatActivity implements View.OnClickListener {

    private TextView bedName,bedWidth,bedDepth,bedPrice;
    private ImageView tableImg;
    private String bTitle, bImg,bWidth,bDepth,bTableUrl,bPrice;
    private Button viewRoom;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_info);

        bedName =findViewById(R.id.bedTitle);
        tableImg=findViewById(R.id.bedPicture);

        bedDepth =findViewById(R.id.bedDepth);
        bedWidth =findViewById(R.id.bedWidth);
        bedPrice = findViewById(R.id.priceText);


        Intent bedIntent = getIntent();
        bTitle=bedIntent.getStringExtra("title");
        bImg= bedIntent.getStringExtra("imageUrl");
        bWidth=bedIntent.getStringExtra("width");
        bDepth=bedIntent.getStringExtra("depth");
        bTableUrl=bedIntent.getStringExtra("bedUrl");

        viewRoom = (Button)findViewById(R.id.roomView);
        viewRoom.setOnClickListener(this);

        Picasso.get().load(bImg).into(tableImg);
        bedName.setText(bTitle);
//        deskWidth.setText(dWidth);
//        deskDepth.setText(dDepth);
        context = this;

        BedInfo.Content content = new BedInfo.Content();
        content.execute();
    }
    private class Content extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(bTableUrl).get();
                Elements dimensions = doc.select("table.woocommerce-product-attributes");

                for (Element attribute : dimensions) {
                    Elements elementLabel = attribute.select("th.woocommerce-product-attributes-item__label");
                    Elements elementValue = attribute.select("td.woocommerce-product-attributes-item__value");

                    for (int i = 0; i < elementLabel.size(); i++) {
                        String label = elementLabel.get(i).text().trim();
                        String value = elementValue.get(i).text().trim();

                        if (label.equalsIgnoreCase("Width (cm)")) {
                            bWidth = value;
                        } else if (label.equalsIgnoreCase("Depth (cm)")) {
                            bDepth = value;
                        }
                    }
                }
                Elements prices = doc.select("p.price span.woocommerce-Price-amount");
                if (!prices.isEmpty()) {
                    String priceString = prices.first().text();
                    bPrice = priceString;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bedWidth.setText(bWidth);
            bedDepth.setText(bDepth);
            bedPrice.setText(bPrice);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.roomView:
                Toast.makeText(BedInfo.this, "Viewing table in room", Toast.LENGTH_LONG).show();
                viewDeskRoom();
                break;
        }
    }

    public void viewDeskRoom(){
        Intent tableRoom = new Intent(context, ViewTable.class);
        tableRoom.putExtra("depth",bedDepth.getText());
        tableRoom.putExtra("width",bedWidth.getText());
        context.startActivity(tableRoom);
    }
}