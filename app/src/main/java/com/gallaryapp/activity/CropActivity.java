package com.gallaryapp.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.gallaryapp.R;
import com.gallaryapp.handler.ProgressView;
import com.gallaryapp.presenter.CropPresenter;
import com.gallaryapp.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


public class CropActivity extends AppCompatActivity implements View.OnClickListener, ProgressView {

    private LinearLayout linear_crop;
    private LinearLayout linear_rotate;
    private LinearLayout linear_text;
    private int rotation = 0;
    private ImageView iv_image_view;
    private String photo = "";
    private TextView tv_done;
    private CropPresenter presenter;
    public final int PIC_CROP = 1;
    private Activity activity;
    private Bitmap bitmap;
    private String filename = "";
    private String Path = "";
    private ImageView iv_back;
    private EditText et_enter_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        activity = this;
        initUI();
    }

    private void initUI() {
        presenter = new CropPresenter(activity, this);

        photo = getIntent().getStringExtra("photo");
        iv_back = (ImageView) findViewById(R.id.iv_back);

        linear_crop = (LinearLayout) findViewById(R.id.linear_crop);
        linear_rotate = (LinearLayout) findViewById(R.id.linear_rotate);
        linear_text = (LinearLayout) findViewById(R.id.linear_text);
        iv_image_view = (ImageView) findViewById(R.id.iv_image_view);

        tv_done = (TextView) findViewById(R.id.tv_done);
        et_enter_text = (EditText) findViewById(R.id.et_enter_text);
        et_enter_text.setVisibility(View.GONE);

        linear_crop.setOnClickListener(this);
        linear_rotate.setOnClickListener(this);
        linear_text.setOnClickListener(this);
        tv_done.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        Glide.with(this).load(new File(photo)).into(iv_image_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_crop:
                iv_image_view.invalidate();
                Drawable drCrop = ((ImageView) iv_image_view).getDrawable();
                Bitmap bmpCrop = ((GlideBitmapDrawable) drCrop.getCurrent()).getBitmap();
                Uri uri = getImageUri(CropActivity.this, bmpCrop);

                break;
            case R.id.linear_rotate:
                if (rotation == 360) {
                    rotation = 0;
                }
                rotation = rotation + 90;
                iv_image_view.setRotation(rotation);
                break;
            case R.id.linear_text:

                if (et_enter_text.getVisibility() == View.VISIBLE) {
                    et_enter_text.setVisibility(View.GONE);
                } else {
                    et_enter_text.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_done:
                iv_image_view.invalidate();
                Drawable dr = ((ImageView) iv_image_view).getDrawable();
                Bitmap bmp = ((GlideBitmapDrawable) dr.getCurrent()).getBitmap();
                SaveImage(bmp);
                break;

            case R.id.iv_back:
                finish();
                break;
        }
    }

    private BitmapDrawable writeTextOnDrawable(int drawableId, String text) {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);
        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(CropActivity.this, 11));
        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);
        Canvas canvas = new Canvas(bm);

        if (textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(CropActivity.this, 7));        //Scaling needs to be used for different dpi's

        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(text, xPos, yPos, paint);
        return new BitmapDrawable(getResources(), bm);
    }


    public static int convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;
        return (int) ((nDP * conversionScale) + 0.5f);
    }

    private void SaveImage(Bitmap finalBitmap) {

        String rootPath = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(rootPath + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image" + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();

        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        finalBitmap = Bitmap.createBitmap(finalBitmap, 0, 0, finalBitmap.getWidth(), finalBitmap.getHeight(), matrix, true);

        try {
            FileOutputStream outValue = new FileOutputStream(file);

            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outValue);
            outValue.flush();
            outValue.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        presenter.uploadImageToSerVer(photo);
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch (ActivityNotFoundException anfe) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PIC_CROP) {
            if (data != null) {
                // get the returned data
                Uri selectedImage = data.getData();
                Log.e("selected image", "" + selectedImage);
                compressImage(getPath(selectedImage));
                String picturePath = filename;
                Glide.with(activity).load(picturePath).into(iv_image_view);
            }
        }
    }

    private String getPath(Uri selectedImage) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(selectedImage, projection, null, null, null);
        if (cursor == null)
            return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        Path = cursor.getString(column_index);
        cursor.close();
        return Path;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        performCrop(Uri.parse(path));
        return Uri.parse(path);
    }

    @Override
    public void showDialog(Activity activity) {
        Utils.showDialog(activity);
    }

    @Override
    public void hideDialog() {
        Utils.hideDialog();
    }

    private String compressImage(String absolutePath) {

        String filePath = getRealPathFromURI(absolutePath);
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;

        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bitmap = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int actualWidth, int actualHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > actualHeight || width > actualWidth) {
            final int heightRatio = Math.round((float) height / (float) actualHeight);
            final int widthRatio = Math.round((float) width / (float) actualWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = actualWidth * actualHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = this.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    private String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }
}
