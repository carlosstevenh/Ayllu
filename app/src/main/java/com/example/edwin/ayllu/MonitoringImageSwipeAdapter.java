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

    private ArrayList<File> imagenesFiles = null;
    private String[] imagenesRutas = null;
    private Context ctx;
    private LayoutInflater layoutInflater;

    public MonitoringImageSwipeAdapter(Context ctx, ArrayList<File> imgs){
        this.ctx = ctx;
        this.imagenesFiles = imgs;
    }

    public MonitoringImageSwipeAdapter(Context ctx, String[] imgs){
        this.ctx = ctx;
        this.imagenesRutas = imgs;
    }

    @Override
    public int getCount() {
        if(imagenesFiles != null) return imagenesFiles.size();
        else return  imagenesRutas.length;
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

        if(imagenesFiles != null)
            Picasso.with(ctx).load(imagenesFiles.get(position)).fit().centerCrop().into(ivMonitoring);
        else
            Picasso.with(ctx).load(imagenesRutas[position]).fit().centerCrop().into(ivMonitoring);

        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
