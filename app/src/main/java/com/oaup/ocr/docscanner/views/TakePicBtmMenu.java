package com.oaup.ocr.docscanner.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oaup.ocr.docscanner.R;

/**
 * Created by jkx on 2015/8/5.
 */
public class TakePicBtmMenu extends ViewGroup {
    public TakePicBtmMenu(Context context,AttributeSet attrs){
        super(context,attrs);
    }

    private ImageView mTakeSetting;
    private ImageView mTakeCapture;
    private ImageView mTakeBatch;
    private ImageView mTakeSingle;

    private void initBar(){
        mTakeSetting = (ImageView)findViewById(R.id.takepic_setting);
        mTakeCapture = (ImageView)findViewById(R.id.takepic_capture);
        mTakeBatch = (ImageView)findViewById(R.id.takepic_batch);
        mTakeSingle = (ImageView)findViewById(R.id.takepic_single);

        if (mTakeCapture != null){
//            mTakeSetting.setImageResource(R.mipmap.ic_capture_settings);
//            mTakeCapture.setImageResource(R.mipmap.ic_capture_capure);
//            mTakeBatch.setImageResource(R.mipmap.ic_capture_batch_off);
//            mTakeSingle.setImageResource(R.mipmap.ic_capture_single_on);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        initBar();

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
        measureChildren(widthMeasureSpec, heightMeasureSpec);

//        setMeasuredDimension(width, mTakeCapture.getMeasuredHeight());

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b){
        int x_center = (r-l)/2;
        int width = r-l;
        int captureWidth = mTakeCapture.getMeasuredWidth();
        mTakeCapture.layout(x_center - captureWidth/2,
                0,
                x_center - captureWidth/2 + captureWidth,
                getMeasuredHeight());
        int tempWidth = x_center - captureWidth/2;

        int x_block = (tempWidth - mTakeBatch.getMeasuredWidth() * 2)/3;
        if (mTakeSingle.getVisibility() == View.INVISIBLE){
            x_block = (width - captureWidth * 3)/4;
        }

        int left = x_center + captureWidth/2 + x_block;
        int top = (getHeight()-mTakeBatch.getMeasuredHeight()) /2;

        mTakeSetting.layout(width-left-mTakeSetting.getMeasuredWidth(),
                top,
                width-left,
                top+mTakeSetting.getMeasuredHeight());

        mTakeBatch.layout(left,
                top,
                left + mTakeBatch.getMeasuredWidth(),
                top + mTakeBatch.getMeasuredHeight());
        left += mTakeBatch.getMeasuredWidth() + x_block;
        mTakeSingle.layout(left,
                top,
                left + mTakeSingle.getMeasuredWidth(),
                top + mTakeSingle.getMeasuredHeight());
    }

}
