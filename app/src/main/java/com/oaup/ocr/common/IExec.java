package com.oaup.ocr.common;

/**
 * Created by jkx on 2015/7/17.
 */
public interface IExec {
    /**
    * @param value
    * @return true
     */
    public boolean todo(Event event, Object value);

    public boolean todo(Class<Form> form, Event event, Object value);

}
