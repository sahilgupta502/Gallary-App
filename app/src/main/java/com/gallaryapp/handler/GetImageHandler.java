package com.gallaryapp.handler;

import com.gallaryapp.model.getImage.MyPojo;

public interface GetImageHandler {
    public void onSuccess(MyPojo myPojo);
    public void onError(String message);
}
