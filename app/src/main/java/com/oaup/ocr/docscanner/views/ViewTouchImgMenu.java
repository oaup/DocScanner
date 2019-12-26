package com.oaup.ocr.docscanner.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.oaup.ocr.docscanner.R;


/**
 * Created by jkx on 2015/8/7.
 */
public class ViewTouchImgMenu extends ViewGroup {
    public ViewTouchImgMenu(Context context,AttributeSet attrs){
        super(context,attrs);
    }

    private ImageView mImgBtnLeft;
    private ImageView mImgBtnRightOfLeft;
    private ImageView mImgBtnCenter;
    private ImageView mImgBtnLeftOfRight;
    private ImageView mImgBtnRight;
    private void initBar(){
        mImgBtnLeft = (ImageView)findViewById(R.id.touch_menu_left);
        mImgBtnRightOfLeft = (ImageView)findViewById(R.id.touch_menu_rightOfleft);
        mImgBtnCenter = (ImageView)findViewById(R.id.touch_menu_center);
        mImgBtnLeftOfRight = (ImageView)findViewById(R.id.touch_menu_leftOfright);
        mImgBtnRight = (ImageView)findViewById(R.id.touch_menu_right);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        initBar();

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(width, mImgBtnLeft.getMeasuredHeight());

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b){
        int width = r-l;
        int height = b-t;
        int x_block = (width - mImgBtnLeft.getMeasuredWidth()*5) / 6;

        int left = x_block;
        mImgBtnLeft.layout(left,
                0,
                left+mImgBtnLeft.getMeasuredWidth(),
                height);
        left += mImgBtnLeft.getMeasuredWidth() + x_block;
        mImgBtnRightOfLeft.layout(left,
                0,
                left + mImgBtnRightOfLeft.getMeasuredWidth(),
                height);

        left += mImgBtnLeft.getMeasuredWidth() + x_block;
        mImgBtnCenter.layout(left,
                0,
                left + mImgBtnCenter.getMeasuredWidth(),
                height);

        left += mImgBtnLeft.getMeasuredWidth() + x_block;
        mImgBtnLeftOfRight.layout(left,
                0,
                left+mImgBtnLeftOfRight.getMeasuredWidth(),
                height);

        left += mImgBtnLeft.getMeasuredWidth() + x_block;
        mImgBtnRight.layout(left,
                0,
                left+mImgBtnRight.getMeasuredWidth(),
                height);

    }
}
