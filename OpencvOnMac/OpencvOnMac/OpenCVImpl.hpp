//
//  OpenCVImpl.hpp
//  OpencvOnMac
//
//  Created by 王志远 on 2017/3/25.
//  Copyright © 2017年 王志远. All rights reserved.
//

#ifndef OpenCVImpl_hpp
#define OpenCVImpl_hpp

#include <stdio.h>
#include <opencv2/opencv.hpp>
#include <string>
#include <vector>
using namespace cv;
using namespace std;

class OpenCVImpl{
public:
    void DisplayImage(string url);
    void ErodeImage(string url);
    void BlurImage(string url);
    void CannyImage(string url);
    void VideoDisplay(string url);
    void CaptureDisplay();
    int DetectFace();
    
private:
    
    void detectAndDisplay(Mat frame);
    
    String face_cascade_name = "/Users/wangzhiyuan/LibSources/opencv/data/haarcascades/haarcascade_frontalface_alt.xml";
    String eyes_cascade_name = "/Users/wangzhiyuan/LibSources/opencv/data/haarcascades/haarcascade_eye_tree_eyeglasses.xml";
    CascadeClassifier face_cascade;
    CascadeClassifier eyes_cascade;
    string window_name = "Capture - Face detection";
    
};

#endif /* OpenCVImpl_hpp */
