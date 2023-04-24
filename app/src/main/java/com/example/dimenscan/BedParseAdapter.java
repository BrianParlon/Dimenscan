package com.example.dimenscan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BedParseAdapter extends RecyclerView.Adapter<BedParseAdapter.ViewHolder> {

    private ArrayList<ParseItem> parseItems;
    private Context context;

    public BedParseAdapter(ArrayList<ParseItem> parseItems, Context context){
        this.parseItems= parseItems;
        this.context= context;

    }

    @NonNull
    @Override
    public BedParseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parsing_items, parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BedParseAdapter.ViewHolder holder, int position) {
        ParseItem parseItem = parseItems.get(position);
        holder.textView.setText(parseItem.getTitle());
        if(parseItem.getImgUrl().isEmpty()){
            holder.imageView.setImageResource(R.drawable.gradient);
            holder.textView.setText(parseItem.getTitle());

        }else {
            Picasso.get().load(parseItem.getImgUrl()).into(holder.imageView);
            holder.textView.setText(parseItem.getTitle());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bedIntent = new Intent(context, BedInfo.class);
                bedIntent.putExtra("title", parseItem.getTitle());
                bedIntent.putExtra("imageUrl", parseItem.getImgUrl());
                bedIntent.putExtra("depth",parseItem.getDepth());
                bedIntent.putExtra("width",parseItem.getWidth());
                bedIntent.putExtra("bedUrl",parseItem.getDeskUrl());

                context.startActivity(bedIntent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return parseItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView= itemView.findViewById(R.id.imageView2);
            textView = itemView.findViewById(R.id.textViewP);
        }
    }
}
