package com.confessions.android.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.confessions.android.PostsAdapter;
import com.confessions.android.R;
import com.confessions.android.retrofit.ApiClient;
import com.confessions.android.retrofit.PostResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SexAndRelationshipsFragment extends Fragment {
    private View rootView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private final String POST_TYPE="sex and relationships";

    public SexAndRelationshipsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_confessions, container, false);
        initializeViews();
        getPosts(POST_TYPE);
        return rootView;
    }

    private void getPosts(String postType) {
        swipeRefreshLayout.setRefreshing(true);

        ApiClient.getClient().getPosts(postType).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                swipeRefreshLayout.setRefreshing(false);
                if(response.isSuccessful()){
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(new PostsAdapter(getActivity(),response.body().getData()));
                    if(response.body().getData().size()==0){
                        Snackbar.make(recyclerView,"No content available at this moment. Please come back later",Snackbar.LENGTH_LONG).show();
                    }
                }else{
                    Snackbar.make(recyclerView,"Something went wrong. Please pull down to refresh",Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Snackbar.make(recyclerView,"Something went wrong. Please pull down to refresh",Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void initializeViews() {
        swipeRefreshLayout=rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView=rootView.findViewById(R.id.recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts(POST_TYPE);
            }
        });
    }

}
