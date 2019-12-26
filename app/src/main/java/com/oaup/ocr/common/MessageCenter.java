package com.oaup.ocr.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkx on 2015/7/17.
 */
public class MessageCenter {

    static List<IExec> s_exelist = new ArrayList<IExec>();

    public static void register(IExec execute){
        s_exelist.add(execute);
    }

    public static void unregister(IExec execute){
        s_exelist.remove(execute);
    }

    public static void sendMessage(Event event, Object args){
        for (IExec s_exec:s_exelist){
            s_exec.todo(event,args);
        }
    }

    public static void sendMessage(Class form,Event event,Object args){
        for(IExec s_exec:s_exelist){
            s_exec.todo(form,event,args);
        }
    }
}
