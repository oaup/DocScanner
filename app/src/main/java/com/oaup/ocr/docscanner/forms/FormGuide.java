package com.oaup.ocr.docscanner.forms;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;

import com.oaup.ocr.common.Event;
import com.oaup.ocr.common.Form;
import com.oaup.ocr.common.FormType;
import com.oaup.ocr.docscanner.MainActivity;
import com.oaup.ocr.docscanner.R;
import com.oaup.ocr.docscanner.utils.InstallTessData;
import com.oaup.ocr.docscanner.utils.UserExtendParams;


public class FormGuide extends Form implements InstallTessData.TessInitFinishCallback {
	Context context;
	View mView;
	private ProgressDialog mProgressDlgStart;
	private InstallTessData installTessData;

	private void initStartProgressDlg() {
		mProgressDlgStart = new ProgressDialog(context);
		mProgressDlgStart.setMessage("初始化...");
		mProgressDlgStart.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDlgStart.setCancelable(false);
		mProgressDlgStart.show();
	}

	Handler mHandle = new Handler() {
		public void handleMessage(Message msg) {
			mProgressDlgStart.dismiss();
		}
	};

	SharedPreferences sharedPreferences;

	@Override
	public void onCreate(final Context context, Object value) {
		// TODO Auto-generated method stub
		mView = LayoutInflater.from(context).inflate(R.layout.form_guide, null);
		this.context = context;
		SharedPreferences user = getContext().getSharedPreferences(UserExtendParams.USER, Context.MODE_PRIVATE);
		final int loginState = user.getInt(UserExtendParams.DirectLoginKey, -2);
		sharedPreferences = context.getSharedPreferences("copyAsset",Context.MODE_PRIVATE);
		boolean ret = sharedPreferences.getBoolean("FormGuidAsset",false);
		if (ret){
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (((MainActivity) context).initOcr() == -1) {
						Toast.makeText(context, "初始化字库失败", Toast.LENGTH_SHORT).show();
					}
					sendMessage(Event.REQ_FORM_SHOW_TAKE_PIC, null);
				}
			}).start();
		}else {
			initStartProgressDlg();
			installTessData = new InstallTessData(context);
			installTessData.setTessInitFinishCallback(this);
			installTessData.initTessData();
		}

	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return mView;
	}


	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public FormType getFormType() {

		return FormType.ONLY_TOP;
	}

	@Override
	public Animation getPopAnimation() {
		// TODO Auto-generated method stub

		return null;
	}

	public Animation getPushAnimation(boolean fromback) {
		return null;
	}

	@Override
	public void onTessInitFinish() {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("FormGuidAsset",true);
		editor.commit();
		if (((MainActivity) context).initOcr() == -1) {
			Toast.makeText(context, "初始化字库失败", Toast.LENGTH_SHORT).show();
		}
		Message msg = new Message();
		mHandle.sendMessage(msg);
		sendMessage(Event.REQ_FORM_SHOW_TAKE_PIC, null);
     }
}
