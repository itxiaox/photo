package com.itxiaox.multi_image_selector;

import java.util.List;

public interface MultiResultListener<T> {

    void  onSuccess(List<T> filePath);

    void  onFail(String error);
}
