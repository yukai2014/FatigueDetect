#include <iostream>
#include "cv.h"
#include "cxcore.h"
#include "highgui.h"
using namespace std;

CvSeq *getImageContours(CvArr *src)
{
	cvThreshold(src, src, 100, 255, CV_THRESH_BINARY);
	CvMemStorage * storage = cvCreateMemStorage(0);
	CvSeq * contours;
	cvFindContours(src, storage, &contours);
	return contours;
}
int main()
{
	IplImage *src1 = cvLoadImage("C:/Users/admin/Desktop/close.jpg", 0);
	CvSeq *contours1 = getImageContours(src1);  // 得到src1的轮廓

	IplImage *src2 = cvLoadImage("C:/Users/admin/Desktop/close.jpg", 0);

	CvSize sz;
    double scale = 5;
    sz.width = src2->width*scale;
    sz.height = src2->height*scale;
    IplImage *desc = cvCreateImage(sz,src2->depth,src2->nChannels);
    cvResize(src2,desc,CV_INTER_CUBIC);

	CvSeq *contours2 = getImageContours(desc);
	double result = cvMatchShapes(contours1, contours2, 1);   // 根据输入的图像或轮廓来计算它们的hu矩的相似度
	cout << "lala"<<result << endl;
	cvReleaseMemStorage(&contours1->storage);
	cvReleaseMemStorage(&contours2->storage);
	cvReleaseImage(&src1);
	cvReleaseImage(&src2);
	return 0;
}
