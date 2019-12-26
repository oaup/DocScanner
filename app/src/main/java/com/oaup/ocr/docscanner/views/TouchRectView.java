package com.oaup.ocr.docscanner.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.oaup.ocr.common.NLog;
import com.oaup.ocr.docscanner.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TouchRectView extends TextView {

	Paint m_paint;
	PointType m_press_down = PointType.NONE;
    private  List<Point> mSrcPoints = new ArrayList<>();
	float m_percent;
	int m_src_width;
	int m_src_height;

	private Point[] m_pointsTemp = new Point[4];
    private String logs = "";
	private static final String FILEPATH = "/mnt/sdcard/camtest";
	private static final String FILENAME = "Log.txt";
	private String logTag = "TouchRectView";

	public TouchRectView(Context context){
		super(context);
		
	}
	
	public void setImageSize(int w,int h)
	{
		m_src_width = w;
		m_src_height = h;
	}
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		change_percent(w, h);
		setDrawPoint(mSrcPoints);

		setMeasuredDimension((int) (m_src_width * m_percent), (int) (m_src_height * m_percent));

		Log.i(logTag, String.format("onMeasure w:%d h:%d ", getMeasuredWidth(), getMeasuredHeight()));

	}
	
	private void change_percent(int w, int h) {
		NLog.i("pic point: change_percent w:%d h:%d src_w:%d src_h:%d", w, h, m_src_width, m_src_height);
		if( m_src_width == 0 || w == 0)
			return;
		
		if( w > m_src_width || h > m_src_height )
		{
			if( w*1.0/m_src_width > h*1.0f/m_src_height )
				m_percent = h*1.0f/m_src_height;
			else
				m_percent = w*1.0f/m_src_width;
		}
		else
		{
			if( m_src_width*1.0/w > m_src_height*1.0f/h )
				m_percent = w*1.0f/m_src_width;
			else
				m_percent = h*1.0f/m_src_height;
		}
	}

	public TouchRectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		m_paint = new Paint();
		m_paint.setStyle(Style.STROKE);
		m_paint.setStrokeWidth(5);
		
		m_paint.setColor(Color.GREEN);
		m_paint.setAntiAlias(true);
	}
	
	public void initPoint(final List<Point> points){
		if (points == null){
			return;
		}
		sort_point(points);
		mSrcPoints = points;

		if (((m_percent-0.0)>0)){
			setDrawPoint(mSrcPoints);
		}
	}

	private void setDrawPoint(final List<Point> points){
		for (int i = 0;i<points.size();i++){
			Point ptTemp = new Point();
			ptTemp.set(points.get(i).x, points.get(i).y);

			ptTemp.x =(int)(ptTemp.x*m_percent);
			ptTemp.y=(int)(ptTemp.y*m_percent);
			setPoint(ptTemp,i);
		}
	}

	private void convert_point(List<Point> points) {

		if( m_percent == 0 )
			return;
		
		NLog.i("pic point(getwidth:%s,src_width:%s)", getWidth()+"",m_src_width*m_percent+"");
		for( Point p:points)
		{
			p.x = (int) (p.x * m_percent +  (1080-m_src_width * m_percent)*1f/2);
			p.y = (int) (p.y * m_percent +  (1080-m_src_height * m_percent)*1f/2);
			
			/*p.x = (int) (p.x * m_percent);
			p.y = (int) (p.y * m_percent);*/
			
		}
		
		for(Point p:points)
			NLog.i("pic point convert->:%s", p.toString());
	}
	private void sort_point(List<Point> points)
	{
		if( points == null )
			return;

		Collections.sort(points, new Comparator<Point>() {

			@Override
			public int compare(Point rhs, Point lhs) {
				return rhs.x - lhs.x;
			}
		});
		
		if( points.get(0).y > points.get(1).y )
		{
			Point p0 = points.get(0);
			Point p1 = points.get(1);
			Point p3 = points.get(3);
			points.set(0, p1);
			points.set(3, p0);
			points.set(1,p3);
			
		}
		else
		{
			Point p1 = points.get(1);
			Point p3 = points.get(3);
			points.set(3, p1);
			points.set(1,p3);
			
		}
		
		if( points.get(1).y > points.get(2).y )
		{
			Point p1 = points.get(1);
			Point p2 = points.get(2);
			points.set(2, p1);
			points.set(1,p2);
		}
		for(Point p:points)
			NLog.i("pic point sord->:%s", p.toString());
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if( m_pointsTemp[0] != null)
		{
			Rect r = new Rect(0,0,getWidth(),getHeight());
			canvas.clipRect(r);
			Path path = new Path();
			path.lineTo(m_pointsTemp[PointType.LEFT_TOP.ordinal()].x,m_pointsTemp[PointType.LEFT_TOP.ordinal()].y);
			path.lineTo(m_pointsTemp[PointType.RIGHT_TOP.ordinal()].x, m_pointsTemp[PointType.RIGHT_TOP.ordinal()].y);
			path.lineTo(m_pointsTemp[PointType.RIGHT_BOTTOM.ordinal()].x,m_pointsTemp[PointType.RIGHT_BOTTOM.ordinal()].y);
			path.lineTo(m_pointsTemp[PointType.LEFT_BOTTOM.ordinal()].x,m_pointsTemp[PointType.LEFT_BOTTOM.ordinal()].y);
			path.lineTo(m_pointsTemp[PointType.LEFT_TOP.ordinal()].x, m_pointsTemp[PointType.LEFT_TOP.ordinal()].y);

			for(Point p:m_pointsTemp)
			{
				path.addCircle(p.x, p.y, m_point_off, Direction.CW);
			}
			canvas.clipPath(path, Region.Op.INTERSECT);

			m_paint.setStyle(Style.STROKE);
			m_paint.setColor(0x33ffffff);
			//m_paint.setColor(Color.TRANSPARENT);
			canvas.drawRect(r, m_paint);
			drawLine(canvas);
			drawPoint(canvas);
		}
		
	}
	
	
	float m_point_last_x;
	float m_point_last_y;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch( event.getAction() )
		{
			case MotionEvent.ACTION_DOWN:
			{
				m_point_last_x = event.getX();
				m_point_last_y = event.getY();
				
				m_press_down = is_press_in_point(event.getX(), event.getY());
				if( m_press_down == PointType.NONE )
				{
					return false;
				}
				else
				{
					NLog.i("onTouch : down %s",m_press_down.toString());
					return true;
				}
			}	
				
			case MotionEvent.ACTION_MOVE:
			{
				if( m_press_down != PointType.NONE )
				{
					m_pointsTemp[m_press_down.ordinal()].x = (int)event.getX();//event.getX() -  m_point_last_x;
					m_pointsTemp[m_press_down.ordinal()].y = (int)event.getY();//event.getY() -  m_point_last_y;
					
					m_point_last_x = event.getX();
					m_point_last_y = event.getY();

					invalidate();
					Point p = new Point((int)m_point_last_x,(int)m_point_last_y);
					if(m_percent >0.0){
						p.x = (int) ((p.x - (getWidth()-m_src_width * m_percent)/2)/m_percent);
						p.y =  (int) ((p.y - (getHeight()-m_src_height * m_percent)/2)/m_percent);
					}
					
					int off = 50;
					touchMoveListener.move(new Rect(
							    p.x-off,
								p.y-off,
								p.x+off,
								p.y+off
							));
				}
				break;
			}
			case MotionEvent.ACTION_UP:
				m_press_down = PointType.NONE;
				invalidate();
				touchMoveListener.moveup();
				break;
		}

		return true;
	}

	//int m_point_off = R.dimen.touchRectView_point_in;
	int m_point_off = 30;

	private TouchMoveListener touchMoveListener;
	private PointType is_press_in_point(float x,float y)
	{
		if( m_pointsTemp[0] == null)
			return PointType.NONE;
		for(int i=0;i<m_pointsTemp.length;i++)
		{
			Point p = m_pointsTemp[i];
			if( x >= p.x-m_point_off
					&& x<= p.x+m_point_off
					&& y>= p.y-m_point_off
					&& y<= p.y+m_point_off )
				return PointType.values()[i];
		}
		return PointType.NONE;
	}
	
	private void drawPoint(Canvas canvas)
	{
		m_paint.setStyle(Style.STROKE);
		m_paint.setStrokeWidth(3);
		int i=0;
		for(Point p:m_pointsTemp)
		{
			if( m_press_down.ordinal() == i++ )
				m_paint.setColor(Color.YELLOW);
			else
				m_paint.setColor(getResources().getColor(R.color.white_transparent));
			
			canvas.drawCircle( p.x,  p.y, m_point_off, m_paint);
			Log.i(logTag, String.format("drawPoint：%d,%d",p.x,p.y));
			//canvas.drawRect(new Rect( p.x-m_point_off,p.y-m_point_off,p.x+m_point_off,p.y+m_point_off ), m_paint);
		}
		
	}
	private void drawLine(Canvas canvas)
	{
		m_paint.setStrokeWidth(3);
		m_paint.setColor(getResources().getColor(R.color.white_transparent));
		int nSize = m_pointsTemp.length;
		for (int i = 0;i<nSize;i++){
			Point pt1 = m_pointsTemp[(i % nSize)];
			Point pt2 = m_pointsTemp[(i+1)%nSize];
			canvas.drawLine(pt1.x,pt1.y,pt2.x,pt2.y,m_paint);

			logs = String.format("time:%d", System.currentTimeMillis()) + String.format("drawLine:%d,%d", pt1.x, pt1.y);
			//LogFile.getInstanceLog().writeTxtToFile(logs, FILEPATH, FILENAME);
		}
	}
	
	public void setPoint(Point p,int i)
	{
		m_pointsTemp[i] = p;
	}
	
	public static enum PointType
	{
		LEFT_TOP,
		RIGHT_TOP,
		RIGHT_BOTTOM,
		LEFT_BOTTOM,
		NONE,
	}

	public List<Point> getPoints() {
		int nSize = m_pointsTemp.length;//list.size();
		List<Point> result = new ArrayList<Point>();
		if (m_percent >0.0){
			mSrcPoints.clear();
			for (int i = 0;i<nSize;i++){
				Point ptTemp = new Point();
				ptTemp.x = (int)(m_pointsTemp[i].x/m_percent);
				ptTemp.y=(int)(m_pointsTemp[i].y/m_percent);
				result.add(ptTemp);
				mSrcPoints.add(ptTemp);
			}
		}
		return result;
	}

	public static interface TouchMoveListener{
		
		public void move(Rect rect);
		public void moveup();
	}


	public void setTouchMoveListener(TouchMoveListener touchMoveListener) {
		this.touchMoveListener = touchMoveListener;
	}

	// add by jkx 2015/08/26
}
