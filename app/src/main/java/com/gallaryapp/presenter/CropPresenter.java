package com.gallaryapp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.gallaryapp.api.WebService;
import com.gallaryapp.handler.ProgressView;
import com.gallaryapp.handler.SendImageHandler;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CropPresenter {

    private final Activity activity;
    private final ProgressView progressView;

    public CropPresenter(Activity activity, ProgressView progressView) {
    this.activity = activity;
    this.progressView = progressView;
}

    public void uploadImageToSerVer(String picturePath) {
        progressView.showDialog(activity);
        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/saved_images/");
        folder.mkdirs();
        File[] allFiles = folder.listFiles();
        File file=allFiles[0];
        Log.e("wddwd",file.toString());

       // File file = new File(picturePath);



        // Log.d(TAG, "Filename " + file.getName());
        //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image1", file.getName(), mFile);
        //  RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());


      //  RequestBody userId1 = RequestBody.create(MediaType.parse("text/plain"), userId);

        WebService.getInstance().uploadImageAPI(fileToUpload,  new SendImageHandler() {
            @Override
            public void onSuccess(String uploadImageExample) {
                Toast.makeText(activity,uploadImageExample,Toast.LENGTH_LONG).show();
                progressView.hideDialog();
                activity.finish();

            }

            @Override
            public void onError(String message) {
                Toast.makeText(activity,message,Toast.LENGTH_LONG).show();
                progressView.hideDialog();

            }
        });
    }
}
