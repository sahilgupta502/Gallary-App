package com.gallaryapp.api;

import com.gallaryapp.model.getImage.MyPojo;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface API {

    @POST("test.php")
    Call<MyPojo> getImageAPI();

    @Multipart
    @POST("upload.php")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file);
}
