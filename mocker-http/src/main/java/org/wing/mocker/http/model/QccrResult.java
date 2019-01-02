package org.wing.mocker.http.model;

public class QccrResult {
    private int code=0;
    private String msg="成功";
    private Object info;

    public static QccrResult create(Object info){
        QccrResult qccrResult=new QccrResult();
        qccrResult.setInfo(info);
        return qccrResult;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }
}
