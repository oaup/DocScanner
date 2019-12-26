package com.oaup.ocr.common;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;

import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jkx on 2015/7/17.
 */
public class FormManager {
    Form mDelegate;
    IFormExec mFormExec;
    ReentrantLock mLock;
    public FormManager(IFormExec iFormExec){
        mFormExec = iFormExec;
        mLock = new ReentrantLock();
    }

    Vector<Form> mForms = new Vector<Form>();
    public void toPush(Class formClass, Object value){
        View focus = (View)((Activity)mFormExec.getContext()).getCurrentFocus();
        if (focus != null){
            ((InputMethodManager)mFormExec.getContext().getSystemService(mFormExec.getContext().INPUT_METHOD_SERVICE))
                    .hideSoftInputFromInputMethod(focus.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }

        long duration = 500;
        try{
            mLock.lock();
            if (mForms.size()>0){
                final Form form_cur = mForms.get(mForms.size()-1);
                if (form_cur.getClass().equals(formClass)){
                    form_cur.newValue(value);
                    return;
                }else {
                    final FormType type = form_cur.getFormType();
                    switch( type ){
                        case ONLY_TOP:

                            mForms.remove(form_cur);
                            break;
                        case ONLY_ONE:
                            break;
                    }
                    form_cur.onPop();

                    Animation anim = form_cur.getPopAnimation();
                    if (anim != null){
                        anim.setDuration(duration);
                        anim.setFillEnabled(true);
                        anim.setFillAfter(true);
                        anim.setFillBefore(true);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mFormExec.exePop(form_cur);
                                if( type == FormType.ONLY_TOP)
                                    form_cur.onDestory();
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        form_cur.getView().startAnimation(anim);
                    }else {
                        mFormExec.exePop(form_cur);
                        if( type == FormType.ONLY_TOP)
                            form_cur.onDestory();
                    }
                    int pos = getFormPos(formClass);
                    Form form = null;
                    boolean fromback = true;
                    if( pos > -1 ){
                        form	= mForms.remove(pos);
                        form.newValue(value);
                    }else{
                        try {	form = (Form)formClass.newInstance();	} catch (Exception e) {	e.printStackTrace();	}
                        form.onCreate(mFormExec.getContext(),value);
                        fromback = false;
                    }
                    mFormExec.exePush(form);
                    mForms.add(form);
                    form.onPush(fromback);

                    anim = form.getPushAnimation(fromback);
                    if( anim != null )
                    {
                        anim.setDuration(duration);
                        anim.setFillEnabled(true);
                        anim.setFillAfter(true);
                        anim.setFillBefore(true);
                        form.getView().startAnimation(anim);
                    }

                }
            }else {
                Form form = null;
                try {	form = (Form)formClass.newInstance();
                } catch (Exception e) {	e.printStackTrace();
                }
                form.onCreate(mFormExec.getContext(),value);
                mFormExec.exePush(form);
                mForms.add(form);
                form.onPush(false);

                Animation anim = form.getPushAnimation(false);
                if( anim != null )
                {
                    anim.setDuration(duration);
                    anim.setFillEnabled(true);
                    anim.setFillAfter(true);
                    anim.setFillBefore(true);
                    form.getView().startAnimation(anim);
                }
            }
        }finally {
            mLock.unlock();
        }
    }

    public void toPop(){
        if( mForms.size() > 1 ){
            toPush(mForms.get(mForms.size()-2).getClass(),null);
        }
    }

    public void toPopAll(){
        while( mForms.size() > 0 ){
            Form form_cur =  mForms.remove(mForms.size()-1);
            form_cur.onPop();
            form_cur.onDestory();
            form_cur = null;
        }
    }

    public int getFormPos(Class<Form> formclass){
        for(int i=0;i<mForms.size();i++){
            if( mForms.get(i).getClass().equals(formclass) )
                return i;
        }
        return -1;
    }

    public Form getForm(Class formclass){
        for(int i=0;i<mForms.size();i++){
            if( mForms.get(i).getClass().equals(formclass) )
                return mForms.elementAt(i);
        }
        return null;
    }

    public Form getForm() {
        try{
            mLock.lock();
            if( mForms.size() == 0 )
                return null;
            return mForms.elementAt(mForms.size()-1);
        }
        finally{
            mLock.unlock();
        }
    }

    public int getFormSize() {
        return mForms.size();
    }

    public void setDelegate(Form form) {
        mDelegate = form;
    }

    public Form getDelegate() {
        return mDelegate;
    }

}
