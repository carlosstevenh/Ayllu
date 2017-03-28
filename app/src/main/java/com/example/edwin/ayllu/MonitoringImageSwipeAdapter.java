package com.example.edwin.ayllu;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class MonitoringImageSwipeAdapter extends PagerAdapter{

    private ArrayList<File> imagenes;
    private Context ctx;
    private LayoutInflater layoutInflater;

    public MonitoringImageSwipeAdapter(Context ctx, ArrayList<File> imgs){
        this.ctx = ctx;
        this.imagenes = imgs;
    }

    @Override
    public int getCount() {
        return imagenes.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.slide_image_monitoring, container, false);
        ImageView ivMonitoring = (ImageView) item_view.findViewById(R.id.iv_monitoring);

        Picasso.with(ctx).load(imagenes.get(position)).fit().centerCrop().into(ivMonitoring);
        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
