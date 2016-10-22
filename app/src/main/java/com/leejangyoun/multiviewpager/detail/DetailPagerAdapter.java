package com.leejangyoun.multiviewpager.detail;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leejangyoun.multiviewpager.Fruit;
import com.leejangyoun.multiviewpager.R;

import java.util.ArrayList;
import java.util.Random;

public class DetailPagerAdapter extends PagerAdapter {

    Context mContext;
    ArrayList<Fruit> mArr;
    LayoutInflater mLayoutInflater;

    // =======================================================================
    // METHOD : AnswerPagerAdapter
    // =======================================================================
    public DetailPagerAdapter(Context context, ArrayList<Fruit> arr) {
        mContext = context;
        mArr = arr;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // =======================================================================
    // METHOD : getItemPosition
    // =======================================================================
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    // =======================================================================
    // METHOD : instantiateItem
    // =======================================================================
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mLayoutInflater.inflate(R.layout.multipager_detail_cell, null);

        Fruit fruit = mArr.get(position);

        if(fruit.getType() == Fruit.TYPE.PROGRESS) {
            view.findViewById(R.id.content_layout).setVisibility(View.GONE);
            view.findViewById(R.id.loading_layout).setVisibility(View.VISIBLE);

        } else {
            view.findViewById(R.id.content_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.loading_layout).setVisibility(View.GONE);

            ((TextView) view.findViewById(R.id.txt_desc)).setText(fruit.getDesc());
            ((TextView) view.findViewById(R.id.txt_fruit_name)).setText(fruit.getTitle());
            ((TextView) view.findViewById(R.id.txt_fruit_no)).setText("FRUIT NO : " + fruit.getNo());

            ((TextView) view.findViewById(R.id.txt_comment_cnt)).setText((100 + new Random().nextInt(99)) + "");
            ((TextView) view.findViewById(R.id.txt_like_cnt)).setText((100 + new Random().nextInt(99)) + "");

            ImageView profile = (ImageView) view.findViewById(R.id.img_profile);
            Glide.with(mContext).load(fruit.getThumb()).into(profile);

            ImageView contentThumb = (ImageView) view.findViewById(R.id.content_thumb);
            Glide.with(mContext).load(fruit.getThumb()).into(contentThumb);
        }


        /*
        view.findViewById(R.id.layout_main).setVisibility(answer.itemType == Answer.ITEM_TYPE.NORMAL ? View.VISIBLE : View.GONE);

        Glide.with(mContext)
                .load(answer.profileImg)
                .into((ImageView) view.findViewById(R.id.img_profile));

        ((TextView) view.findViewById(R.id.txt_user_names)).setText(answer.userName);
        ((TextView) view.findViewById(R.id.txt_issue_date)).setText(Common.timeAgoToWords(answer.issueDate));
        ((TextView) view.findViewById(R.id.txt_content   )).setText(answer.answerContent);

        final ImageView imgLike = (ImageView) view.findViewById(R.id.img_like);
        imgLike.setSelected(answer.like ? true : false);

        final ImageView imgAdopt = (ImageView) view.findViewById(R.id.img_adopt_answer);
        imgAdopt.setSelected(answer.chosen ? true : false);

        final CustomTextView txtLike = (CustomTextView) view.findViewById(R.id.txt_like);
        txtLike.setText(answer.likeCount);

        view.findViewById(R.id.btn_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SingletonData.getInstance().getLoginStatus())
                    new LikeAnswerAsync(mContext, mQuestionId, answer.answerNo, imgLike, txtLike, imgLike.isSelected()).execute();
                else
                    CustomToast.showToastMessage(mContext, "좋아요는 로그인 후에 남기실 수 있어요.");
            }
        });

        view.findViewById(R.id.btn_adopt_answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(! mIsQuestionOwn) {
                    CustomToast.showToastMessage(mContext, "답변 채택은 질문자만 진행할 수 있습니다.");
                    return;
                }

                if(! mIsAvailableAdopt) {
                    CustomToast.showToastMessage(mContext, "이미 채택된 답변 입니다.");
                    return;
                }

                if(mIsAvailableAdopt) {
                    ALog.i("btnAdoptAnswer > mIsAvailableAdopt : " + mIsAvailableAdopt);
                    DialogLayout dialogLayout = new DialogLayout(mActivity);
                    dialogLayout.showTwoBtn(true, "답변 채택하기", "답변은 1개만 채택할 수 있으며, 이후에 변경할 수 없습니다.\n\n이 답변을 채택 하시겠습니까?", "네, 채택합니다", "아니오");
                    dialogLayout.setListener(new DialogLayout.DialogLayoutListener() {
                        @Override
                        public void onConfirm() {
                            new AdoptAnswerAsync(mContext, mQuestionId, answer.answerNo, imgAdopt, imgAdopt.isSelected()).execute();
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }

            }
        });


        //메뉴 스피너 셋팅
        final CustomSpinner mSpinner = (CustomSpinner) view.findViewById(R.id.spinner);
        final String[] mSpinnerStrList = answer.own ? (new String [] {"좋아요", "삭제하기", "수정하기"}) : (new String [] {"좋아요", "신고하기"});
        ArrayAdapter<String> dataAdapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, Arrays.asList(mSpinnerStrList));
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner_cell_item);
        mSpinner.setListener(new CustomSpinner.OnItemListener() {
            @Override
            public void onItemSelected(int position) {
                if(mSpinnerStrList[position].equals("좋아요")) {
                    if(SingletonData.getInstance().getLoginStatus())
                        new LikeAnswerAsync(mContext, mQuestionId, answer.answerNo, imgLike, txtLike, imgLike.isSelected()).execute();
                    else
                        CustomToast.showToastMessage(mContext, "좋아요는 로그인 후에 남기실 수 있어요.");


                } else if(mSpinnerStrList[position].equals("신고하기")) {
                    if( ! SingletonData.getInstance().getLoginStatus()){
                        CustomToast.showToastMessage(mContext, "신고하기는 로그인 후에 남기실 수 있어요.");
                        return;
                    }

                    if(listener != null) listener.onReport(mQuestionId, answer.answerNo);


                } else if(mSpinnerStrList[position].equals("삭제하기")) {
                    if(listener != null) listener.onDelete(mQuestionId, answer.answerNo);

                } else if(mSpinnerStrList[position].equals("수정하기")) {
                    if(answer.chosen) {
                        CustomToast.showToastMessage(mContext, "채택한 답변이기 때문에 수정할 수 없습니다.");
                        return;
                    }

                    ALog.i("CustomSpinner > modify : " + answer.answerContent);
                    ArrayList<PhotoFileItem> tempFileArr = new ArrayList();
                    for (int i = 0; i < answer.getFileArrSize(); i++)
                        tempFileArr.add(answer.getFileArr(i));

                    if(listener != null) listener.onModify(
                            mQuestionId,
                            answer.answerNo,
                            answer.answerContent,
                            answer.metaUrl,
                            tempFileArr);
                }
            }
        });
        mSpinner.setAdapter(dataAdapter);


        view.findViewById(R.id.btn_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpinner.performClick();
            }
        });


        // 이미지 미리 보기 셋팅
        ImageView[] imgArr = new ImageView[10];
        imgArr[0] = (ImageView) view.findViewById(R.id.img_thumb0);
        imgArr[1] = (ImageView) view.findViewById(R.id.img_thumb1);
        imgArr[2] = (ImageView) view.findViewById(R.id.img_thumb2);
        imgArr[3] = (ImageView) view.findViewById(R.id.img_thumb3);
        imgArr[4] = (ImageView) view.findViewById(R.id.img_thumb4);
        imgArr[5] = (ImageView) view.findViewById(R.id.img_thumb5);
        imgArr[6] = (ImageView) view.findViewById(R.id.img_thumb6);
        imgArr[7] = (ImageView) view.findViewById(R.id.img_thumb7);
        imgArr[8] = (ImageView) view.findViewById(R.id.img_thumb8);
        imgArr[9] = (ImageView) view.findViewById(R.id.img_thumb9);
        for (int i = 0; i < 10; i++) {
            if (i < answer.getFileArrSize()) {
                imgArr[i].setVisibility(View.VISIBLE);
                String imgUrl = answer.getFileArr(i).fileUrl;
                if (imgUrl != null && (! imgUrl.equals(""))) {
                    Glide.with(mContext).load(imgUrl).into(imgArr[i]);
                    imgArr[i].setOnClickListener(new CustomImgClick(position, imgUrl));
                }
            } else {
                imgArr[i].setVisibility(View.GONE);
            }
        }

        // MetaUrl 셋팅
        if(answer.metaUrl == null || answer.metaUrl.url==null || answer.metaUrl.url.equals("") || answer.metaUrl.url.equals("null")) {

        } else {
            ALog.i("answer.metaUrl.url : " + answer.metaUrl.url);
            ((CustomTextView) view.findViewById(R.id.txt_meta_title)).setText(answer.metaUrl.title);
            ((CustomTextView) view.findViewById(R.id.txt_meta_desc )).setText(answer.metaUrl.description);
            ((CustomTextView) view.findViewById(R.id.txt_meta_url  )).setText(answer.metaUrl.url);
            Glide.with(mContext).load(answer.metaUrl.image).into((ImageView)view.findViewById(R.id.img_meta_image));
            View metaView = view.findViewById(R.id.layout_meta_url);
            metaView.setVisibility(View.VISIBLE);
            metaView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) listener.onClickMetaUrl(answer.metaUrl.url);
                }
            });
        }
        */
        container.addView(view);
        return view;
    }

    // =======================================================================
    // METHOD : destroyItem
    // =======================================================================
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    // =======================================================================
    // METHOD : getCount
    // =======================================================================
    @Override
    public int getCount() {
        return mArr.size();
    }

    // =======================================================================
    // METHOD : isViewFromObject
    // =======================================================================
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }
}
