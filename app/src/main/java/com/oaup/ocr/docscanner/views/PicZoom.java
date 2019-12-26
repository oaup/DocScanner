package com.oaup.ocr.docscanner.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import com.oaup.ocr.common.UIUtil;

public class PicZoom extends View {

	Bitmap m_bitmap;
	Rect m_rect;
	Paint m_paint;
	
	public PicZoom(Context context, AttributeSet attrs) {
		super(context, attrs);
		m_paint = new Paint();
		m_paint.setStyle(Style.FILL);
		m_paint.setStrokeWidth(1);
		m_paint.setColor(Color.GREEN);
		m_paint.setAntiAlias(true);  
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int width = (int) (UIUtil.width/4);
		setMeasuredDimension(width,width);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		
		Path path = new Path();
		
		path.addCircle(getWidth()/2,getHeight()/2,getHeight()/2, Direction.CW);
		
		canvas.clipPath(path, Region.Op.REPLACE);
		
		
		
		Rect self_rect = new Rect(0,0,getWidth(),getHeight());
		
		m_paint.setColor(Color.BLACK);
		m_paint.setStyle(Style.FILL);
		canvas.drawRect(self_rect, m_paint);
		
		m_paint.setStrokeWidth(3);
		m_paint.setStyle(Style.STROKE);
		m_paint.setColor(Color.WHITE);
		canvas.drawCircle(getWidth()/2,getHeight()/2,getHeight()/2-3, m_paint);
		
		if( m_bitmap != null )
		{
			canvas.drawBitmap(m_bitmap, m_rect,self_rect,null);
		}
		
		int p_x = getWidth()/2;
		int p_y = getWidth()/2;
		int off = 6;
		
		
		m_paint.setStrokeWidth(1);
		m_paint.setColor(Color.GREEN);
		canvas.drawLine(p_x-off, p_y-off,p_x+off,p_y+off,m_paint);
		canvas.drawLine(p_x-off, p_y+off,p_x+off,p_y-off,m_paint);
		
		
	}
	
	public void setImage(Bitmap bitmap,Rect rect)
	{
		m_bitmap = bitmap;
		m_rect = rect;
		invalidate();
	}
	
	

}
