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

public class ParseAdapter extends RecyclerView.Adapter<ParseAdapter.ViewHolder> {

    private ArrayList<ParseItem> parseItems;
    private Context context;

    public ParseAdapter(ArrayList<ParseItem> parseItems, Context context){
        this.parseItems= parseItems;
        this.context= context;

    }

    @NonNull
    @Override
    public ParseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parsing_items, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParseAdapter.ViewHolder holder, int position) {
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
                Intent deskIntent = new Intent(context, DeskInfo.class);
                deskIntent.putExtra("title", parseItem.getTitle());
                deskIntent.putExtra("imageUrl", parseItem.getImgUrl());
                deskIntent.putExtra("depth",parseItem.getDepth());
                deskIntent.putExtra("width",parseItem.getWidth());

                context.startActivity(deskIntent);
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
