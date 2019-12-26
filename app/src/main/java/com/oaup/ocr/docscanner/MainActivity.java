package com.oaup.ocr.docscanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.oaup.ocr.common.Event;
import com.oaup.ocr.common.Form;
import com.oaup.ocr.common.FormManager;
import com.oaup.ocr.common.IExec;
import com.oaup.ocr.common.IFormExec;
import com.oaup.ocr.common.MessageCenter;
import com.oaup.ocr.common.NLog;
import com.oaup.ocr.common.UIUtil;
import com.oaup.ocr.docscanner.dialog.OcrEditTextDialog;
import com.oaup.ocr.docscanner.forms.FormGuide;
import com.oaup.ocr.docscanner.forms.FormPicCapture;
import com.oaup.ocr.docscanner.forms.FormPicCrop;
import com.oaup.ocr.docscanner.forms.FormPicOCR;
import com.oaup.ocr.docscanner.utils.MsgEvent;
import com.oaup.ocr.imgprocess.image.Preprocess;
import com.oaup.ocr.tesseract.tesseract.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements IExec,IFormExec,TessBaseAPI.ProgressNotifier{
    RelativeLayout mContainer;
    FormManager mFormManager;
    long mCurrentThread;
    public String cid ;
    private SharedPreferences sharedPreferences;

    Handler mHandler = new Handler(){

        public void handleMessage(android.os.Message msg) {
            MessageCenter.sendMessage(Event.values()[msg.arg1], msg.obj);
        };

    };

    public Handler mMsgHandler = new Handler(){
        public void handleMessage(Message msg){
            MsgEvent event = MsgEvent.values()[msg.arg1];
            switch (event){
                case MSG_OCR_TEXT_FINISH:
                    showOcrEditText((String)msg.obj);
                    break;
                case MSG_OCR_INIT:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        showStatusBar();
        setContentView(R.layout.activity_main);
        mContainer = (RelativeLayout)findViewById(R.id.container);
        mFormManager = new FormManager(this);
        mCurrentThread = Thread.currentThread().getId();

        //ocr init
        sharedPreferences = getContext().getSharedPreferences("langOcr", Context.MODE_PRIVATE);
        this.language = sharedPreferences.getString("language", "");
        if (this.language.isEmpty()){
            language = "chi_sim";
        }
        initProgressDlg();
        MessageCenter.register(this);
        mFormManager.toPush(FormGuide.class, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean todo(Event event, Object value){
        if (mFormManager.getForm() != null){
            return todo(mFormManager.getForm().getClass(),event,value);
        }else {
            return todo(null,event,value);
        }
    }
    @Override
    public boolean todo(Class form, Event event, Object value){
        if( Thread.currentThread().getId() != mCurrentThread )
        {
            Message msg = new Message();
            msg.arg1 = event.ordinal();
            msg.obj = value;
            mHandler.sendMessage(msg);
            return true;
        }
        NLog.i("Message deal:event:%s value:%s",
                event.toString(), value == null ? "null" : value);
        boolean result = false;
        switch (event) {
            case REQ_FORM_SHOW_GUIDE:
                mFormManager.toPush(FormGuide.class, value);
                return true;
            case REQ_FORM_SHOW_DOCUMENT_IMG_EDIT:
                //mFormManager.toPush(FormImgProcess.class, value);
                return true;
            case REQ_FORM_SHOW_TAKE_PIC:
                mFormManager.toPush(FormPicCapture.class, value);
                return true;
            case REQ_FORM_SHOW_PIC_CROP:
                mFormManager.toPush(FormPicCrop.class, value);
                return true;
            case REQ_FORM_SHOW_PIC_ENHANCE:
                mFormManager.toPush(FormPicOCR.class, value);
                return true;
            case REQ_FORM_BACK:
                mFormManager.toPop();
                return true;
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        NLog.i("ActControl onkeyDown");
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(!(mFormManager.getForm() instanceof FormPicCapture)){
                    mFormManager.getForm().sendMessage(Event.REQ_FORM_BACK,null);
            }else
                exitBy2Click();
        }
        return false;
    }

    /**
     * 双击退出应用
     */
    private static Boolean isExit = false;

    private void exitBy2Click(){
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, R.string.click_again_to_exit, Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2*1000);

        } else {
            if (mTessObj != null){
                mTessObj.end();
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("language",this.language);
            editor.commit();
            finish();
            System.exit(0);
        }

    }

    @Override
    public void exePush(Form formz) {
        View view = formz.getView();
        if( view.getParent() != null )
            ((ViewGroup)view.getParent()).removeView(view);
        mContainer.addView(view);
    }
    @Override
    public void exePop(Form formz)
    {

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable(){
            @Override
            public void run() {
                mContainer.removeViewAt(0);
            }
        }, 100);

    }

    @Override
    public Context getContext() {
        return this;
    };

    boolean init;
    @Override
    public void onAttachedToWindow() {
        //super.onAttachedToWindow();
        if(!init){
            init = true;
            UIUtil.toInit(this);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        Form form = mFormManager.getForm();
        if (form.getClass().equals(FormPicCapture.class) ){
            form.onMessage(Event.REQ_RESUME_CAMERA, null);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    private final int REQUESTCODE_TIAKPICK = 10001;
    private final int REQUESTCODE_OPENGALLERY = 10002;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO
        switch (requestCode) {
            case REQUESTCODE_TIAKPICK:

                if(resultCode == RESULT_OK){
                    // MessageCenter.sendMessage(Event.REQ_CAMERA_RETURN_OK,null);
                }else if(resultCode == RESULT_CANCELED)
                    return;
                break;
            case REQUESTCODE_OPENGALLERY:

                if(resultCode == RESULT_OK){
                    Bitmap bitmap = null;

                    try {
                        Uri originalUri = data.getData();//获得图片的Uri
                        ContentResolver resolver = getContentResolver();
                        bitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    //MessageCenter.sendMessage(Event.REQ_GALLERY_RETURN_OK , bitmap);
                }else if(resultCode == RESULT_CANCELED)
                    return;
                break;

        }
    }

    public void onClick(View view){
        mFormManager.getForm().onClick(view);
    }

    private void hideStatusBar(){
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void showStatusBar(){
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &=  ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void showOcrEditText(String text){
        OcrEditTextDialog dialog = new OcrEditTextDialog(this,"识别结果",text,
                "分享",new OcrEditTextDialog.OnOcrEditTextDialogListener(){
            public void back(String editText){
            }
        });
        dialog.show();
    }
    private Preprocess mPreprocess;
    private TessBaseAPI mTessObj;
    private final static String logTag = "MainActivity";
    public int initOcr(){
        try {
            mPreprocess = new Preprocess();
        }catch (Exception e){
            e.printStackTrace();
            mPreprocess = null;
        }

        try{
            initTessBase();
        }catch (Exception e){
            e.printStackTrace();
            Log.i(logTag, "Init Ocr Obj Failed");
            return -1;
        }
        return 1;
    }
    private String language = "chi_sim";
    private void initTessBase(){
        File appDir = this.getExternalFilesDir(null);
        try {
            File[] files = new File(appDir,"tesseract/tessdata").listFiles();
            if (null != files && files.length>0){
                mTessObj = new TessBaseAPI(this);
                String dataPath = appDir.getAbsolutePath() + "/tesseract/";
                File dir = new File(dataPath + "tessdata/");
                if (!dir.exists()){
                    dir.mkdirs();
                }
                String lang = "chi_sim";
                if (null != mTessObj){
                    if (!mTessObj.init(dataPath,language)){
                        Log.i("logTag", "Init Ocr Failed");
                        mTessObj = null;
                        Toast.makeText(this,"字库加载失败",Toast.LENGTH_SHORT).show();
                    }
                }
            }else {
                Toast.makeText(this,"请安装字库",Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ProgressDialog mProgressDialog;
    private void initProgressDlg(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(100);
        mProgressDialog.setMessage("正在识别...");
    }

    @Override
    public void onProgressValues(TessBaseAPI.ProgressValues progressValues){
        if (null != progressValues){
            int percent = progressValues.getPercent();
            mProgressDialog.setProgress(percent);
        }
    }

    public Preprocess getPreprocessInstance(){
        return mPreprocess;
    }

    public TessBaseAPI getTessBaseInstance(){
        return mTessObj;
    }

    public ProgressDialog getOcrProgressDialog(){
        return mProgressDialog;
    }

    public String getLanguage(){
        return language;
    }

    public void setLanguage(String language){
        this.language = language;
        if (null != mTessObj){
            File appDir = Environment.getExternalStorageDirectory();
            String dataPath = appDir.getAbsolutePath() + "/tesseract/";
            if (!mTessObj.init(dataPath,this.language)){
                Log.i("logTag", "Init Ocr Failed");
                mTessObj = null;
                Toast.makeText(this,"字库加载失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
