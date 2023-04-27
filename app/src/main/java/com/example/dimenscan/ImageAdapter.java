package com.example.dimenscan;

import android.content.Context;
import android.graphics.Color;
import android.telephony.SmsManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{
    private Context itemContext;
    private List<ParseItem> uploadItem;


    public ImageAdapter(Context context, List<ParseItem>parseItems){
        itemContext=context;
        uploadItem=parseItems;

    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(itemContext).inflate(R.layout.image_view,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ParseItem uploadCurrent = uploadItem.get(position);
//        holder.textViewName.setText(uploadCurrent.getTitle());
//        holder.textViewName.setTextColor(Color.RED);
        Picasso.get().load(uploadCurrent.getImgUrl()).into(holder.imageView);
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        holder.textViewName.setText(uploadCurrent.getTitle());

    }

    @Override
    public int getItemCount() {
        return uploadItem.size();
    }

    public void filter(List<ParseItem> filterList) {
        uploadItem = filterList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(purchasesProductView purchasesProductView) {
    }

    public void setOnItemClickListener(ImagesActivity imagesActivity, List<ParseItem> uploads) {
    }

    public interface OnItemCLickListener {
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewP);
            imageView = itemView.findViewById(R.id.imageView2);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);


        }
    }
}



