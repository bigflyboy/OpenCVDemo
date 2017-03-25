package com.visionin.androidgl.myandroidopencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean success = OpenCVLoader.initDebug();
        if(success){
            Log.e(TAG, "Init success!");
        }
    }

    public void changeImage(View v){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.argame03, options);
        long startTime = System.currentTimeMillis();

        //bitmap = convert2Gray(bitmap);
        bitmap = invert(bitmap);

        long stopTime = System.currentTimeMillis();
        long allTime = stopTime - startTime;
        Log.e(TAG, "AllTIme:"+allTime);
        ImageView imgView = (ImageView) findViewById(R.id.image_test);
        imgView.setImageBitmap(bitmap);
    }
    // 灰度图 240毫秒
    public Bitmap convert2Gray(Bitmap bitmap){
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap,src);
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGRA2GRAY);
        Utils.matToBitmap(dst, bitmap);
        src.release();
        dst.release();
        return bitmap;
    }
    // 图片取反 114114毫秒
    public Bitmap invert(Bitmap bitmap){
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        int width = src.cols();
        int height = src.rows();
        int cnum = src.channels();
        byte[] bgra = new byte[cnum];
        for(int row=0; row<height; row++){
            for(int col=0; col<width; col++){
                src.get(row, col, bgra);
                for(int i=0; i<cnum; i++){
                    bgra[i] = (byte)(255 - bgra[i]&0xff);
                }
                src.put(row, col, bgra);
            }
            int b = height - row;
            Log.e(TAG, b + "");
        }
        Utils.matToBitmap(src, bitmap);
        src.release();
        return bitmap;
    }

}
