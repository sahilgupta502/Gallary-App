package com.gallaryapp.presenter;

import android.app.Activity;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.gallaryapp.R;
import com.gallaryapp.api.WebService;
import com.gallaryapp.handler.GetImageHandler;
import com.gallaryapp.handler.ProgressView;
import com.gallaryapp.model.getImage.MyPojo;

public class GetImagePresenter {

    private final Activity activity;
    private final ImageView imageView;
    private final ProgressView progressView;

    public GetImagePresenter(Activity activity, ProgressView progressView, ImageView imageView) {
        this.activity = activity;
        this.progressView = progressView;
        this.imageView = imageView;
    }

    public void getAllImages() {
        progressView.showDialog(activity);
        WebService.getInstance().getImageMethod(new GetImageHandler() {
            @Override
            public void onSuccess(MyPojo myPojo) {
                String image = myPojo.getResult()[0].getImages();
                Glide.with(activity).load(image).animate(R.anim.fade_in).into(imageView);
                progressView.hideDialog();
            }

            @Override
            public void onError(String message) {
                progressView.hideDialog();
            }
        });
    }
}
