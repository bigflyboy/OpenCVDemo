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
};

#endif /* OpenCVImpl_hpp */
