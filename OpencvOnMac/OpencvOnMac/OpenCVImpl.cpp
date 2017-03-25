//
//  OpenCVImpl.cpp
//  OpencvOnMac
//
//  Created by 王志远 on 2017/3/25.
//  Copyright © 2017年 王志远. All rights reserved.
//

#include "OpenCVImpl.hpp"

void OpenCVImpl::DisplayImage(string url){
    Mat srcImage = imread(url);
    imshow("[原始图]", srcImage);
    waitKey(0);
}

void OpenCVImpl::ErodeImage(string url){
    Mat srcImage = imread(url);
    imshow("【原图】腐蚀操作", srcImage);
    Mat element = getStructuringElement(MORPH_RECT, Size(15,15));
    Mat dstImage;
    erode(srcImage, dstImage, element);
    
    imshow("腐蚀之后", dstImage);
    waitKey(0);
    
}

void OpenCVImpl::BlurImage(string url){
    Mat srcImage = imread(url);
    Mat dstImage;
    blur(srcImage, dstImage, Size(7,7));
    
    imshow("均值滤波", dstImage);
    waitKey(0);
}

void OpenCVImpl::CannyImage(string url){
    Mat srcImage = imread(url);
    Mat grayImage, dstImage;
    //先转灰度图
    cvtColor(srcImage, grayImage, CV_BGR2GRAY);
    
    //均值滤波来降噪
    blur(grayImage, dstImage, Size(3,3));
    
    Canny(dstImage, dstImage,3,9,3);
    imshow("Canny边缘检测", dstImage);
    waitKey(0);
}

void OpenCVImpl::VideoDisplay(string url){
    VideoCapture capture(url);
    
    while(1){
        Mat frame;
        capture>>frame;
        if(frame.empty()){
            break;
        }
        
        imshow("videodisplay", frame);
        waitKey(15);
    }
}

void OpenCVImpl::CaptureDisplay(){
    VideoCapture capture(0);
    Mat edges;
    while(1){
        Mat frame;
        capture>>frame;
        if(frame.empty()){
            break;
        }
        cvtColor(frame, edges, CV_BGRA2GRAY);
        
        blur(edges,edges,Size(7,7));
        Canny(edges,edges,0,30,3);
        
        imshow("", edges);
        waitKey(30);
    }
}












