package com.leejangyoun.multiviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FruitPagerAdapter extends PagerAdapter {

    Context mContext;
    ArrayList<Fruit> mArr;

    //=======================================================================
    // METHOD : QnaDetailPagerAdapter
    //=======================================================================
    public FruitPagerAdapter(Context context, ArrayList<Fruit> arr) {
        this.mContext = context;
        this.mArr = arr;
    }

    //=======================================================================
    // METHOD : instantiateItem
    //=======================================================================
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater alertInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = alertInflater.inflate(R.layout.multipager_cell, null);

        final Fruit fruit = mArr.get(position);

        if(fruit.getType() == Fruit.TYPE.ITEM) {
            view.findViewById(R.id.moreView).setVisibility(View.GONE);
            view.findViewById(R.id.cellView).setVisibility(View.VISIBLE);
            Glide.with(mContext).load(mArr.get(position).getThumb()).into((ImageView) view.findViewById(R.id.img_profile));
            ((TextView) view.findViewById(R.id.txt_name)).setText(mArr.get(position).getTitle());
            ((TextView) view.findViewById(R.id.txt_content)).setText(mArr.get(position).getDesc());

        } else if (fruit.getType() == Fruit.TYPE.MORE) {
            view.findViewById(R.id.moreView).setVisibility(View.VISIBLE);
            view.findViewById(R.id.cellView).setVisibility(View.GONE);

        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) listener.onClick(fruit.getNo());
            }
        });

        container.addView(view);
        return view;
    }

    //=======================================================================
    // METHOD : destroyItem
    //=======================================================================
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    //=======================================================================
    // METHOD : getCount
    //=======================================================================
    @Override
    public int getCount() {
        return mArr.size();
    }

    //=======================================================================
    // METHOD : isViewFromObject
    //=======================================================================
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    //=======================================================================
    // METHOD : setListener
    //=======================================================================
    FruitPagerAdapterListener listener;
    public void setListener(FruitPagerAdapterListener listener){
        this.listener = listener;
    }
    public interface FruitPagerAdapterListener {
        void onClick(int no);
    }
}
