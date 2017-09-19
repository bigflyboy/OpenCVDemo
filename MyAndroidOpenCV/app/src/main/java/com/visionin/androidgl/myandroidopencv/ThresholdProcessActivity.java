package com.visionin.androidgl.myandroidopencv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;
import java.io.InputStream;

public class ThresholdProcessActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private String TAG = "CVLib";
    private int MAX_SIZE = 768;
    private int REQUEST_GET_IMAGE = 1;
    private String command;
    private Bitmap selectedBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threshold_process);
        initLoadOpenCVLibs();
        command = this.getIntent().getStringExtra("command");

        Button processBtn = (Button)findViewById(R.id.t_processButton);
        processBtn.setTag("PROCESS");
        processBtn.setOnClickListener(this);
        processBtn.setText(command);

        Button selectBtn = (Button)findViewById(R.id.t_select_imgButton);
        selectBtn.setTag("SELECT");
        selectBtn.setOnClickListener(this);
        selectedBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.test);

        SeekBar myseekBar = (SeekBar)this.findViewById(R.id.seekBarView);
        myseekBar.setOnSeekBarChangeListener(this);

        TextView textView = (TextView)this.findViewById(R.id.seekBarValueTxtView);
        textView.setText("当前阈值为: " + myseekBar.getProgress());


        // will be removed later on...
        // selectedBitmap = ImageProcessUtils.convert2Gray(selectedBitmap);
        selectedBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.happyfish);
        ImageView imageView = (ImageView)this.findViewById(R.id.imageView);
        imageView.setImageBitmap(selectedBitmap);
    }
    private void initLoadOpenCVLibs() {
        boolean success = OpenCVLoader.initDebug();
        if(success) {
            Log.i(TAG, "load library successfully...");
        }
    }

    @Override
    public void onClick(View v) {
        Object obj = v.getTag();
        if(obj instanceof String) {
            if("SELECT".equals(obj.toString())) {
                selectImage();
                return;
            }
            else if("PROCESS".equals(obj.toString())) {
                SeekBar myseekBar = (SeekBar)this.findViewById(R.id.seekBarView);
                processCommand(myseekBar.getProgress());
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_GET_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();
                int height = options.outHeight;
                int width = options.outWidth;
                int sampleSize = 1;
                int max = Math.max(height, width);
                if(max > MAX_SIZE) {
                    int nw = width / 2;
                    int nh = height / 2;
                    while((nw / sampleSize) > MAX_SIZE || (nh /sampleSize) > MAX_SIZE) {
                        sampleSize *= 2;
                    }
                }
                options.inSampleSize = sampleSize;
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                selectedBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
                ImageView imageView = (ImageView)this.findViewById(R.id.imageView);
                imageView.setImageBitmap(selectedBitmap);
            } catch(IOException ioe) {
                Log.i(TAG, ioe.getMessage());
            }
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Browser Image..."), REQUEST_GET_IMAGE);
    }

    private void processCommand(int t) {
        if((t % 2) == 0) {
            t = t+1;
        }
        Bitmap temp = selectedBitmap.copy(selectedBitmap.getConfig(), true);
        if(CommandConstants.ADAPTIVE_THRESHOLD_COMMAND.equals(command)) {
            ImageProcessUtils.adaptiveThresholdBinary(t, temp, false);
        }else if(CommandConstants.ADAPTIVE_GAUSSIAN_COMMAND.equals(command)) {
            ImageProcessUtils.adaptiveThresholdBinary(t, temp, true);
        } else if(CommandConstants.CANNY_EDGE_COMMAND.equals(command)) {
            ImageProcessUtils.cannyEdge(t, temp);
        } else if(CommandConstants.HOUGH_LINES_COMMAND.equals(command)) {
            ImageProcessUtils.houghLinesDet(t, temp);
        } else if(CommandConstants.HOUGH_CIRCLE_COMMAND.equals(command)) {
            ImageProcessUtils.houghCircleDet(t, temp);
        } else if(CommandConstants.FIND_CONTOURS_COMMAND.equals(command)) {
            ImageProcessUtils.findAndDrawContours(t, temp);
        }else if(CommandConstants.MEASURE_OBJECT_COMMAND.equals(command)) {
            double[][] results = ImageProcessUtils.measureObjects(t, temp);
            for(int i=0; i<results.length; i++) {
                Log.i("Measure Data:", "第 " + i + " 轮廓");
                Log.i("Measure Data:", "周长: " + results[i][0]);
                Log.i("Measure Data:", "面积: " + results[i][1]);
            }
        }else {
            ImageProcessUtils.manualThresholdBinary(t, temp);
        }
        ImageView imgView = (ImageView) this.findViewById(R.id.imageView);
        imgView.setImageBitmap(temp);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        SeekBar myseekBar = (SeekBar)this.findViewById(R.id.seekBarView);
        int value = myseekBar.getProgress();
        TextView textView = (TextView)this.findViewById(R.id.seekBarValueTxtView);
        textView.setText("当前阈值为: " + value);

        // call process
        processCommand(value);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
