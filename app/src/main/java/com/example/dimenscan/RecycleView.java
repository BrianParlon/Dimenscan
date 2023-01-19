package com.example.dimenscan;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dimenscan.Dimension;
import com.example.dimenscan.MyAdapter;
import com.example.dimenscan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecycleView extends AppCompatActivity {

    //    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private MyAdapter mAdapter;
    private String userId;
    private DatabaseReference reference, reference2;
    ArrayList<Dimension> dimsList;
    RecyclerView recyclerview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        dimsList = new ArrayList<>();

        recyclerview = findViewById(R.id.recyclerView);
        mAdapter = new MyAdapter(dimsList, this);
        reference = FirebaseDatabase.getInstance().getReference("Dimensions").child(userId);
        reference2 = FirebaseDatabase.getInstance().getReference("Dimensions").child(userId);

        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(RecycleView.this));

        recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerview.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int Position) {

                reference2.removeValue();
                mAdapter.notifyItemRemoved(Position);

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot Tasks : snapshot.getChildren()){

                    Dimension dim = Tasks.getValue(Dimension.class);

                    dimsList.add(dim);

                    recyclerview.setAdapter(mAdapter);
                    mAdapter.notifyItemInserted(dimsList.size());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}