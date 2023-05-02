package com.example.dimenscan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AsyncPlayer;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.InvalidMarkException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableInfo extends AppCompatActivity implements View.OnClickListener {

    private TextView tableName,tableWidth,tableDepth,tablePrice,tableHeight;
    private ImageView tableImg;
    private String tTitle, tImg,tWidth,tDepth,tHeight,tTableUrl,tPrice;
    private Button viewRoom,pay;
    private Context context;
    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String userId;
    private FirebaseAuth mAuth;
    private String onlineUserId;
    private List<ParseItem> parseItems;

    //Stripe information
     String publishableKey ="pk_test_51N0hvLJqlrisGEA5png81SsyJN99wqv7IPdheyT61mB3lBj13qu1sULw1LMvfQmEQpwj7NP9z6sIKCwn1xSvbhDI003cDM9gqG";
     String secretKey ="sk_test_51N0hvLJqlrisGEA5fzDMiw7mCDSbOT6a3mBp4Qh4HvsM1Fq6345fCiwX60sCUmBxT4PUPrgyKJouC1FKh7EdhinH00Rh7IduXi";
     String CustomerId;
     String ephericalKey;
     String clientSecret;
     PaymentSheet paymentSheet;
    private boolean customerInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_info);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        parseItems = new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("purchases").child(userId);


        tableName =findViewById(R.id.tableTitle);
        tableImg=findViewById(R.id.tablePicture);
        tableDepth =findViewById(R.id.tableDepth);
        tableWidth =findViewById(R.id.tableWidth);
        tableHeight= findViewById(R.id.tableHght);
        tablePrice = findViewById(R.id.priceText);

        pay = findViewById(R.id.purchase);
        pay.setOnClickListener(this);

        PaymentConfiguration.init(this, publishableKey);

        paymentSheet = new PaymentSheet(this,paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });


        StringRequest request = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);

                            CustomerId = object.getString("id");

                            Toast.makeText(TableInfo.this, CustomerId, Toast.LENGTH_SHORT).show();

                            getEmphericalKey();
                            customerInitialized = true;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(TableInfo.this, "error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String >header = new HashMap<>();
                header.put("Authorization","Bearer "+secretKey);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);




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

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
            savePurchase();
            startActivity(new Intent(this, Profile.class));

        }
    }

    private void getEmphericalKey() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);

                            ephericalKey = object.getString("id");

                            Toast.makeText(TableInfo.this, CustomerId, Toast.LENGTH_SHORT).show();

                        //   getClientSecret(CustomerId,ephericalKey);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(TableInfo.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String >header = new HashMap<>();
                header.put("Authorization","Bearer "+secretKey);
                header.put("Stripe-Version","2022-11-15");
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("customer",CustomerId);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);



    }

    private void getClientSecret(String customerId, String ephericalKey, int priceCents) {

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);


                            clientSecret=object.getString("client_secret");
                            Toast.makeText(TableInfo.this, clientSecret, Toast.LENGTH_SHORT).show();

                            paymentFlow();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(TableInfo.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String >header = new HashMap<>();
                header.put("Authorization","Bearer "+secretKey);

                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("customer",CustomerId);
                params.put("amount", String.valueOf(priceCents));
                params.put("currency","EUR");
                params.put("automatic_payment_methods[enabled]","true");

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);



    }
    private void paymentFlow() {

        paymentSheet.presentWithPaymentIntent(clientSecret,new PaymentSheet.Configuration("DimenScan",new PaymentSheet.CustomerConfiguration(
                CustomerId,
                ephericalKey
        )));

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
                        else if (label.equalsIgnoreCase("Height (cm)")) {
                            tHeight = value;
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
            tableHeight.setText(tHeight);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.roomView:
                Toast.makeText(TableInfo.this, "Viewing table in room", Toast.LENGTH_LONG).show();
              //  createRoomSize();
                viewDeskRoom();
                break;
            case R.id.purchase:
                if (customerInitialized) {
                    String priceString = tablePrice.getText().toString();
                    priceString = priceString.replace("€", "").trim();
                    int priceInCents = (int) (Double.parseDouble(priceString) * 100);
                    getClientSecret(CustomerId, ephericalKey, priceInCents);


                } else {
                    Toast.makeText(TableInfo.this, "id not set", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void createRoomSize() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.room_object, null);
        // Find the EditTexts in the dialog layout
        EditText widthTxt = dialogView.findViewById(R.id.widthEntry);
        EditText heightTxt = dialogView.findViewById(R.id.heightEntry);

        AlertDialog.Builder objDialog = new AlertDialog.Builder(this);
        objDialog.setTitle("Add Room dimensions");
        objDialog.setView(dialogView);
        objDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Parse the width and height entered by the user
                double width = Double.parseDouble(widthTxt.getText().toString());
                double height = Double.parseDouble(heightTxt.getText().toString());

            }
        });
        objDialog.setNegativeButton("Cancel", null);
        objDialog.show();
    }


    private void savePurchase() {

            ParseItem parseItem = new ParseItem(tTitle, tImg,tWidth,tDepth,tTableUrl,tHeight,tPrice);
            String uploadId = databaseReference.push().getKey();
            databaseReference.child(uploadId).setValue(parseItem);

    }


    public void viewDeskRoom(){
        Intent tableRoom = new Intent(context, TableRoomEntry.class);
        tableRoom.putExtra("depth",tableDepth.getText());
        tableRoom.putExtra("width",tableWidth.getText());
        tableRoom.putExtra("price",tablePrice.getText());
        tableRoom.putExtra("title",tableName.getText());
        context.startActivity(tableRoom);
    }
}