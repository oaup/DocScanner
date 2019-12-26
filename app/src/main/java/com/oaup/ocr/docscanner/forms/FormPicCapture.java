package com.oaup.ocr.docscanner.forms;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import com.oaup.ocr.common.BitmapCache;
import com.oaup.ocr.common.Event;
import com.oaup.ocr.common.Form;
import com.oaup.ocr.docscanner.MainActivity;
import com.oaup.ocr.docscanner.R;
import com.oaup.ocr.docscanner.utils.DoubleClickButtonUtil;
import com.oaup.ocr.docscanner.utils.UserExtendParams;
import com.oaup.ocr.docscanner.views.TakePicBtmMenu;

import java.io.IOException;
import java.util.List;

/**
 * Created by jkx on 2015/8/5.
 */

public class FormPicCapture extends Form {
    Context mContext;
    View mView;
    private Camera mCamera;
    private Size maxPicSize;
    private Size maxPreSize;
    private FrameLayout cameraPreview;
    private CameraPreview mPreview;
    public FrameLayout layRelative ;
    private static final int CAPTURE_START = 0;
    private static final int CAPTURE_STOP = 1;

    private static final int STATUS_CAMERA_FLASH_AUTO = 0;
    private static final int STATUS_CAMERA_FLASH_ON = 1;
    private static final int STATUS_CAMERA_FLASH_OFF = 2;

    private int mCameraFlash = STATUS_CAMERA_FLASH_AUTO;

    private SharedPreferences mSp;

    private int captureState = CAPTURE_START;
    private ImageView mIvLeft;
    private ImageView mIvCenter;

