package com.visionin.androidgl.myandroidopencv;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.objdetect.CascadeClassifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/23.
 */

public class ImageProcessUtils {

    public static void faceDetect(Bitmap bitmap, CascadeClassifier detector) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGRA2GRAY);
        MatOfRect faces = new MatOfRect();
        detector.detectMultiScale(dst, faces, 1.1, 15, 0, new Size(50, 50), new Size());
        List<Rect> faceList = faces.toList();
        if(faceList.size() > 0) {
            for(Rect rect : faceList) {
                Imgproc.rectangle(src, rect.tl(), rect.br(), new Scalar(255, 0, 0, 255), 2, 8, 0);
            }
        }
        Utils.matToBitmap(src, bitmap);
        src.release();
        dst.release();
    }

    public static double[][] measureObjects(int t, Bitmap bitmap) {
        // TODO:gloomyFish
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.Canny(src, dst, t, t*2, 3, false);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dst, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Imgproc.cvtColor(src, src, Imgproc.COLOR_GRAY2BGR);
        double[][] result = new double[contours.size()][2];
        for(int i=0; i<contours.size(); i++) {
            Moments moments = Imgproc.moments(contours.get(i), false);
            double m00 = moments.get_m00();
            double m10 = moments.get_m10();
            double m01 = moments.get_m01();
            double x0 = m10 / m00;
            double y0 = m01 / m00;
            double arclength = Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true);
            double area = Imgproc.contourArea(contours.get(i));
            result[i][0] = arclength;
            result[i][1] = area;
            Imgproc.circle(src, new Point(x0, y0), 2, new Scalar(255, 0, 0), 2, 8, 0);
        }
        Utils.matToBitmap(src, bitmap);
        src.release();
        dst.release();
        hierarchy.release();
        return result;
    }

    public static void findAndDrawContours(int t, Bitmap bitmap) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.Canny(src, dst, t, t*2, 3, false);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dst, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Imgproc.cvtColor(src, src, Imgproc.COLOR_GRAY2BGR);
        for(int i=0; i<contours.size(); i++) {
            MatOfPoint points = contours.get(i);
            Imgproc.drawContours(src, contours, i, new Scalar(255, 0, 0), 2, 8, hierarchy, 0, new Point(0, 0));
        }
        Utils.matToBitmap(src, bitmap);
        src.release();
        dst.release();
        hierarchy.release();
    }

    public static void templateMatchDemo(Bitmap tpl, Bitmap bitmap) {
        Mat src = new Mat();
        Mat tplMat = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Utils.bitmapToMat(tpl, tplMat);
        int width = bitmap.getWidth() - tpl.getWidth() + 1;
        int height = bitmap.getHeight() - tpl.getHeight() + 1;
        Mat result = new Mat(width, height, CvType.CV_32FC1);
        Imgproc.matchTemplate(src, tplMat, result, Imgproc.TM_CCORR_NORMED);
        Core.normalize(result, result, 0, 1.0, Core.NORM_MINMAX, -1);
        Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);
        Point pt = minMaxLocResult.maxLoc;
        Imgproc.rectangle(src, pt, new Point(pt.x+tpl.getWidth(), pt.y+tpl.getHeight()),
                new Scalar(255, 0, 0, 0), 2, 8, 0);

        Utils.matToBitmap(src, bitmap);
        src.release();
        result.release();
        tplMat.release();
    }

    public static void houghCircleDet(int t, Bitmap bitmap) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.HoughCircles(src, dst, Imgproc.CV_HOUGH_GRADIENT, 1, 15, t*2, 65, 50, 80);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_GRAY2BGR);
        double[] circleParams = new double[3];
        for(int i=0; i<dst.cols(); i++) {
            circleParams = dst.get(0, i);
            Point cp = new Point(circleParams[0], circleParams[1]);
            Imgproc.circle(src, cp,  (int)circleParams[2], new Scalar(255, 0, 0, 0), 2, 8, 0);
        }
        Utils.matToBitmap(src, bitmap);
        src.release();
        dst.release();
    }

    public static void houghLinesDet(int t, Bitmap bitmap) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Mat lines = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.GaussianBlur(src, src, new Size(3, 3), 0, 0, 4);
        Imgproc.Canny(src, dst, t, t*2, 3, false);
        Mat drawImg = new Mat(src.size(), src.type());
        /*Imgproc.HoughLines(dst, lines, 1, Math.PI/180.0, t);
        double[] linep = new double[2];
        for(int i=0; i<lines.cols(); i++) {
            linep = lines.get(0, i);
            double rho = linep[0];
            double theta = linep[1];
            double a = Math.cos(theta);
            double b = Math.sin(theta);
            double x0 = a*rho;
            double y0 = b*rho;
            Point p1 = new Point(x0+1000*(-b), y0 + 1000*a);
            Point p2 = new Point(x0-1000*(-b), y0 - 1000*a);
            Core.line(drawImg, p1, p2, new Scalar(255, 0, 0, 0), 2, 8, 0);
        }*/

        // 直接得到直线的方法
        Imgproc.HoughLinesP(dst, lines, 1, Math.PI/180, t, 15, 3);
        double[] pts = new double[4];
        for(int i=0; i<lines.cols(); i++) {
            pts = lines.get(0, i);
            Point p1 = new Point(pts[0], pts[1]);
            Point p2 = new Point(pts[2], pts[3]);
            Imgproc.line(drawImg, p1, p2, new Scalar(255, 0, 0, 0), 2, 8, 0);
        }

        Utils.matToBitmap(drawImg, bitmap);
        src.release();
        lines.release();
        drawImg.release();
        dst.release();
    }

    public static void cannyEdge(int t, Bitmap bitmap) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.GaussianBlur(src, src, new Size(3, 3), 0, 0, 4);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.Canny(src, dst, t, t*2, 3, false);
        Utils.matToBitmap(dst, bitmap);
        src.release();
        dst.release();
    }
    /***
     * 1 - X 方向
     * 2 - Y 方向
     * 3 - XY方向
     * @param bitmap
     * @param type
     */
    public static void sobleGradient(Bitmap bitmap, int type) {
        Mat src = new Mat();
        Mat xgrad = new Mat();
        Mat ygrad = new Mat();
        Utils.bitmapToMat(bitmap, src);
        if(type == 1) {
            Imgproc.Scharr(src, xgrad, CvType.CV_16S, 1, 0);
            Core.convertScaleAbs(xgrad, xgrad);
            Utils.matToBitmap(xgrad, bitmap);
        } else if(type == 2){
            Imgproc.Scharr(src, ygrad, CvType.CV_16S, 0, 1);
            Core.convertScaleAbs(ygrad, ygrad);
            Utils.matToBitmap(ygrad, bitmap);
        } else if(type == 3) {
            Imgproc.Scharr(src, xgrad, CvType.CV_16S, 1, 0);
            Imgproc.Scharr(src, ygrad, CvType.CV_16S, 0, 1);
            Core.convertScaleAbs(xgrad, xgrad);
            Core.convertScaleAbs(ygrad, ygrad);
            Mat dst = new Mat();
            Core.addWeighted(xgrad, 0.5, ygrad, 0.5, 30, dst);
            Utils.matToBitmap(dst, bitmap);
            dst.release();
        }
        src.release();
        xgrad.release();
        ygrad.release();
    }

    public static void histogramEq(Bitmap bitmap) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.equalizeHist(src, dst);
        Utils.matToBitmap(dst, bitmap);
        src.release();
        dst.release();
    }

    public static void adaptiveThresholdBinary(int size, Bitmap bitmap, boolean gaussian) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);

        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.adaptiveThreshold(src, dst, 255, (gaussian? Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C: Imgproc.ADAPTIVE_THRESH_MEAN_C),
                Imgproc.THRESH_BINARY, size, 0.0);
        Utils.matToBitmap(dst, bitmap);
        src.release();
        dst.release();
    }

    public static void manualThresholdBinary(int t, Bitmap bitmap) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);

        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.threshold(src, dst, t, 255, Imgproc.THRESH_BINARY );
        Utils.matToBitmap(dst, bitmap);
        src.release();
        dst.release();
    }

    public static void thresholdImg(String command, Bitmap bitmap) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.threshold(src, dst, 0, 255, getType(command));
        Utils.matToBitmap(dst, bitmap);
        src.release();
        dst.release();
    }

    private static int getType(String command) {
        if(CommandConstants.THRESHOLD_BINARY_COMMAND.equals(command)) {
            return (Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        } else if(CommandConstants.THRESHOLD_BINARY_INV_COMMAND.equals(command)) {
            return (Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
        } else if(CommandConstants.THRESHOLD_TRUNCAT_COMMAND.equals(command)) {
            return (Imgproc.THRESH_TRUNC | Imgproc.THRESH_OTSU);
        } else if(CommandConstants.THRESHOLD_ZERO_COMMAND.equals(command)) {
            return (Imgproc.THRESH_TOZERO | Imgproc.THRESH_OTSU);
        } else if(CommandConstants.THRESHOLD_ZERO_INV_COMMAND.equals(command)) {
            return (Imgproc.THRESH_TOZERO_INV | Imgproc.THRESH_OTSU);
        } else {
            return (Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        }
    }

    public static void morphLineDetection(Bitmap bitmap) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.threshold(src, src, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
        Mat strElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(35, 1), new Point(-1, -1));
        Imgproc.morphologyEx(src, dst, Imgproc.MORPH_OPEN, strElement);
        Utils.matToBitmap(dst, bitmap);
        src.release();
        dst.release();
    }

    public static void openOrClose(String command, Bitmap bitmap) {
        boolean open = command.equals(CommandConstants.OPEN_COMMAND);
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.threshold(src, src, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
        Mat strElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3), new Point(-1, -1));
        if(open) {
            Imgproc.morphologyEx(src, dst, Imgproc.MORPH_OPEN, strElement);
        } else {
            Imgproc.morphologyEx(src, dst, Imgproc.MORPH_CLOSE, strElement);
        }
        Utils.matToBitmap(dst, bitmap);
        src.release();
        dst.release();
    }

    public static void erodOrDilate(String command, Bitmap bitmap) {
        boolean erode = command.equals(CommandConstants.ERODE_COMMAND);
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat strElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3), new Point(-1, -1));
        if(erode) {
            Imgproc.erode(src, dst, strElement, new Point(-1, -1), 5);
        } else {
            Imgproc.dilate(src, dst, strElement, new Point(-1, -1), 1);
        }
        Utils.matToBitmap(dst, bitmap);
        src.release();
        dst.release();
    }

    public static void customFilter(String command, Bitmap bitmap) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat kernel = getCustomOperator(command);
        Imgproc.filter2D(src, dst, -1, kernel, new Point(-1, -1), 0.0, 4);
        Utils.matToBitmap(dst, bitmap);
        kernel.release();
        src.release();
        dst.release();
    }

    private static Mat getCustomOperator(String command) {
        Mat kernel = new Mat(3, 3, CvType.CV_32FC1);
//        if(CUSTOM_BLUR_COMMAND.equals(command)) {
//            kernel.put(0, 0, 1.0/9.0, 1.0/9.0, 1.0/9.0, 1.0/9.0, 1.0/9.0, 1.0/9.0, 1.0/9.0, 1.0/9.0, 1.0/9.0);
//        } else if(CUSTOM_EDGE_COMMAND.equals(command)) {
//            kernel.put(0, 0, -1, -1, -1, -1, 8, -1, -1, -1, -1);
//        } else if(CUSTOM_SHARPEN_COMMAND.equals(command)) {
//            kernel.put(0, 0, -1, -1, -1, -1, 9, -1, -1, -1, -1);
//        }
        return kernel;
    }

    public static void biBlur(Bitmap bitmap) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2BGR);
        Imgproc.bilateralFilter(src, dst, 15, 150, 15, Core.BORDER_DEFAULT);
        Mat kernel = new Mat(3, 3, CvType.CV_16S);
        kernel.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0);
        Imgproc.filter2D(dst, dst, -1, kernel, new Point(-1, -1), 0.0, 4);
        Utils.matToBitmap(dst, bitmap);
        src.release();
        dst.release();
    }

    public static void gaussianBlur(Bitmap bitmap) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.GaussianBlur(src, dst, new Size(3, 3), 0, 0, 4);
        Utils.matToBitmap(dst, bitmap);
        src.release();
        dst.release();
    }

    public static void meanBlur(Bitmap bitmap) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.blur(src, dst, new Size(3, 3), new Point(-1, -1), Core.BORDER_DEFAULT);
        Utils.matToBitmap(dst, bitmap);
        src.release();
        dst.release();
    }

    public static Bitmap getROIArea(Bitmap bitmap) {
        Rect roi = new Rect(200, 150, 200, 300);
        Bitmap roimap = Bitmap.createBitmap(roi.width, roi.height, Bitmap.Config.ARGB_8888);
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat roiMat = src.submat(roi);
        Mat roiDstMat = new Mat();
        Imgproc.cvtColor(roiMat, roiDstMat, Imgproc.COLOR_BGR2GRAY);
        Utils.matToBitmap(roiDstMat, roimap);

        roiDstMat.release();
        roiMat.release();
        src.release();
        return roimap;
    }

    public static Bitmap demoMatUsage() {
        Bitmap bitmap = Bitmap.createBitmap(400, 600, Bitmap.Config.ARGB_8888);
        Mat dst = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1, new Scalar(100));
        Utils.matToBitmap(dst, bitmap);
        dst.release();
        return bitmap;
    }
    public static Bitmap convert2Gray(Bitmap bitmap) {
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGRA2GRAY);
        Utils.matToBitmap(dst, bitmap);
        src.release();
        dst.release();
        return bitmap;
    }

    public static Bitmap invert(Bitmap bitmap) {
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        long startTime = System.currentTimeMillis();
        Core.bitwise_not(src, src);
        long end = System.currentTimeMillis() - startTime;
        Log.i("Mat-TIME", "\t" + end);
        Utils.matToBitmap(src, bitmap);
        src.release();
        return bitmap;
    }

    public static void adjustContrast(Bitmap bitmap) {
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        src.convertTo(src, CvType.CV_32F);
        Mat whiteImg = new Mat(src.size(), src.type(), Scalar.all(1.25));
        Mat bwImg = new Mat(src.size(), src.type(), Scalar.all(30));
        long startTime = System.currentTimeMillis();
        Core.multiply(whiteImg, src, src);
        Core.add(bwImg, src, src);
        long end = System.currentTimeMillis() - startTime;
        Log.i("Mat-TIME", "\t" + end);
        src.convertTo(src, CvType.CV_8U);
        Utils.matToBitmap(src, bitmap);
        src.release();
        whiteImg.release();
        bwImg.release();
    }

    public static void add(Bitmap bitmap) {
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat whiteImg = new Mat(src.size(), src.type(), Scalar.all(255));
        long startTime = System.currentTimeMillis();
        Core.addWeighted(whiteImg, 0.5, src, 0.5, 0.0,src);
        long end = System.currentTimeMillis() - startTime;
        Log.i("Mat-TIME", "\t" + end);
        Utils.matToBitmap(src, bitmap);
        src.release();
        whiteImg.release();
    }

    public static Bitmap substract(Bitmap bitmap) {
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat whiteImg = new Mat(src.size(), src.type(), Scalar.all(255));
        long startTime = System.currentTimeMillis();
        Core.subtract(whiteImg, src, src);
        long end = System.currentTimeMillis() - startTime;
        Log.i("Mat-TIME", "\t" + end);
        Utils.matToBitmap(src, bitmap);
        src.release();
        return bitmap;
    }

    public static void localInvert(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width*height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int index = 0;
        int a=0, r=0, g=0, b=0;
        long startTime = System.currentTimeMillis();
        for(int row=0; row<height; row++) {
            index = row*width;
            for(int col=0; col<width; col++) {
                int pixel = pixels[index];
                a = (pixel>>24)&0xff;
                r = (pixel>>16)&0xff;
                g = (pixel>>8)&0xff;
                b = (pixel&0xff);
                r = 255 - r;
                // g = 255 - g;
                // b = 255 - b;
                pixel = ((a&0xff)<<24)|((r&0xff)<<16)|((g&0xff)<<8) | (b&0xff);
                pixels[index] = pixel;
                index++;
            }
        }
        long end = System.currentTimeMillis() - startTime;
        Log.i("TIME", "\t" + end);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    }
}
