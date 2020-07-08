package com.gallaryapp.api;

import com.gallaryapp.handler.GetImageHandler;
import com.gallaryapp.handler.SendImageHandler;
import com.gallaryapp.model.getImage.MyPojo;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebService {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    public static WebService mInstance;

    API api;
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();

    public WebService() {
        mInstance = this;
        api = new Retrofit.Builder()
                .baseUrl("https://www.finnovationz.com/dummy/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build().create(API.class);
    }

    public static WebService getInstance() {
        return mInstance;
    }

    public void getImageMethod(final GetImageHandler getImageHandler) {

        api.getImageAPI().enqueue(new Callback<MyPojo>() {
            @Override
            public void onResponse(Call<MyPojo> call, Response<MyPojo> response) {
                getImageHandler.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<MyPojo> call, Throwable t) {
                getImageHandler.onError(t.getMessage());
            }
        });
    }

    public void uploadImageAPI(MultipartBody.Part fileToUpload,  final SendImageHandler uploadImageHandler) {
        api.uploadImage(fileToUpload).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               uploadImageHandler.onSuccess("Insert successfully");

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                uploadImageHandler.onError(t.getMessage());
            }
        });
    }
}
