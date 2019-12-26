package com.oaup.ocr.docscanner.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.oaup.ocr.docscanner.R;


/**
 * Created by jkx on 2015/10/15.
 */
public class OcrEditTextDialog extends Dialog {
    private Context mContext;

    public interface OnOcrEditTextDialogListener{
        public void back(String editText);
    }
    public OcrEditTextDialog(Context context,String title,
                            String text,String rightTop,OnOcrEditTextDialogListener ocrEditTextDialogListener){
        super(context);
        this.mContext = context;
        this.mTitle = title;
        this.mText = text;
        this.mRightTop = rightTop;
        this.mOnOcrEditTextDialogListener = ocrEditTextDialogListener;
    }

    private String mText;
    private String mTitle;
    private String mRightTop;
    private TextView mTextCancel;
    private TextView mTextSure;
    private EditText mEditText;
    private OnOcrEditTextDialogListener mOnOcrEditTextDialogListener;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_note_custom_dialog);
        if (!mTitle.isEmpty()){
            ((TextView)findViewById(R.id.idNoteDialogTitle)).setText(mTitle);
        }
        mTextCancel = (TextView)findViewById(R.id.idNoteDialogClose);
        mTextSure = (TextView)findViewById(R.id.idNoteDialogRight);
        if (null != mTextSure && null != mRightTop){
            mTextSure.setText(mRightTop);
        }
        mEditText = (EditText)findViewById(R.id.idNoteDialogEdit);
        if (null != mText && !mText.isEmpty()){
            mEditText.setText(mText);
        }

        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mTextSure.setText(mRightTop);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTextSure.setText(R.string.finish);
                InputMethodManager im = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                im.showSoftInput(mEditText, 0);
            }
        });
        mTextSure.setOnClickListener(mOnSureClickListener);
        mTextCancel.setOnClickListener(mOnCancelClickListener);
    }

    private View.OnClickListener mOnSureClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mTextSure.getText().equals(mContext.getString(R.string.finish))){
                mTextSure.setText(mRightTop);
                InputMethodManager im = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }else {
               // share
                showShareDlg(mEditText.getText().toString());
            }
        }
    };

    private View.OnClickListener mOnCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (null != mOnOcrEditTextDialogListener){
                mOnOcrEditTextDialogListener.back(String.valueOf(mEditText.getText()));
                OcrEditTextDialog.this.dismiss();
            }}
    };

    protected void showShareDlg(String text){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(Intent.createChooser(intent, "分享文本"));
        mOnOcrEditTextDialogListener.back(String.valueOf(mEditText.getText()));
        OcrEditTextDialog.this.dismiss();
    }
}
