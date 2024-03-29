package com.example.dimenscan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.content.Context;
import android.content.Intent;
import android.media.AsyncPlayer;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeskInfo extends AppCompatActivity implements View.OnClickListener {

    private TextView deskName,deskWidth,deskDepth,deskPrice,deskHeight;
    private ImageView deskImage;
    private String dTitle, dImg,dWidth,dDepth,dDeskUrl,dPrice,dHeight;
    private Button viewRoom,pay;
    private Context context;

    //Stripe information
    String publishableKey ="pk_test_51N0hvLJqlrisGEA5png81SsyJN99wqv7IPdheyT61mB3lBj13qu1sULw1LMvfQmEQpwj7NP9z6sIKCwn1xSvbhDI003cDM9gqG";
    String secretKey ="sk_test_51N0hvLJqlrisGEA5fzDMiw7mCDSbOT6a3mBp4Qh4HvsM1Fq6345fCiwX60sCUmBxT4PUPrgyKJouC1FKh7EdhinH00Rh7IduXi";
    String CustomerId;
    String ephericalKey;
    String clientSecret;
    String price;
    PaymentSheet paymentSheet;
    private boolean customerInitialized = false;

    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String userId;
    private FirebaseAuth mAuth;
    private String onlineUserId;
    private List<ParseItem> parseItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk_info);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        parseItems = new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("purchases").child(userId);

        deskName =findViewById(R.id.deskTitle);
        deskImage =findViewById(R.id.deskPicture);

        deskDepth =findViewById(R.id.deskDepth);
        deskWidth =findViewById(R.id.deskWidth);
        deskPrice = findViewById(R.id.priceText);
        deskHeight = findViewById(R.id.deskHght);

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

                            Toast.makeText(DeskInfo.this, CustomerId, Toast.LENGTH_SHORT).show();

                            getEmphericalKey();
                            customerInitialized = true;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(DeskInfo.this, "error", Toast.LENGTH_SHORT).show();
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

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {

        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, Profile.class));

        }
    }

    private void savePurchase() {
        ParseItem parseItem = new ParseItem(dTitle, dImg,dWidth,dDepth,dDeskUrl,dHeight,dPrice);
        String uploadId = databaseReference.push().getKey();
        databaseReference.child(uploadId).setValue(parseItem);

    }

    private void getEmphericalKey() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);

                            ephericalKey = object.getString("id");

                            Toast.makeText(DeskInfo.this, CustomerId, Toast.LENGTH_SHORT).show();

                          //  getClientSecret(CustomerId,ephericalKey);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(DeskInfo.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

    private void getClientSecret(String customerId, String ephericalKey, int priceInCents) {

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);


                            clientSecret=object.getString("client_secret");
                            Toast.makeText(DeskInfo.this, clientSecret, Toast.LENGTH_SHORT).show();
                            paymentFlow();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(DeskInfo.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("amount", String.valueOf(priceInCents));
                params.put("currency","Eur");
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
                        else if (label.equalsIgnoreCase("Height (cm)")) {
                            dHeight = value;
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
            deskHeight.setText(dHeight);


        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.roomView:
                Toast.makeText(DeskInfo.this, "Viewing desk in room", Toast.LENGTH_LONG).show();
                viewDeskRoom();
                break;
            case R.id.purchase:
                if (customerInitialized) {
                    String priceString = deskPrice.getText().toString();
                    priceString = priceString.replace("€", "").trim();
                    int priceCents = (int) (Double.parseDouble(priceString) * 100);
                    getClientSecret(CustomerId, ephericalKey, priceCents);

                } else {
                    Toast.makeText(DeskInfo.this, "id not set", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void viewDeskRoom(){
        Intent deskRoom = new Intent(context, DeskRoomEntry.class);
        deskRoom.putExtra("depth",deskDepth.getText());
        deskRoom.putExtra("width",deskWidth.getText());
        deskRoom.putExtra("price",deskPrice.getText());
        deskRoom.putExtra("title",deskName.getText());
        context.startActivity(deskRoom);
    }
}