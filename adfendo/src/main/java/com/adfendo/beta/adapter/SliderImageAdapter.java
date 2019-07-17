package com.adfendo.beta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.adfendo.beta.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class SliderImageAdapter extends PagerAdapter {
    Context context;
    List<String> list;

    LayoutInflater layoutInflater;

    public SliderImageAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        int size = 0;
        if (list != null){
            size = list.size();
        }
        return size;
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_image_row_item, container, false);
        ImageView slide_imgView = view.findViewById(R.id.slider_image_view);
        if (position == 0){
            Glide.with(context).load(list.get(position)).into(slide_imgView);
        }
        Glide.with(context)
                .load(list.get(position)).into(slide_imgView);
        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }

}