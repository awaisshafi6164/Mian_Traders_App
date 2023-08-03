package com.example.miantraders;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getProductImage()).into(holder.recImage);
        holder.recName.setText(dataList.get(position).getProductName());
        holder.recCode.setText("#"+dataList.get(position).getProductCode());
        holder.recPrice.setText("Rs."+dataList.get(position).getProductPrice());
        holder.recPerc.setText(dataList.get(position).getProductPercentage()+"%");
        holder.recCateg.setText("Cat: "+dataList.get(position).getProductCategory());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, activity_detail.class);

                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getProductImage());
                intent.putExtra("Name", dataList.get(holder.getAdapterPosition()).getProductName());
                intent.putExtra("Code", dataList.get(holder.getAdapterPosition()).getProductCode());
                intent.putExtra("Price", dataList.get(holder.getAdapterPosition()).getProductPrice());
                intent.putExtra("Percentage", dataList.get(holder.getAdapterPosition()).getProductPercentage());
                intent.putExtra("Category", dataList.get(holder.getAdapterPosition()).getProductCategory());
                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage;
    TextView recName, recCode, recPrice, recPerc, recCateg;
    CardView recCard;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recName = itemView.findViewById(R.id.recName);
        recCode = itemView.findViewById(R.id.recCode);
        recPrice = itemView.findViewById(R.id.recPrice);
        recPerc = itemView.findViewById(R.id.recPerc);
        recCateg = itemView.findViewById(R.id.recCateg);
        recCard = itemView.findViewById(R.id.recCard);
    }
}
