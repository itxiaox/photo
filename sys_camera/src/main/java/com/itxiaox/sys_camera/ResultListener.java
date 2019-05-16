package com.itxiaox.sys_camera;

public interface ResultListener<T> {

    void  onSuccess(T filePath);

    void  onFail(String error);
}
