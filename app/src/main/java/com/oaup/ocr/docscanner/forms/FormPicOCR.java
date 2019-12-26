package com.oaup.ocr.docscanner.forms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.oaup.ocr.common.BitmapCache;
import com.oaup.ocr.common.Event;
import com.oaup.ocr.common.Form;
import com.oaup.ocr.docscanner.MainActivity;
import com.oaup.ocr.docscanner.R;
import com.oaup.ocr.docscanner.dialog.OcrEditTextDialog;
import com.oaup.ocr.docscanner.utils.MsgEvent;
import com.oaup.ocr.docscanner.views.HScrollImgPreMenu;
import com.oaup.ocr.docscanner.views.ImageText;
import com.oaup.ocr.docscanner.views.ViewTouchImgMenu;
import com.oaup.ocr.imgprocess.image.Preprocess;

import com.oaup.ocr.tesseract.tesseract.TessBaseAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkx on 2015/8/11.
 */
public class FormPicOCR extends Form{
    private final static String logTag = "FormPicOCR";
    Context mContext;
    View mView;
    private ViewTouchImgMenu mViewTouchImgMenu;
    private FrameLayout mFrameLayout;
    private HorizontalScrollView mHorizontalScrollView;
    private ImageView mImgView;
    private Bitmap mBitmap;
    private Preprocess mPreprocess;
    private TessBaseAPI mTessObj;
    private String ocrText;

    private List<ImageText> mEnhanceViews;
    private int mImageTextIndex;

    private int[] mResId = {
            R.mipmap.enhance_recommend_hl,
            R.mipmap.enhance_orig_hl,
            R.mipmap.enhance_low_hl,
            R.mipmap.enhance_high_hl,
            R.mipmap.enhance_gray_hl,
            R.mipmap.enhance_bw_hl
    };

    private int[] mResIdN = {
            R.mipmap.enhance_recommend,
            R.mipmap.enhance_orig,
            R.mipmap.enhance_low,
            R.mipmap.enhance_high,
            R.mipmap.enhance_gray,
            R.mipmap.enhance_bw
    };

    private void initImgMenu(){
        mEnhanceViews = new ArrayList<>(6);
        HScrollImgPreMenu hScrollImgPreMenu = (HScrollImgPreMenu)mHorizontalScrollView.findViewById(
                R.id.enhanceMenu
        );
        mEnhanceViews.add(((ImageText) hScrollImgPreMenu.findViewById(R.id.hScrollLeft0)));
        mEnhanceViews.add(((ImageText) hScrollImgPreMenu.findViewById(R.id.hScrollLeft1)));
        mEnhanceViews.add(((ImageText) hScrollImgPreMenu.findViewById(R.id.hScrollLeft2)));
        mEnhanceViews.add(((ImageText) hScrollImgPreMenu.findViewById(R.id.hScrollLeft3)));
        mEnhanceViews.add(((ImageText) hScrollImgPreMenu.findViewById(R.id.hScrollLeft4)));
        mEnhanceViews.add(((ImageText) hScrollImgPreMenu.findViewById(R.id.hScrollLeft5)));
        mImageTextIndex = 0;
        ((ImageView)mEnhanceViews.get(0).findViewById(R.id.image_image_text)).setImageResource(mResId[mImageTextIndex]);
        ((TextView)mEnhanceViews.get(0).findViewById(R.id.text_image_text)).setTextColor(Color.WHITE);
        for (int i = 1;i<mEnhanceViews.size();i++){
            ((ImageView)mEnhanceViews.get(i).findViewById(R.id.image_image_text)).setImageResource(mResIdN[i]);
            ((TextView)mEnhanceViews.get(i).findViewById(R.id.text_image_text)).setTextColor(Color.rgb(26,130,105));
        }

        mImgView.setImageBitmap(mBitmap);
    }

    @Override
    public void onCreate(Context context, Object value) {
        this.mContext = context;
        mBitmap = (Bitmap)value;
        mView = LayoutInflater.from(mContext).inflate(R.layout.form_pic_enhance, null);
        mHorizontalScrollView = (HorizontalScrollView)mView.findViewById(R.id.enhanceHScroll);
        mViewTouchImgMenu = (ViewTouchImgMenu)mView.findViewById(R.id.layImgTouchMenu);
        mFrameLayout = (FrameLayout)mView.findViewById(R.id.enhanceFrame);
        mImgView = (ImageView)mView.findViewById(R.id.enhanceImgShow);

        initImgMenu();
        mPreprocess = ((MainActivity)mContext).getPreprocessInstance();
        mTessObj = ((MainActivity)mContext).getTessBaseInstance();
        //initOcr();
    }

