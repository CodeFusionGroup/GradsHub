package com.example.gradshub.adapters;

import android.text.format.DateFormat;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gradshub.R;
import com.example.gradshub.model.ModelPost;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder>{

    Context context;
    List<ModelPost>postList;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get the data
        String userId = postList.get(position).getUserId();
        String uEmail = postList.get(position).getUserEmail();
        String uName = postList.get(position).getuName();
        String pTitle = postList.get(position).getPostTitle();
      // String userDp = postList.get(position).getUserDp();
        String pId = postList.get(position).getPostId();
        String pDesc = postList.get(position).getPostDescr();
        String pImage = postList.get(position).getPostImage();
        String pTimeStamp = postList.get(position).getPostTime();

        //convert Timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calender = Calendar.getInstance(Locale.getDefault());
        calender.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calender).toString();

        //set data
        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        holder.pDescrTv.setText(pDesc);

        //set user dp
//        try{
//            Picasso.get().load(userDp).placeHolder(R.drawable.biza_unset).into(MyHolder.upicture);
//        }
//        catch (Exception e){
//
//        }
        //handle button clicks

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "more", Toast.LENGTH_SHORT).show();
            }
        });
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "like", Toast.LENGTH_SHORT).show();
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "comment", Toast.LENGTH_SHORT).show();
            }
        });
        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "share", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{
        //views from row_post.xml

        ImageView uPictureTv,pImageTv;
        TextView uNameTv,pTimeTv,pTitleTv,pDescrTv,pLikesTv;
        ImageButton moreBtn;
        Button likeBtn, commentBtn,shareBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init Views
            uPictureTv =  itemView.findViewById(R.id.upicture);
            pImageTv =  itemView.findViewById(R.id.pImageTv);
            uNameTv  =  itemView.findViewById(R.id.uNameTv);
            pTimeTv  =  itemView.findViewById(R.id.pTimeTv);
            pTitleTv =  itemView.findViewById(R.id.pTitleTv);
            pDescrTv =  itemView.findViewById(R.id.pDescTv);
            pLikesTv =  itemView.findViewById(R.id.pLikesTv);
            moreBtn =  itemView.findViewById(R.id.moreBtn);
            likeBtn =  itemView.findViewById(R.id.likeBtn);
            commentBtn =  itemView.findViewById(R.id.commentBtn);
            shareBtn =  itemView.findViewById(R.id.shareBtn);

        }
    }
}
