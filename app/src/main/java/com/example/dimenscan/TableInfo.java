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

public class TableInfo extends AppCompatActivity implements View.OnClickListener {

    private TextView tableName,tableWidth,tableDepth,tablePrice;
    private ImageView tableImg;
    private String tTitle, tImg,tWidth,tDepth,tTableUrl,tPrice;
    private Button viewRoom;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_info);

        tableName =findViewById(R.id.tableTitle);
        tableImg=findViewById(R.id.tablePicture);

        tableDepth =findViewById(R.id.tableDepth);
        tableWidth =findViewById(R.id.tableWidth);
        tablePrice = findViewById(R.id.priceText);


        Intent tableIntent = getIntent();
        tTitle=tableIntent.getStringExtra("title");
        tImg= tableIntent.getStringExtra("imageUrl");
        tWidth=tableIntent.getStringExtra("width");
        tDepth=tableIntent.getStringExtra("depth");
        tTableUrl=tableIntent.getStringExtra("tableUrl");

        viewRoom = (Button)findViewById(R.id.roomView);
        viewRoom.setOnClickListener(this);

        Picasso.get().load(tImg).into(tableImg);
        tableName.setText(tTitle);
//        deskWidth.setText(dWidth);
//        deskDepth.setText(dDepth);
        context = this;

        TableInfo.Content content = new TableInfo.Content();
        content.execute();
    }
    private class Content extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(tTableUrl).get();
                Elements dimensions = doc.select("table.woocommerce-product-attributes");

                for (Element attribute : dimensions) {
                    Elements elementLabel = attribute.select("th.woocommerce-product-attributes-item__label");
                    Elements elementValue = attribute.select("td.woocommerce-product-attributes-item__value");

                    for (int i = 0; i < elementLabel.size(); i++) {
                        String label = elementLabel.get(i).text().trim();
                        String value = elementValue.get(i).text().trim();

                        if (label.equalsIgnoreCase("Width (cm)")) {
                            tWidth = value;
                        } else if (label.equalsIgnoreCase("Depth (cm)")) {
                            tDepth = value;
                        }
                    }
                }
                Elements prices = doc.select("p.price span.woocommerce-Price-amount");
                if (!prices.isEmpty()) {
                    String priceString = prices.first().text();
                    tPrice = priceString;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tableWidth.setText(tWidth);
            tableDepth.setText(tDepth);
            tablePrice.setText(tPrice);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.roomView:
                Toast.makeText(TableInfo.this, "Viewing table in room", Toast.LENGTH_LONG).show();
                viewDeskRoom();
                break;
        }
    }

    public void viewDeskRoom(){
        Intent tableRoom = new Intent(context, ViewTable.class);
        tableRoom.putExtra("depth",tableDepth.getText());
        tableRoom.putExtra("width",tableWidth.getText());
        context.startActivity(tableRoom);
    }
}