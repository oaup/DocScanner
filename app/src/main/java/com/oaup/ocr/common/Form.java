package com.oaup.ocr.common;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseExpandableListAdapter;


/**
 * Created by jkx on 2015/7/17.
 */
public abstract class Form{

    public abstract void onCreate(Context context,Object value);
    public abstract View getView();

    /*
    * on view visible
     */
    public void onPush(boolean fromback){

    }
    /*
    * on view invisible
     */
    public void onPop(){

    }
    /**
     * on form destory
     */
    public void onDestory() {
    }
    public Object onMessage(Event event,Object value){

        return null;
    }


    public void sendMessage(Event event,Object value){
        MessageCenter.sendMessage(event, value);}
    public void sendMessage(Class form, Event event,Object value){
        MessageCenter.sendMessage(form, event, value);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){return  false;}
    public void newValue(Object value){

    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return false;
    }
    public void onClick(View view){

    }
    public void onPause(){


    }
    public FormType getFormType()
    {
        return FormType.ONLY_TOP;
    }

    public Animation getPushAnimation(boolean fromback){
         if (fromback){
             return new TranslateAnimation(-UIUtil.width, 0,0,0);
         }else {
             return new TranslateAnimation(UIUtil.width,0,0,0);
         }
    }

    public Animation getPopAnimation(){
        if( getFormType() == FormType.ONLY_ONE )
            return new TranslateAnimation(0,-UIUtil.width,0,0);
        else
            return  new TranslateAnimation(0, UIUtil.width,0,0);
    }

}
