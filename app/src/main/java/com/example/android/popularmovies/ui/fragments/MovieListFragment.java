/*
 * Copyright 2015 Nicolas Pintos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.popularmovies.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.api.MoviesService;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieResults;
import com.example.android.popularmovies.ui.adapters.MovieListAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieListFragment extends Fragment {

    private ArrayList<Movie> mMovieList;
    private MovieListAdapter mMovieListAdapter;
    private String mMovieListOrder;
    private int mMovieListPage = 1;
    private int mMovieListColumns = 2;

    private GridLayoutManager mGridLayoutManager;

    private boolean mLoading = false;
    private boolean mFirstTime = true;

    private final String MOVIE_LIST = "movie list";
    private final String PAGE = "page";
    public static final String SORT_BY = "sort_by";

    @Bind(R.id.movie_list_recyclerview) RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mMovieListOrder = getArguments().getString(SORT_BY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mRootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, mRootView);

        if (savedInstanceState == null) {
            mMovieList = new ArrayList<>();
            mMovieListPage = 1;
        } else {
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            mMovieListPage = savedInstanceState.getInt(PAGE);
        }

        mMovieListAdapter = new MovieListAdapter(getActivity(), mMovieList);

        mGridLayoutManager = new GridLayoutManager(getActivity(), mMovieListColumns);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMovieListAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                int totalItemCount = mGridLayoutManager.getItemCount();
                int lastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition();

                if (totalItemCount > 1) {

                    if ((lastVisibleItem >= totalItemCount - 1) && (!mLoading)) {
                        fetchMovieList(mMovieListOrder, mMovieListPage);
                    }
                }
            }

        });

        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mFirstTime){
            fetchMovieList(mMovieListOrder,mMovieListPage);
            mFirstTime = false;
        }

    }

    private void fetchMovieList(String order, int page) {
        mLoading = true;
        MoviesService.
                getMoviesApiClient().
                getMovieList(order, page
                        , new Callback<MovieResults>() {
                    @Override
                    public void success(MovieResults movies, Response response) {
                        mMovieListAdapter.add(movies.getResults());
                        mLoading = false;
                        mMovieListPage++;
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Toast.makeText(getActivity(), retrofitError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST, mMovieList);
        outState.putInt(PAGE, mMovieListPage);
    }

}