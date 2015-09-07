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

import android.database.Cursor;
import android.support.v4.content.Loader;
import android.view.View;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.model.Movie;

import org.chalup.microorm.MicroOrm;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;

public class FavoriteMoviesFragment extends BaseMovieListFragment{

    final MicroOrm mOrm = new MicroOrm();
    @BindString(R.string.no_favorite_movies_text) String mNoFavoritesText;
    @BindString(R.string.no_favorite_movies_desc) String mNoFavoritesDesc;


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        super.onLoadFinished(loader, cursor);
        if (cursor != null && cursor.moveToFirst()) {
            List<Movie> favoriteMovies = mOrm.listFromCursor(cursor, Movie.class);
            mMovieList = new ArrayList<>(favoriteMovies);
        }

        else {
            mMovieList.clear();
            mEmptyView.setMessageText(mNoFavoritesText);
            mEmptyView.setMessageDescription(mNoFavoritesDesc);
            mEmptyView.setVisibility(View.VISIBLE);
        }

        mMovieListAdapter.newData(mMovieList);
        mProgressBar.setVisibility(View.GONE);

    }

}
