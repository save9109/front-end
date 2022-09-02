package com.example.nadri4_edit1;

import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MultiImageAdapter extends RecyclerView.Adapter<MultiImageAdapter.ViewHolder> {

    private ArrayList<JSONObject> mData = null;
    private Context mContext = null;


    public MultiImageAdapter(ArrayList<JSONObject> list, Context context) {
        mData = list;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        EditText comment;
        ViewHolder(View itemView){
            super(itemView);
            //뷰 객체에 대한 참조
            image = itemView.findViewById(R.id.ivPhotos);
            comment = itemView.findViewById(R.id.edtComment);
        }
    }

    @NonNull
    @Override
    public MultiImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        //context에서 LayoutInflater 객체를 얻는다.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.album_page_item, parent, false);
        MultiImageAdapter.ViewHolder viewHolder = new MultiImageAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //이미지 셋팅하기
        Uri imageUri = null;
        try {
            imageUri = Uri.parse(mData.get(position).getString("uri"));
            InputStream inputStream = mContext.getContentResolver().openInputStream(imageUri);
            ExifInterface exif = new ExifInterface(inputStream);

            //기본 썸네일이 있으면 가져오고 없으면 만들기
            if(exif.hasThumbnail()){
                //bitmap변환 라이브러리인 Glide사용
                Glide.with(mContext).load(exif.getThumbnailBytes()).into(holder.image);
            }
            else {
                //썸네일 만드는 작업 추가해야됨!!!
                Glide.with(mContext).load(imageUri).into(holder.image);
            }

        } catch (Exception e) {
            Log.e("HWA", "onBindViewHolder Get Uri Thumbnail Error: " + e);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //코멘트 셋팅하기
        JSONObject imageObject = mData.get(holder.getBindingAdapterPosition());
        try {
            if(imageObject.has("comment")) {
                holder.comment.setText(imageObject.getString("comment"));
            }
            else {
                if(imageObject.has("tags")){
                    JSONArray tags = new JSONArray(imageObject.getString("tags"));
                    String comment = "";
                    for(int i = 0; i < tags.length(); i++){
                        comment += "#" + tags.getJSONObject(i).getString("tag_ko1") + " ";
                    }
                    holder.comment.setText(comment);
                }
            }
        } catch (JSONException e) {
            Log.e("MultiImageAdapter", "get set comment error: " + e);
        }

        holder.comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    mData.get(holder.getBindingAdapterPosition()).put("comment", editable.toString());
                } catch (Exception e) {
                    Log.e("MultiImageAdapter", "put comment error: " + e);
                }
                ;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}