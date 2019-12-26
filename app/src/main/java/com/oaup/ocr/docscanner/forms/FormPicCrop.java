package com.oaup.ocr.docscanner.forms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.oaup.ocr.common.Event;
import com.oaup.ocr.common.Form;
import com.oaup.ocr.common.FormType;
import com.oaup.ocr.docscanner.MainActivity;
import com.oaup.ocr.docscanner.R;
import com.oaup.ocr.docscanner.views.PicZoom;
import com.oaup.ocr.docscanner.views.TouchRectView;
import com.oaup.ocr.imgprocess.image.Preprocess;

import java.io.CharArrayReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkx on 2015/8/10.
 */
public class FormPicCrop extends Form {
    View mView;
    Context mContext;
    private FrameLayout mFrameLayout;
    private ImageView mImageView;
    private TouchRectView mTRView;
    private Preprocess mPreprocess;
    private List<Point> mPoints;
    private PicZoom mPicZoom;
    private Bitmap mSrcBitmap;
    private Bitmap mCutBitmap;
    private ImageView mIvLanguage;
    private final static String logTag = "FormPicCrop";
    @Override
    public void onCreate(Context context, Object value) {
        this.mContext = context;
        mSrcBitmap = (Bitmap)value;
        mView = LayoutInflater.from(mContext).inflate(R.layout.form_detectpoint, null);
        //mViewTouchImgMenu = (ViewTouchImgMenu)mView.findViewById(R.id.layImgTouchMenu);
        mFrameLayout = (FrameLayout)mView.findViewById(R.id.layImgTouchPreview);
        mIvLanguage = (ImageView)mView.findViewById(R.id.iv_language);
        //mProgressBar = (ProgressBar)mFrameLayout.findViewById(R.id.picCircleProgress);
        mImageView = (ImageView)mFrameLayout.findViewById(R.id.ImgTouchPreview);
        mImageView.setImageBitmap(mSrcBitmap);
        mPicZoom = (PicZoom)mFrameLayout.findViewById(R.id.picEditPicZoom);
        mTRView = (TouchRectView)mFrameLayout.findViewById(R.id.picEditTouchRect);
        if(mTRView != null && null != mSrcBitmap){
            mTRView.setImageSize(mSrcBitmap.getWidth(), mSrcBitmap.getHeight());
            mTRView.setVisibility(View.INVISIBLE);
        }
        mPreprocess = ((MainActivity)mContext).getPreprocessInstance();
        mPoints = new ArrayList<>();

        initPoint();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message msg = new Message();
                    msg.arg1 = MESSAGE_DETECTCORNET;
                    mHandler.sendMessage(msg);
                }catch (Exception e){

                }
            }
        });
        thread.start();

        mTRView.setTouchMoveListener(new TouchRectView.TouchMoveListener() {
            @Override
            public void move(Rect rect) {
                mPicZoom.setVisibility(View.VISIBLE);
                mPicZoom.setImage(mSrcBitmap, rect);
            }

            @Override
            public void moveup() {
                mPicZoom.setVisibility(View.INVISIBLE);
            }
        });
        String language = ((MainActivity) mContext).getLanguage();
        if (!language.isEmpty() && language.equals(eng)){
            mIvLanguage.setImageResource(R.mipmap.ic_language_eng1);
        }else {
            mIvLanguage.setImageResource(R.mipmap.ic_language_ch1);
        }
    }

    @Override
    public View getView() {
        return mView;
    }

    public Context getContext() {
        return mContext;
    }

    private FormType mFormType;
    @Override
    public void onPush(boolean fromback){
        mFormType = FormType.ONLY_TOP;
    }

    @Override
    public FormType getFormType(){
        return mFormType;
    }
    private static final float fDisplayRatio = 0.1f;
    private List<Point> mNoDetectedShowDefault;
    private void initPoint(){
        try{
            mNoDetectedShowDefault = new ArrayList<>(4);
        }catch (Exception e){
            e.printStackTrace();
        }

        for (int i = 0;i<4;i++){
            Point point = new Point();
            if (0 == i){
                point.x = (int)(mSrcBitmap.getWidth() * fDisplayRatio);
                point.y = (int)(mSrcBitmap.getHeight()* fDisplayRatio);
            }else if (1 == i){
                point.x=mSrcBitmap.getWidth() -
                        (int)(mSrcBitmap.getWidth() * fDisplayRatio);
                point.y=(int)(mSrcBitmap.getHeight()* fDisplayRatio);
            }else if(2 == i){
                point.x=(int)(mSrcBitmap.getWidth() * fDisplayRatio);
                point.y=mSrcBitmap.getHeight()-
                        (int)(mSrcBitmap.getHeight()* fDisplayRatio);
            }else if (3 == i){
                point.x=mSrcBitmap.getWidth() -
                        (int)(mSrcBitmap.getWidth() * fDisplayRatio);
                point.y=mSrcBitmap.getHeight()-
                        (int)(mSrcBitmap.getHeight()* fDisplayRatio);
            }
            mNoDetectedShowDefault.add(point);
        }


    }

    public void detectCorners(){
        mPoints.clear();
        int result = -1;
        if (mPreprocess!=null){
            result = mPreprocess.detectCorners(mSrcBitmap, mPoints);
        }
        if(result == 0){
            // 逆时针检测点
            // 优化四个点的位置为矩形.
            drawTouchRect(mPoints);
            Log.i(logTag, "Detect Corners Success");
        }else{
            drawTouchRect(mNoDetectedShowDefault);
        }
    }

