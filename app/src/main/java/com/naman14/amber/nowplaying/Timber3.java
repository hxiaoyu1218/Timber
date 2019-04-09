/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.amber.nowplaying;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.amber.R;
import com.naman14.amber.utils.ImageUtils;

public class Timber3 extends BaseNowplayingFragment {

    View mBlurredArt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_timber3, container, false);

        setMusicStateListener();
        setSongDetails(rootView);
        mBlurredArt = (View) rootView.findViewById(R.id.player_root);
        initGestures(rootView.findViewById(R.id.album_art));

        return rootView;
    }

    @Override
    public void doAlbumArtStuff(Bitmap loadedImage) {
        BlurredAlbumArt blurredAlbumArt = new BlurredAlbumArt();
        blurredAlbumArt.execute(loadedImage);
    }

    private class BlurredAlbumArt extends AsyncTask<Bitmap, Void, Drawable> {

        @Override
        protected Drawable doInBackground(Bitmap... loadedImage) {
            Drawable drawable = null;
            try {
                drawable = ImageUtils.createBlurredImageFromBitmap(loadedImage[0], getActivity(), 12);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result != null) {
                if (mBlurredArt.getBackground() != null) {
                    final TransitionDrawable td =
                            new TransitionDrawable(new Drawable[]{
                                    mBlurredArt.getBackground(),
                                    result
                            });
                    mBlurredArt.setBackground(td);
                    td.startTransition(200);

                } else {
                    mBlurredArt.setBackground(result);
                }
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }

}
