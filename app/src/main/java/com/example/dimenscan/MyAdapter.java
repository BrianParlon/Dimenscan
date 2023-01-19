package com.example.dimenscan;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dimenscan.Dimension;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {
    List<Dimension> dims;
    List<Dimension> dimAll;
    private final Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(int Position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        listener = clickListener;
    }

    public MyAdapter(List<Dimension> dims, Context context) {
        this.dims= dims;
        this.context = context;
        this.dimAll = new ArrayList<>(dims);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.recyclerview_adapter_layout,null);
        return new MyViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {



        final Dimension dim = dims.get(position);

        String dimHeight = "Height: "+dim.getHeight();
        String dimWidth = "Width : "+dim.getWidth();
        String dimLength ="Length: " + dim.getLength();


        String taskTotal = "Dimensions: "+"\n"+dimHeight +"\n"+dimWidth +"\n"+dimLength;

        holder.txtView.setText(taskTotal);
//        holder.editImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DialogPlus dialogPlus = DialogPlus.newDialog(context)
//                        .setGravity(Gravity.CENTER)
//                        .setMargin(50,0,50,0)
//                        .setContentHolder(new RecyclerView.ViewHolder(R.layout.activity_profile))
//                        .setExpanded(false)
//                        .create();
//                View holderView = (LinearLayout) dialogPlus.getHolderView();
//
//            }
//        });


    }

    @Override
    public int getItemCount() {

        return dims.size();
    }

    @Override
    public Filter getFilter() {

        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Dimension> filteredList = new ArrayList<>();
            if(charSequence.toString().isEmpty() || charSequence.toString().equals("")){
                filteredList.addAll(dims);
            } else {
                for (Dimension name : dims) {
                    if(name.getLength().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredList.add(name);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            dims.clear();
            dims.addAll((Collection<? extends Dimension>) filterResults.values);
            notifyDataSetChanged();
        }
    };


    static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView deleteImg,editImg,doneImg;
        public TextView txtView;
        private MyAdapter adapter;


        public MyViewHolder(@NonNull View itemView,OnItemClickListener listener) {
            super(itemView);
            editImg = itemView.findViewById(R.id.edit);
            editImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

            doneImg = itemView.findViewById(R.id.complete);
            doneImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
            deleteImg = itemView.findViewById(R.id.delete);
            txtView = itemView.findViewById(R.id.textView);
            deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onItemClick(getAdapterPosition());

                }
            });

        }
    }
}