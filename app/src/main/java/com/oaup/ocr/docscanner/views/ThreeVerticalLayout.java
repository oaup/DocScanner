package com.oaup.ocr.docscanner.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class ThreeVerticalLayout extends ViewGroup {

	public ThreeVerticalLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		View view0 = getChildAt(0);
		view0.measure(widthMeasureSpec, view0.getLayoutParams().height);

		View view2 = getChildAt(2);

		view2.measure(widthMeasureSpec, view2.getLayoutParams().height);

		View view1 = getChildAt(1);

		
		
		view1.measure(widthMeasureSpec,MeasureSpec.makeMeasureSpec( height - view0.getMeasuredHeight()
				- view2.getMeasuredHeight(), MeasureSpec.EXACTLY));

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		View view0 = getChildAt(0);
		view0.layout(0, 0, r - l, view0.getMeasuredHeight());

		View view2 = getChildAt(2);
		view2.layout(0, b - t - view2.getMeasuredHeight(), r - l, b - t);

		View view1 = getChildAt(1);
		view1.layout(0, view0.getBottom(), r - l, view2.getTop());

	}

}
