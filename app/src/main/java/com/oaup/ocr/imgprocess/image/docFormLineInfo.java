package com.oaup.ocr.imgprocess.image;

import android.graphics.Point;

/**
 * Created by jkx on 2015/6/16.
 */
public class docFormLineInfo {
    private int nIndex = -1;      //nIndex pointer to the Chains
    private int nStyle = 0;       // style of line, solid(0), dash(1), dot(2) or virtual(3)
    private Point startPoint;
    private Point endPoint;
    private double dAngle = 0.0;  //Angle
    private double dWidth = 0;    //Average width
    private double dQuality = 0;
    private int bSlant = 0;       //Slant or not
    private int nUseType = 0;     //0 -- unused   1 -- rectangle cell    2 -- other cell type
    public docFormLineInfo(){}
    public docFormLineInfo(int nIndex, int nStyle, Point startPoint, Point endPoint
            , double dAngle, double dWidth, double dQuality, int bSlant, int nUseType){
        this.nIndex = nIndex;
        this.nStyle = nStyle;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.dAngle = dAngle;
        this.dWidth = dWidth;
        this.dQuality = dQuality;
        this.bSlant = bSlant;
        this.nUseType = nUseType;
    }
    public int getnIndex(){
        return nIndex;
    }
    public void setnIndex(int nIndex){
        this.nIndex = nIndex;
    }

    public int getnStyle(){
        return nStyle;
    }
    public void setnStyle(int nStyle){
        this.nStyle = nStyle;
    }

    public Point getStartPoint(){
        return startPoint;
    }
    public void setStartPoint(Point ptStart){
        this.startPoint = ptStart;
    }
    public Point getEndPoint(){
        return endPoint;
    }
    public void setEndPoint(Point ptEnd){
        this.endPoint = ptEnd;
    }

    public double getdAngle(){
        return dAngle;
    }
    public void setdAngle(double dAngle){
        this.dAngle = dAngle;
    }

    public double getdWidth(){
        return dWidth;
    }
    public void setdWidth(double dWidth){
        this.dWidth = dWidth;
    }

    public double getdQuality(){
        return dQuality;
    }
    public void setdQuality(double dQuality){
        this.dQuality = dQuality;
    }

    public int getbSlant(){
        return  bSlant;
    }
    public  void setbSlant(int bSlant){
        this.bSlant = bSlant;
    }

    public int getnUseType(){
        return nUseType;
    }

    public void setnUseType(int nUseType){
        this.nUseType = nUseType;
    }
}