//    private void calcPointLocation(List<Point> points){
//        if (points.size()>0){
//            Point point=new Point();
//            if (points.get(0).x < points.get(1).x){
//                point.x = points.get(1).x;
//            }else {
//                point.x = points.get(0).x;
//            }
//        }
//    }

    public Bitmap drawLineOnBitmap(Bitmap src, List<Point> srcPoints){
        Bitmap bmpTemp = Bitmap.createBitmap(src.getWidth(),src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpTemp);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(src,0,0,null);
        // set drawing colour
        Paint p = new Paint();
        p.setColor(Color.RED);
        // draw a line onto the canvas
        int nSize = srcPoints.size();
        for(int i=0;i<nSize;i++){
            Point pt1 = srcPoints.get(i % nSize);
            Point pt2 = srcPoints.get((i+1)%nSize);
            canvas.drawLine(pt1.x,pt1.y,pt2.x,pt2.y,p);
        }
        return bmpTemp;
    }

    private final static  int MESSAGE_DETECTCORNET = 1001;
    private final static  int RATIO_IMAGE = 1002;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            switch (msg.arg1){
                case MESSAGE_DETECTCORNET:
                    detectCorners();
                    break;
                case RATIO_IMAGE:
                    detectCorners();
                    break;
                default:
                    break;
            }

        }
    };



    private void drawTouchRect(List<Point> points){
        if (points != null && points.size() > 0){
            mTRView.setVisibility(View.VISIBLE);
            mTRView.initPoint(points);
            mTRView.invalidate();
        }
    }

    @Override
    public void onClick(View view){
        int nId = view.getId();
        switch (nId){
            case R.id.iv_back:
                //sendMessage(Event.REQ_FORM_BACK,null);
                sendMessage(Event.REQ_FORM_SHOW_TAKE_PIC, null);
                break;
            case R.id.iv_language:
                switchLanguage();
                break;
            case R.id.iv_rotateleft:
                float e = mFrameLayout.getRotation();
                mFrameLayout.setRotation(e-90.0f);
                break;
            case R.id.iv_rotateright:
                float e1 = mFrameLayout.getRotation();
                e1 = mFrameLayout.getRotation();
                mFrameLayout.setRotation(e1+90.f);
                break;
            case R.id.iv_cut:
                mFormType = FormType.ONLY_ONE;
                if (null != mPreprocess){
                    mCutBitmap = mPreprocess.doPerspective(mSrcBitmap,mTRView.getPoints());
                    sendMessage(Event.REQ_FORM_SHOW_PIC_ENHANCE, mCutBitmap);
                }else
                    sendMessage(Event.REQ_FORM_SHOW_PIC_ENHANCE, mSrcBitmap);
                break;
            default:
                break;
        }
    }

    private final static String ch = "chi_sim";
    private final static String eng = "eng";
    private void switchLanguage(){
        String language = ((MainActivity) mContext).getLanguage();
        if (language.equals(ch)){
            mIvLanguage.setImageResource(R.mipmap.ic_language_eng1);
            ((MainActivity)mContext).setLanguage(eng);
        }else if (language.equals(eng)){
            mIvLanguage.setImageResource(R.mipmap.ic_language_ch1);
            ((MainActivity)mContext).setLanguage(ch);
        }
    }
}
