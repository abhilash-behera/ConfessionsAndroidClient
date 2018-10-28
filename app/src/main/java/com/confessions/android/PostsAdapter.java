package com.confessions.android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.confessions.android.retrofit.ApiClient;
import com.confessions.android.retrofit.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    private Context context;
    private List<Post> posts;
    private final int VIEW_TYPE_POST=1;

    public PostsAdapter(Context context, List<Post> posts){
        this.context=context;
        this.posts=posts;
        Collections.reverse(this.posts);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        public ImageView postImage;
        public TextView postTitle;
        public TextView postTime;
        public PostViewHolder(View itemView){
            super(itemView);
            postImage=itemView.findViewById(R.id.postImage);
            postTitle=itemView.findViewById(R.id.postTitle);
            postTime=itemView.findViewById(R.id.postTime);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_POST;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEW_TYPE_POST:
                View itemView=LayoutInflater.from(context).inflate(R.layout.post_row_view,parent,false);
                return new PostViewHolder(itemView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post=posts.get(position);
        holder.postTitle.setText(post.getTitle());
        Date postDate=new Date(Long.valueOf(post.getTime()));
        Date currentDate=new Date();
        holder.postTime.setText(getQuoteTime((long)(currentDate.getTime()-postDate.getTime())/1000));
        String imageUrl=ApiClient.BASE_URL+post.getImage();
        Log.d("awesome","imageUrl:"+imageUrl);
        GlideApp.with(context).load(imageUrl).centerCrop().into(holder.postImage);
    }

    private String getQuoteTime(long seconds){
        int days=(int)TimeUnit.SECONDS.toDays(seconds);
        long hours=TimeUnit.SECONDS.toHours(seconds)-TimeUnit.DAYS.toHours(days);

        int months=days/30;
        int years=months/12;

        long minutes=TimeUnit.SECONDS.toMinutes(seconds)
                -TimeUnit.DAYS.toMinutes(days)
                -TimeUnit.HOURS.toMinutes(hours);

        long second=TimeUnit.SECONDS.toSeconds(seconds)
                -TimeUnit.DAYS.toSeconds(days)
                -TimeUnit.HOURS.toSeconds(hours)
                -TimeUnit.MINUTES.toSeconds(minutes);
        String postTime="";
        if(years>0){
            if(TextUtils.isEmpty(postTime)){
                if(years==1){
                    postTime=years+" year";
                }else{
                    postTime=years+" years";
                }
            }else{
                postTime=postTime+" "+years+" years";
            }
        }else if(months>0){
            if(TextUtils.isEmpty(postTime)){
                if(months==1){
                    postTime=months+" month";
                }else{
                    postTime=months+" months";
                }
            }else{
                postTime=postTime+" "+months+" months";
            }
        }else if(days>0){
            if(TextUtils.isEmpty(postTime)){
                if(days==1){
                    postTime=days+" day";
                }else{
                    postTime=days+" days";
                }
            }else{
                postTime=postTime+" "+days+" days";
            }

        }else if(hours>0){
            if(TextUtils.isEmpty(postTime)){
                if(hours==1){
                    postTime=hours+" hour";
                }else{
                    postTime=hours+" hours";
                }
            }else{
                postTime=postTime+" "+hours+" hours";
            }
        }else if(minutes>0){
            if(TextUtils.isEmpty(postTime)){
                if(minutes==1){
                    postTime=minutes+" minute";
                }else{
                    postTime=minutes+" minutes";
                }
            }else{
                postTime=postTime+" "+minutes+" minutes";
            }
        }else if(second>0){
            if(TextUtils.isEmpty(postTime)){
                if(second==1){
                    postTime=second+" second";
                }else{
                    postTime=second+" seconds";
                }
            }else{
                postTime=postTime+" "+second+" seconds";
            }
        }

        postTime=postTime+" ago";
        return postTime;
    }
}
