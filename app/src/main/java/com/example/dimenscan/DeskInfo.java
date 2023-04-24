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

public class DeskInfo extends AppCompatActivity implements View.OnClickListener {

    private TextView deskName,deskWidth,deskDepth,deskPrice;
    private ImageView deskImage;
    private String dTitle, dImg,dWidth,dDepth,dDeskUrl,dPrice;
    private Button viewRoom;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk_info);

        deskName =findViewById(R.id.deskTitle);
        deskImage =findViewById(R.id.deskPicture);

        deskDepth =findViewById(R.id.deskDepth);
        deskWidth =findViewById(R.id.deskWidth);
        deskPrice = findViewById(R.id.priceText);


        Intent deskIntent = getIntent();
        dTitle=deskIntent.getStringExtra("title");
        dImg= deskIntent.getStringExtra("imageUrl");
        dWidth=deskIntent.getStringExtra("width");
        dDepth=deskIntent.getStringExtra("depth");
        dDeskUrl=deskIntent.getStringExtra("deskUrl");

        viewRoom = (Button)findViewById(R.id.roomView);
        viewRoom.setOnClickListener(this);

        Picasso.get().load(dImg).into(deskImage);
        deskName.setText(dTitle);
//        deskWidth.setText(dWidth);
//        deskDepth.setText(dDepth);
        context = this;

        DeskInfo.Content content = new DeskInfo.Content();
        content.execute();
    }
    private class Content extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(dDeskUrl).get();
                Elements dimensions = doc.select("table.woocommerce-product-attributes");

                for (Element attribute : dimensions) {
                    Elements elementLabel = attribute.select("th.woocommerce-product-attributes-item__label");
                    Elements elementValue = attribute.select("td.woocommerce-product-attributes-item__value");

                    for (int i = 0; i < elementLabel.size(); i++) {
                        String label = elementLabel.get(i).text().trim();
                        String value = elementValue.get(i).text().trim();

                        if (label.equalsIgnoreCase("Width (cm)")) {
                            dWidth = value;
                        } else if (label.equalsIgnoreCase("Depth (cm)")) {
                            dDepth = value;
                        }
                    }
                }
                Elements prices = doc.select("p.price span.woocommerce-Price-amount");
                if (!prices.isEmpty()) {
                    String priceString = prices.first().text();
                    dPrice = priceString;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            deskWidth.setText(dWidth);
            deskDepth.setText(dDepth);
            deskPrice.setText(dPrice);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.roomView:
                Toast.makeText(DeskInfo.this, "Viewing table in room", Toast.LENGTH_LONG).show();
                viewDeskRoom();
                break;
        }
    }

    public void viewDeskRoom(){
        Intent deskRoom = new Intent(context, ViewDesk.class);
        deskRoom.putExtra("depth",deskDepth.getText());
        deskRoom.putExtra("width",deskWidth.getText());
        context.startActivity(deskRoom);
    }
}