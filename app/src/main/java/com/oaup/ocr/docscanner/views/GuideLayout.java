package com.oaup.ocr.docscanner.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.oaup.ocr.docscanner.R;

public class GuideLayout extends ViewGroup{

	public GuideLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		View img = findViewById(R.id.img_guide);
		img.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
				, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		View img = findViewById(R.id.img_guide);
		img.layout(0, 0, img.getMeasuredWidth(), getHeight());
		
	}

}
