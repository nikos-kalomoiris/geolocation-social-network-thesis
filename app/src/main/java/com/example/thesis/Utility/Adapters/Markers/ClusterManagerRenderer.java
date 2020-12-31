package com.example.thesis.Utility.Adapters.Markers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.thesis.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class ClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {

    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private final int markerWidth;
    private final int markerHeight;
    private final Context context;

    public ClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = 130;
        markerHeight = 130;
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = 4;
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {

//        imageView.setImageResource(item.getIconPicture());
//        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.title(item.getTitle());
        if(item.getTag().equals("Note")) {
            int icon = R.drawable.ic_fab_note;

            imageView.setImageResource(icon);
            Bitmap currIcon = iconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(currIcon))
                    .snippet(item.getSnippet());
            //markerOptions.snippet(item.getSnippet());
        }
        else if(item.getTag().equals("Event")) {
            int icon = R.drawable.ic_fab_event;

            imageView.setImageResource(icon);
            Bitmap currIcon = iconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(currIcon))
                    .snippet(item.getSnippet());
            //markerOptions.snippet(item.getSnippet());
        }
        else if(item.getTag().equals("User")) {
            markerOptions.visible(false);
        }

    }

    @Override
    protected void onClusterItemRendered(ClusterMarker clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);

        switch (clusterItem.getTag()) {

            case "User": {
                Glide.with(context.getApplicationContext())
                        .asBitmap()
                        //.apply(new RequestOptions().override(20, 20))
                        .load(clusterItem.getIconPicture())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                imageView.setImageBitmap(resource);
                                Bitmap currIcon = iconGenerator.makeIcon();
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(currIcon));
                                marker.setVisible(true);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                marker.setVisible(true);
                            }
                        });
                break;
            }

        }

    }

    @Override
    protected void onClusterItemUpdated(ClusterMarker item, Marker marker) {
        boolean changed = false;
        // Update marker text if the item text changed - same logic as adding marker in CreateMarkerTask.perform()
        if (item.getTitle() != null && item.getSnippet() != null) {
            if (!item.getTitle().equals(marker.getTitle())) {
                marker.setTitle(item.getTitle());
                changed = true;
            }
            if (!item.getSnippet().equals(marker.getSnippet())) {
                marker.setSnippet(item.getSnippet());
                changed = true;
            }
        } else if (item.getSnippet() != null && !item.getSnippet().equals(marker.getTitle())) {
            marker.setTitle(item.getSnippet());
            changed = true;
        } else if (item.getTitle() != null && !item.getTitle().equals(marker.getTitle())) {
            marker.setTitle(item.getTitle());
            changed = true;
        }
        // Update marker position if the item changed position
        if (!marker.getPosition().equals(item.getPosition())) {
            marker.setPosition(item.getPosition());
            changed = true;
        }
        if (changed && marker.isInfoWindowShown()) {
            // Force a refresh of marker info window contents
            marker.showInfoWindow();
        }
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return false;
    }

    public void updateClusterMarker(ClusterMarker clusterMarker) {
        Marker clMarker = getMarker(clusterMarker);
        if(clMarker != null) {
            clMarker.setPosition(clusterMarker.getPosition());
        }
    }

    public void setClusterMarkerVisibility(ClusterMarker clusterMarker, Boolean isVisible) {
        Marker clMarker = getMarker(clusterMarker);
        if(clMarker != null) {
            Log.d("Directions", "isVisible: " + isVisible + " | " + clMarker.getTitle());
            clMarker.setVisible(isVisible);
        }
    }
}
