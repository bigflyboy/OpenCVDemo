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

int OpenCVImpl::DetectFace(){
    VideoCapture capture(0);
    Mat frame;
    
    face_cascade.load(face_cascade_name);
    eyes_cascade.load(eyes_cascade_name);
    
    while(1){
        Mat frame;
        capture>>frame;
        if(frame.empty()){
            break;
        }else{
            detectAndDisplay(frame);
        }
        
        waitKey(10);
    }
    
    return 0;
}

void OpenCVImpl::detectAndDisplay(Mat frame){
    vector<Rect> faces;
    Mat frame_gray;
    
    cvtColor(frame, frame_gray, COLOR_BGR2GRAY);
    equalizeHist(frame_gray, frame_gray);
    
    face_cascade.detectMultiScale(frame_gray, faces, 1.1, 2, 0|CV_HAAR_SCALE_IMAGE, Size(30, 30));
    for(size_t i = 0;i<faces.size();i++){
        Point center(faces[i].x + faces[i].width/2 , faces[i].y + faces[i].height/2);
        ellipse( frame, center, Size( faces[i].width/2, faces[i].height/2), 0, 0, 360, Scalar( 255, 0, 255 ), 2, 8, 0 );
        
        Mat faceROI = frame_gray( faces[i] );
        std::vector<Rect> eyes;
        
        //-- 在脸中检测眼睛
        eyes_cascade.detectMultiScale( faceROI, eyes, 1.1, 2, 0 |CV_HAAR_SCALE_IMAGE, Size(30, 30) );
        
        for( size_t j = 0; j < eyes.size(); j++ )
        {
            Point eye_center( faces[i].x + eyes[j].x + eyes[j].width/2, faces[i].y + eyes[j].y + eyes[j].height/2 );
            int radius = cvRound( (eyes[j].width + eyes[j].height)*0.25 );
            circle( frame, eye_center, radius, Scalar( 255, 0, 0 ), 3, 8, 0 );
        }
    }
    
    imshow(window_name, frame);
}























