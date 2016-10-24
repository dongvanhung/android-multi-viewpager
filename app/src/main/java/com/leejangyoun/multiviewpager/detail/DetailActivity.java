package com.leejangyoun.multiviewpager.detail;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.leejangyoun.multiviewpager.Fruit;
import com.leejangyoun.multiviewpager.R;
import com.leejangyoun.multiviewpager.blur.BlurBehind;
import com.pixplicity.multiviewpager.MultiViewPager;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    public static final String INTENT_NO = "INTENT_NO";

    private Context mContext;

    ArrayList<Fruit> mArr;
    MultiViewPager mPager;
    DetailPagerAdapter mAdapter;

    private RequestQueue mQueue;

    private int mPage = 1;
    private boolean mIsLastItem = false;

    private int mIntentNo;

    // =======================================================================
    // METHOD : onCreate
    // =======================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, 0);
        setContentView(R.layout.activity_detail);

        mContext = getApplicationContext();

        BlurBehind.getInstance()
                .withAlpha(90)
                .withFilterColor(Color.parseColor("#000000"))
                .setBackground(DetailActivity.this);


        mIntentNo = getIntent().getExtras().getInt(INTENT_NO);

        mArr = new ArrayList();
        mArr.add(new Fruit(Fruit.TYPE.MORE.PROGRESS));
        mPager = (MultiViewPager) findViewById(R.id.pager);
        mAdapter = new DetailPagerAdapter(mContext, mArr);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("TEST", "onPageSelected : " + position);

                if (position == (mArr.size() - 1)) {

                    if (mIsLastItem)
                        return;

                    //if sending all card, must get more card by http protocol
                    String url = "http://leejangyoun.com/android/dummy/MultiViewPagerArr_" + (++mPage) + ".json";

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new CustomSuccessListener(), new CustomErrorListener());
                    mQueue.add(stringRequest);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        findViewById(R.id.btn_qna_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "WRITE button click", Toast.LENGTH_SHORT).show();
            }
        });

        //set http queue
        mQueue = Volley.newRequestQueue(mContext);
        String url = "http://leejangyoun.com/android/dummy/MultiViewPagerArr_1.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new CustomSuccessListener(), new CustomErrorListener());
        mQueue.add(stringRequest);

    }

    // =======================================================================
    // METHOD : finish
    // =======================================================================
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
    }

    // =======================================================================
    // METHOD : CustomSuccessListener
    // =======================================================================
    private  class CustomSuccessListener implements Response.Listener<String> {
        @Override
        public void onResponse(String response) {

            for (Fruit f : mArr) {
                if(f.getType() == Fruit.TYPE.PROGRESS) {
                    mArr.remove(f);
                    break;
                }
            }

            Gson gson = new Gson();

            JsonObject root = new JsonParser().parse(response).getAsJsonObject();

            mIsLastItem = root.get("last").getAsBoolean();

            JsonArray node = root.get("arrList").getAsJsonArray();
            for (JsonElement jEle : node) {
                Fruit fruit = gson.fromJson(jEle.getAsJsonObject(), Fruit.class);
                fruit.setType(Fruit.TYPE.ITEM);
                mArr.add(fruit);
            }

            if ( ! mIsLastItem)
                mArr.add(new Fruit(Fruit.TYPE.PROGRESS));

            mAdapter.notifyDataSetChanged();

            // move selected card.
            if (mPage == 1 && mIntentNo != -1) {
                for (Fruit f : mArr) {
                    if(f.getNo() == mIntentNo) {
                        new ChangePosition().execute(mArr.indexOf(f));
                        break;
                    }
                }
            }
        }
    }


    // =======================================================================
    // METHOD : ChangePosition
    // =======================================================================
    private class ChangePosition extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            try { Thread.sleep(100); } catch (InterruptedException e) { }
            return params[0];

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mPager.setCurrentItem(integer);
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
