//
//  main.cpp
//  OpencvOnMac
//
//  Created by 王志远 on 2017/3/25.
//  Copyright © 2017年 王志远. All rights reserved.
//

#include <iostream>
#include "OpenCVImpl.hpp"

//char* url = "/Users/wangzhiyuan/Documents/image/dog.jpg";
string imageUrl = string("/Users/wangzhiyuan/Documents/image/dog.jpg");
string videoUrl = string("/Users/wangzhiyuan/Documents/avi/me.mov");

int main(int argc, const char * argv[]) {
    OpenCVImpl* cvImpl = new OpenCVImpl();
    cvImpl->DetectFace();
    delete cvImpl;
    return 0;
}
