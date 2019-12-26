package com.oaup.ocr.docscanner.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.oaup.ocr.docscanner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkx on 2015/8/11.
 */
public class HScrollImgPreMenu extends ViewGroup {
    public HScrollImgPreMenu(Context context,AttributeSet attrs){
        super(context,attrs);
        mImageViews = new ArrayList<>(6);
    }

    private ImageText mLeft0;
    private ImageText mLeft1;
    private ImageText mLeft2;
    private ImageText mLeft3;
    private ImageText mLeft4;
    private ImageText mLeft5;
    private List<ImageText> mImageViews;
    private  void initBar(){
        mImageViews.clear();
        mLeft0 = (ImageText)findViewById(R.id.hScrollLeft0);
        mImageViews.add(mLeft0);
        mLeft1 = (ImageText)findViewById(R.id.hScrollLeft1);
        mImageViews.add(mLeft1);
        mLeft2 = (ImageText)findViewById(R.id.hScrollLeft2);
        mImageViews.add(mLeft2);
        mLeft3 = (ImageText)findViewById(R.id.hScrollLeft3);
        mImageViews.add(mLeft3);
        mLeft4 = (ImageText)findViewById(R.id.hScrollLeft4);
        mImageViews.add(mLeft4);
        mLeft5 = (ImageText)findViewById(R.id.hScrollLeft5);
        mImageViews.add(mLeft5);
        if(mLeft0 != null){
//            ((ImageView)mLeft0.findViewById(R.id.image_image_text)).setImageResource(
//                    R.mipmap.enhance_recommend_hl);
            ((TextView)mLeft0.findViewById(R.id.text_image_text)).setText(
                    getContext().getString(R.string.enhance_auto));
//            ((ImageView)mLeft1.findViewById(R.id.image_image_text)).setImageResource(
//                    R.mipmap.enhance_orig);
            ((TextView)mLeft1.findViewById(R.id.text_image_text)).setText(
                    getContext().getString(R.string.enhance_ori));

//            ((ImageView)mLeft2.findViewById(R.id.image_image_text)).setImageResource(
//                    R.mipmap.enhance_low);
            ((TextView)mLeft2.findViewById(R.id.text_image_text)).setText(
                    getContext().getString(R.string.enhance));

//            ((ImageView)mLeft3.findViewById(R.id.image_image_text)).setImageResource(
//                    R.mipmap.enhance_high);
            ((TextView)mLeft3.findViewById(R.id.text_image_text)).setText(
                    getContext().getString(R.string.enhance_high));
//            ((ImageView)mLeft4.findViewById(R.id.image_image_text)).setImageResource(
//                    R.mipmap.enhance_gray);
            ((TextView)mLeft4.findViewById(R.id.text_image_text)).setText(
                    getContext().getString(R.string.enhance_gray));
//            ((ImageView)mLeft5.findViewById(R.id.image_image_text)).setImageResource(
//                    R.mipmap.enhance_bw);
            ((TextView)mLeft5.findViewById(R.id.text_image_text)).setText(
                    getContext().getString(R.string.enhance_bw));
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initBar();
        Log.i("initBar", "succession");
        int nSize = mImageViews.size();
        int nWidth=mLeft0.getMeasuredWidth()/2;
        for (int i =0;i<nSize;i++){
            ((View)mImageViews.get(i)).measure(mImageViews.get(i).getMeasuredWidth(),
                    mImageViews.get(i).getLayoutParams().height);
            nWidth += mImageViews.get(i).getMeasuredWidth();
        }
        nWidth += mLeft0.getMeasuredWidth() * 5 + mLeft0.getMeasuredWidth()/2;
        setMeasuredDimension(nWidth,mLeft0.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b){
        int left=mLeft0.getMeasuredWidth()/2;
        int x_block = left*2;
        int nSize = mImageViews.size();
        for (int i = 0;i<nSize;i++){
            mImageViews.get(i).layout(left,
                    0,
                    left+mImageViews.get(i).getMeasuredWidth(),
                    getHeight()
            );
            Log.i("TAG:", "width = " + mImageViews.get(i).getMeasuredWidth() + "height = " + mImageViews.get(i).getMeasuredHeight());
            left += (mImageViews.get(i)).getMeasuredWidth() + x_block;
        }
    }
}