    @Override
    public View getView() {
        return mView;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void onDestory(){
            System.gc();
    }
    @Override
    public void onClick(View view){
        int nId = view.getId();
        switch (nId){
            case R.id.hScrollLeft0:
                switchEnhanceMenuBar(0);
                break;
            case R.id.hScrollLeft1:
                switchEnhanceMenuBar(1);
                mImgView.setImageBitmap(mBitmap);
                mImgView.invalidate();
                Toast.makeText(mContext, R.string.enhance_ori, Toast.LENGTH_SHORT).show();
                break;
            case R.id.hScrollLeft2:
                switchEnhanceMenuBar(2);
                mImgView.setImageBitmap(mPreprocess.enhance(mBitmap));
                mImgView.invalidate();
                Toast.makeText(mContext, R.string.enhance, Toast.LENGTH_SHORT).show();
                break;
            case R.id.hScrollLeft3:
                switchEnhanceMenuBar(3);
                break;
            case R.id.hScrollLeft4:
                switchEnhanceMenuBar(4);
                mImgView.setImageBitmap(mPreprocess.cvt2Gray(mBitmap));
                mImgView.invalidate();
                Toast.makeText(mContext, R.string.enhance_gray, Toast.LENGTH_SHORT).show();
                break;
            case R.id.hScrollLeft5:
                switchEnhanceMenuBar(5);
                mImgView.setImageBitmap(mPreprocess.cvt2BW(mBitmap));
                mImgView.invalidate();
                Toast.makeText(mContext, R.string.enhance_bw, Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_back:
                sendMessage(Event.REQ_FORM_BACK,null);
                break;
            case R.id.iv_rotateleft:
                mBitmap = BitmapCache.rotate(mBitmap, -90);
                mImgView.setImageBitmap(mBitmap);
                break;
            case R.id.iv_rotateright:
                mBitmap = BitmapCache.rotate(mBitmap, 90);
                mImgView.setImageBitmap(mBitmap);
                break;
            case R.id.iv_ocr:
                if (ocrThreadRuning){
                    AlertDialog.Builder dlg = new AlertDialog.Builder(mContext);
                    dlg.setTitle(R.string.warning);
                    dlg.setMessage("正在识别是否取消？");
                    dlg.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mTessObj.stop();
                        }
                    }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
                }else {
                    new OcrThread().start();
                }
                break;
            default:
                break;
        }
    }

    private void switchEnhanceMenuBar(int nId){
        if (mEnhanceViews.get(nId) != mEnhanceViews.get(mImageTextIndex)){
            ((ImageView)mEnhanceViews.get(mImageTextIndex).findViewById(R.id.image_image_text))
                    .setImageDrawable(mContext.getResources().getDrawable(mResIdN[mImageTextIndex]));
            ((TextView)mEnhanceViews.get(mImageTextIndex).findViewById(R.id.text_image_text)).setTextColor(Color.rgb(26, 130, 105));
            ((ImageView)mEnhanceViews.get(nId).findViewById(R.id.image_image_text))
                    .setImageDrawable(mContext.getResources().getDrawable(mResId[nId]));
            ((TextView)mEnhanceViews.get(nId).findViewById(R.id.text_image_text)).setTextColor(Color.WHITE);
            mImageTextIndex = nId;
        }
    }


    private boolean ocrThreadRuning = false;
    class OcrThread  extends Thread{
        public OcrThread(){
            ((MainActivity)mContext).getOcrProgressDialog().setProgress(0);
            ((MainActivity)mContext).getOcrProgressDialog().show();
        }

        @Override
        public void run(){
            if (null != mTessObj){
                ocrThreadRuning = true;
                mTessObj.clear();
                mTessObj.setImage(mBitmap);
                ocrText = mTessObj.getUTF8Text();
                ((MainActivity)mContext).getOcrProgressDialog().dismiss();
                Message msg = new Message();
                MsgEvent event = MsgEvent.MSG_OCR_TEXT_FINISH;
                msg.arg1 = event.ordinal();
                msg.obj = ocrText;
                ((MainActivity)mContext).mMsgHandler.sendMessage(msg);
            }
        }
    }

}
