package com.leejangyoun.multiviewpager;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.leejangyoun.multiviewpager.blur.BlurBehind;
import com.leejangyoun.multiviewpager.blur.OnBlurCompleteListener;
import com.leejangyoun.multiviewpager.detail.DetailActivity;
import com.pixplicity.multiviewpager.MultiViewPager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    private ArrayList<Fruit> mAnswerArr;
    private FruitPagerAdapter mFruitAdapter;

    private RequestQueue mQueue;

    boolean mIsShowingBottomView = false;

    View mBottomLayout;

    // =======================================================================
    // METHOD : onCreate
    // =======================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        MultiViewPager pager = (MultiViewPager) findViewById(R.id.pager);
        mAnswerArr = new ArrayList<>();
        mFruitAdapter = new FruitPagerAdapter(mContext, mAnswerArr);
        pager.setAdapter(mFruitAdapter);
        mFruitAdapter.setListener(new FruitPagerAdapter.FruitPagerAdapterListener() {
            @Override
            public void onClick(int fruitNo) {
                hideAndGotoAnswerActivity(fruitNo);
            }
        });

        //set bottom layout
        mBottomLayout = findViewById(R.id.layout_bottom);

        //set http queue
        mQueue = Volley.newRequestQueue(mContext);
        String url = "http://leejangyoun.com/android/dummy/MultiViewPager.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new CustomSuccessListener(), new CustomErrorListener());
        mQueue.add(stringRequest);
    }

    //=======================================================================
    // METHOD : onResume
    //=======================================================================
    @Override
    protected void onResume() {
        super.onResume();
        if(mBottomLayout.getVisibility() != View.VISIBLE) {
            Animation bottomDown = AnimationUtils.loadAnimation(mContext, R.anim.bottom_down);
            mBottomLayout.startAnimation(bottomDown);
            mBottomLayout.setVisibility(View.VISIBLE);

            mIsShowingBottomView = false;
        }
    }

    // =======================================================================
    // METHOD : hideAndGotoAnswerActivity
    // =======================================================================
    private void hideAndGotoAnswerActivity(final int fruitNo) {

        if (mIsShowingBottomView)
            return;

        mIsShowingBottomView = true;

        Animation bottomUp = AnimationUtils.loadAnimation(mContext, R.anim.bottom_up);
        mBottomLayout.startAnimation(bottomUp);
        mBottomLayout.setVisibility(View.GONE);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                BlurBehind.getInstance().execute(MainActivity.this, new OnBlurCompleteListener() {
                    @Override
                    public void onBlurComplete() {
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra(DetailActivity.INTENT_NO, fruitNo);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                });
            }
        }, 500);
    }
    // =======================================================================
    // METHOD : CustomSuccessListener
    // =======================================================================
    private  class CustomSuccessListener implements Response.Listener<String> {
        @Override
        public void onResponse(String response) {

            Gson gson = new Gson();

            JsonObject root = new JsonParser().parse(response).getAsJsonObject();

            JsonArray node = root.get("arrList").getAsJsonArray();
            for (JsonElement jEle : node) {
                Fruit fruit = gson.fromJson(jEle.getAsJsonObject(), Fruit.class);
                fruit.setType(Fruit.TYPE.ITEM);
                mAnswerArr.add(fruit);
            }

            // add temp var for more View
            mAnswerArr.add(new Fruit(Fruit.TYPE.MORE));

            mFruitAdapter.notifyDataSetChanged();
        }
    }

    // =======================================================================
    // METHOD : CustomErrorListener
    // =======================================================================
    private  class CustomErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("TEST", "error : " + error.getMessage());
        }
    }
}
