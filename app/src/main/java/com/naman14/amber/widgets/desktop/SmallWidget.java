package com.naman14.amber.widgets.desktop;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.naman14.amber.MusicService;
import com.naman14.amber.R;
import com.naman14.amber.utils.NavigationUtils;
import com.naman14.amber.utils.AmberUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by nv95 on 02.11.16.
 */

public class SmallWidget extends BaseWidget {

    @Override
    int getLayoutRes() {
        return R.layout.widget_small;
    }

    @Override
    void onViewsUpdate(Context context, RemoteViews remoteViews, ComponentName serviceName, Bundle extras) {
        remoteViews.setOnClickPendingIntent(R.id.image_next, PendingIntent.getService(
                context,
                REQUEST_NEXT,
                new Intent(context, MusicService.class)
                        .setAction(MusicService.NEXT_ACTION)
                        .setComponent(serviceName),
                0
        ));
        remoteViews.setOnClickPendingIntent(R.id.image_playpause, PendingIntent.getService(
                context,
                REQUEST_PLAYPAUSE,
                new Intent(context, MusicService.class)
                        .setAction(MusicService.TOGGLEPAUSE_ACTION)
                        .setComponent(serviceName),
                0
        ));
        if (extras != null) {
            String t = extras.getString("track");
            if (t != null) {
                remoteViews.setTextViewText(R.id.textView_title, t);
            }
            t = extras.getString("artist");
            if (t != null) {
                remoteViews.setTextViewText(R.id.textView_subtitle, t);
            }
            remoteViews.setImageViewResource(R.id.image_playpause,
                    extras.getBoolean("playing") ? R.drawable.ic_pause_white_36dp : R.drawable.ic_play_white_36dp);
            long albumId = extras.getLong("albumid");
            if (albumId != -1) {
                Bitmap artwork = ImageLoader.getInstance().loadImageSync(AmberUtils.getAlbumArtUri(albumId).toString());
                if (artwork != null) {
                    remoteViews.setImageViewBitmap(R.id.imageView_cover, artwork);
                } else {
                    remoteViews.setImageViewResource(R.id.imageView_cover, R.drawable.holder);
                }
            }
        }
        remoteViews.setOnClickPendingIntent(R.id.textView_title, PendingIntent.getActivity(
                context,
                0,
                NavigationUtils.getNowPlayingIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT
        ));
    }
}
