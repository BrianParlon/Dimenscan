package com.example.dimenscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class purchasesProductView extends AppCompatActivity implements PurchasesAdapter.OnItemCLickListener {

    private RecyclerView recyclerView;
    private PurchasesAdapter adapter;
    private FloatingActionButton cart;
    private FirebaseUser user;
    private String userId;
    private EditText searchView;
    private ProgressBar progressBar;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference, databaseReference2;
    private ValueEventListener dbListener;
    private List<ParseItem> uploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases_product_view);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progress_circle);
        uploads = new ArrayList<>();
        adapter = new PurchasesAdapter(purchasesProductView.this, uploads);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(purchasesProductView.this);
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("purchases").child(userId);


        dbListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                uploads.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ParseItem upload = postSnapshot.getValue(ParseItem.class);
                    //upload.setKey(postSnapshot.getKey());
                    uploads.add(upload);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(purchasesProductView.this, "Error", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(dbListener);
    }
}