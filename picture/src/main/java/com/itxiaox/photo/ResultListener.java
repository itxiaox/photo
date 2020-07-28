package com.itxiaox.photo;

public interface ResultListener<T> {

    void  onSuccess(T filePath);

    void  onFail(String error);
}
