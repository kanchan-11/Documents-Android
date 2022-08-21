package com.example.documents.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.documents.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private String iv_pic[];
    private String names[];
    Context context;

    public RecyclerViewAdapter(Context context,String[] names,String[] iv_pic) {
        this.names = names;
        this.context = context;
        this.iv_pic = iv_pic;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_picture_item,parent,false);
       // ViewHolder viewHolder = new ViewHolder(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.getTv_pic_name().setText(names[position]);
        holder.getIv_pic().setImageBitmap(stringToBitMap(iv_pic[position]));
        holder.tv_pic_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Clicked on "+names[position],Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView tv_pic_name;
        private final ImageView iv_pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_pic_name = itemView.findViewById(R.id.tv_pic_name);
            iv_pic = itemView.findViewById(R.id.iv_pic);
        }
        public TextView getTv_pic_name(){
            return tv_pic_name;
        }
        public ImageView getIv_pic(){
            return iv_pic;
        }
    }
    public static Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
