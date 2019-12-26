package com.oaup.ocr.docscanner.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oaup.ocr.docscanner.R;

/**
 * Created by jkx on 2015/6/11.
 */
public class ImageText extends LinearLayout{
    private Context mContext = null;
    private ImageView  mImageView = null;
    private TextView   mTextView = null;
    private  static int DEFAULT_IMAGE_WIDTH = 64;
    private  static int DEFAULT_IMAGE_HEIGHT = 64;
    private int CHECKED_COLOR = Color.rgb(29,118,199);
    private int UNCHECKED_COLOR = Color.WHITE;
    public ImageText(Context context){
        super(context);
        mContext = context;
    }
    public ImageText(Context context, AttributeSet attrs){
        super(context,attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_img_text,this,true);
        mImageView = (ImageView)view.findViewById(R.id.image_image_text);
        mTextView = (TextView)view.findViewById(R.id.text_image_text);
    }

    public void setImage(int nId){
         if (mImageView != null){
             mImageView.setImageResource(nId);
             setImageSize(DEFAULT_IMAGE_WIDTH,DEFAULT_IMAGE_HEIGHT);
         }
    }

    public void setImage(Bitmap bmp){
        if (mImageView != null){
            mImageView.setImageBitmap(bmp);
            setImageSize(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
        }
    }

    public void setText(String s){
        if (mTextView!=null){
            mTextView.setText(s);
            mTextView.setTextColor(UNCHECKED_COLOR);
        }
    }

    public void setText(int resid){
        if (mTextView != null){
            mTextView.setText(resid);
            mTextView.setTextColor(UNCHECKED_COLOR);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        return true;
    }

    private void setImageSize(int w,int h){
        if (mImageView != null){
            ViewGroup.LayoutParams params = mImageView.getLayoutParams();
            params.width = w;
            params.height = h;
            mImageView.setLayoutParams(params);
        }
    }

    public void setDefaultImageWidth(int width){
        DEFAULT_IMAGE_WIDTH = width;
    }

    public void setDefaultImageHeight(int height){
        DEFAULT_IMAGE_HEIGHT = height;
    }

}