    @Override
    public void onCreate(Context context, Object value) {
        this.mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.form_take_picture,null);
        mIvLeft = (ImageView)mView.findViewById(R.id.takepic_setting);
        mIvCenter = (ImageView)mView.findViewById(R.id.takepic_capture);
        layRelative =  (FrameLayout) mView.findViewById(R.id.layPicPickPreview);
        cameraPreview = (FrameLayout) layRelative.findViewById(R.id.cameraPreview);
        cameraPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mCamera.autoFocus(null);
                    return true;
                }
                return false;
            }
        });
        mSp = mContext.getSharedPreferences(UserExtendParams.CameraFlashMode,Context.MODE_PRIVATE);
        int cameraFlash = mSp.getInt(UserExtendParams.CameraFlashModeKey, 0);
        mCameraFlash = cameraFlash;
        if (mCameraFlash == STATUS_CAMERA_FLASH_AUTO){
            mIvLeft.setImageResource(R.mipmap.btn_ic_flashauto);
        }else if (mCameraFlash == STATUS_CAMERA_FLASH_ON){
            mIvLeft.setImageResource(R.mipmap.btn_ic_flashon);
        }else if (mCameraFlash == STATUS_CAMERA_FLASH_OFF){
            mIvLeft.setImageResource(R.mipmap.btn_ic_flashoff);
        }
        mIvCenter.setImageResource(R.mipmap.ic_capture_capure);

        initCameraPreview();
    }

    private void initCameraPreview() {
        // TODO Auto-generated method stub
        mCamera = getCameraInstance();
        if (null != mCamera){
            mPreview = new CameraPreview(getContext(), mCamera);
            cameraPreview.addView(mPreview);
        }else{
            Toast.makeText(mContext, R.string.camera_taking_pictures, Toast.LENGTH_LONG);
        }

}

    public static Camera getCameraInstance()
    {
        Camera c = null;
        try
        {
            c = Camera.open();
            Camera.Parameters parameters = c.getParameters();
            parameters.setJpegQuality(90);
            c.setParameters(parameters);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }
    @Override
    public View getView() {
        return mView;
    }

    public Context getContext() {
        return mContext;
    }

    public void onPause(){
        int i = 0;
        i++;
    }

    @Override
    public void onDestory(){
        SharedPreferences.Editor editor = mSp.edit();
        editor.putInt(UserExtendParams.CameraFlashModeKey,mCameraFlash);
        editor.commit();
    }

    @Override
    public Object onMessage(Event event,Object value){
        switch (event){
            case REQ_RESUME_CAMERA:
                resumeCamera();
                break;
            default:
                assert(false);
                break;
        }
        return null;
    }

    @Override
    public void onClick(View view){
        if (DoubleClickButtonUtil.isFastDoubleClick()){
            return;
        }
        int nId = view.getId();
        switch (nId){
            case R.id.takepic_capture:
                takePicture();
                break;
            case R.id.takepic_setting:
                switcher();
                break;
            default:
                break;
        }
    }
    private void takePicture(){
        if (captureState == CAPTURE_START){
            TakePic();
        }else{
            sendMessage(Event.REQ_FORM_SHOW_PIC_CROP, mBitmap);
        }
    }

    private void switcher(){
        if (captureState == CAPTURE_STOP){
            captureState = CAPTURE_START;
            mCamera.startPreview();
            mIvCenter.setImageResource(R.mipmap.ic_capture_capure);
            if (mCameraFlash == STATUS_CAMERA_FLASH_AUTO) {
                mIvLeft.setImageResource(R.mipmap.btn_ic_flashauto);
            }else if (mCameraFlash == STATUS_CAMERA_FLASH_ON){
                mIvLeft.setImageResource(R.mipmap.btn_ic_flashon);
            }else if (mCameraFlash == STATUS_CAMERA_FLASH_OFF){
                mIvLeft.setImageResource(R.mipmap.btn_ic_flashoff);
            }
        }else {
            Log.i("FormPicCapture","onClick flash set.");
            if (mCameraFlash == STATUS_CAMERA_FLASH_AUTO) {
                mCameraFlash = STATUS_CAMERA_FLASH_ON;
                mIvLeft.setImageResource(R.mipmap.btn_ic_flashon);
                Log.i("FormPicCapture","flash on");
            }else if (mCameraFlash == STATUS_CAMERA_FLASH_ON){
                mCameraFlash = STATUS_CAMERA_FLASH_OFF;
                mIvLeft.setImageResource(R.mipmap.btn_ic_flashoff);
                Log.i("FormPicCapture", "flash off");
            }else if (mCameraFlash == STATUS_CAMERA_FLASH_OFF){
                mCameraFlash = STATUS_CAMERA_FLASH_AUTO;
                mIvLeft.setImageResource(R.mipmap.btn_ic_flashauto);
                Log.i("FormPicCapture", "flash auto");
            }
            setCameraFlashMode(mCameraFlash);
        }
    }

    private Bitmap mBitmap;
    private void TakePic(){

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if(success){
                    camera.takePicture(new Camera.ShutterCallback() {

                        @Override
                        public void onShutter() {
                            // TODO Auto-generated method stub

                        }
                    }, null, new Camera.PictureCallback() {

                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            camera.stopPreview();
                            boolean bIsTrue=false;
                            try {
                                mBitmap = BitmapCache.decordBitmap(data, 720, 480);
                                bIsTrue = true;
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if (bIsTrue){
                                captureState = CAPTURE_STOP;
                                mIvLeft.setImageResource(R.mipmap.ic_capture_confirm_back);
                                mIvCenter.setImageResource(R.mipmap.ic_capture_confirm_ok);
                            }

                        }
                    });
                }
            }
        });
    }

    private void resumeCamera(){
        if (null != mCamera){
            mCamera.release();
            mCamera = null;
        }
        initCameraPreview();
    }


    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
        private SurfaceHolder mHolder;
        private Camera camera;


        public CameraPreview(Context context, Camera camera) {
            super(context);
            this.camera = camera;
            mHolder = getHolder();
            mHolder.setKeepScreenOn(true);
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            try {
                if (null != camera){
                    camera.setPreviewDisplay(holder);
                    camera.setDisplayOrientation(90);
                    camera.startPreview();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            // TODO Auto-generated method stub
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                camera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                Camera.Parameters params = mCamera.getParameters();
                params.setPictureFormat(PixelFormat.JPEG);
                params.setJpegQuality(100);
                String flashMode = "auto";
                switch (mCameraFlash){
                    case STATUS_CAMERA_FLASH_AUTO:
                        flashMode = Camera.Parameters.FLASH_MODE_AUTO;
                        break;
                    case STATUS_CAMERA_FLASH_ON:
                        flashMode = Camera.Parameters.FLASH_MODE_ON;
                        break;
                    case STATUS_CAMERA_FLASH_OFF:
                        flashMode = Camera.Parameters.FLASH_MODE_OFF;
                        break;
                    default:
                        break;
                }
                params.setFlashMode(flashMode);
                List<Size> picSizeList = params.getSupportedPictureSizes();
                List<Size> preSizeList = params.getSupportedPreviewSizes();
                // get the max size of camera
                int nPicSize = picSizeList.size();
                int nPreSize = preSizeList.size();
                for (int i =nPicSize-1;i>0;--i){
                    for (int j = 0;j<i;++j){
                        if (picSizeList.get(j).width < picSizeList.get(j+1).width){
                            Size size = picSizeList.get(j);
                            picSizeList.set(j,picSizeList.get(j+1));
                            picSizeList.set(j+1,size);
                        }
                    }
                }
                for (int i =nPreSize-1;i>0;--i){
                    for (int j = 0;j<i;++j){
                        if (preSizeList.get(j).width < preSizeList.get(j+1).width){
                            Size size = preSizeList.get(j);
                            preSizeList.set(j,preSizeList.get(j+1));
                            preSizeList.set(j+1,size);
                        }
                    }
                }
                int nPos = nPicSize/2;
                maxPicSize = picSizeList.get(nPos);
                maxPreSize = preSizeList.get(0);

				/*for (Size size : preSizeList) {
					Log.i("tag", "presize:"+size.width+":"+size.height);
				}*/
                params.setPictureSize(maxPicSize.width, maxPicSize.height);
                params.setPreviewSize(maxPreSize.width, maxPreSize.height);
                params.set("rotation", 90);
//                String strFocusMode = params.getFocusMode();
//                List<String> listFocusMode = params.getSupportedFocusModes();
//                params.getFlashMode();
                mCameraFlashMode = params.getSupportedFlashModes();
                camera.setParameters(params);

                camera.setPreviewDisplay(holder);
                camera.setDisplayOrientation(90);
                camera.startPreview();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            if (camera != null)
            {
                Camera.Parameters params = mCamera.getParameters();
                String camerFlash = params.getFlashMode();
                if (!camerFlash.equals(Camera.Parameters.FLASH_MODE_OFF)){
                    int index = mCameraFlashMode.indexOf(Camera.Parameters.FLASH_MODE_OFF);
                    if (index != -1){
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(params);
                    }
                }
                this.getHolder().removeCallback(this);
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }

    }

    private List<String> mCameraFlashMode;
    private void setCameraFlashMode(int position){
        if (null != mCamera){
            Camera.Parameters params = mCamera.getParameters();
            String camerFlash = params.getFlashMode();
            // auto flash
            if (position == 0){
                if (!camerFlash.equals(Camera.Parameters.FLASH_MODE_AUTO)){
                    int index = mCameraFlashMode.indexOf(Camera.Parameters.FLASH_MODE_AUTO);
                    if (index != -1){
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                        mCamera.setParameters(params);
                    }
                }
            }else if (position == 1){     // on flash
                if (!camerFlash.equals(Camera.Parameters.FLASH_MODE_ON)){
                    int index = mCameraFlashMode.indexOf(Camera.Parameters.FLASH_MODE_ON);
                    if (index != -1){
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        mCamera.setParameters(params);
                    }
                }
            }else if (position == 2){     // off flash
                if (!camerFlash.equals(Camera.Parameters.FLASH_MODE_OFF)){
                    int index = mCameraFlashMode.indexOf(Camera.Parameters.FLASH_MODE_OFF);
                    if (index != -1){
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(params);
                    }
                }
            }
        }

    }

}
