#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include <jni.h>

#include <iostream>
#include <queue>
#include <stdio.h>
#include <math.h>

#include "constants.h"
#include "findEyeCenter.h"
#include "findEyeCorner.h"

using namespace cv;
using namespace std;
/** Constants **/

extern "C" {
/** Function Headers */
int detectAndDisplay( cv::Mat frame );

/** Global variables */
//-- Note, either copy these two files from opencv/data/haarscascades to your current folder, or change these locations
cv::String face_cascade_name = "D:/res/lbpcascade_frontalface.xml";


cv::CascadeClassifier face_cascade;

cv::RNG rng(12345);
cv::Mat debugImage;
cv::Mat skinCrCbHist = cv::Mat::zeros(cv::Size(256, 256), CV_8UC1);

int is_blink(Mat frame) {

  // Load the cascades
  if( !face_cascade.load( face_cascade_name ) ){ printf("--(!)Error loading face cascade, please change face_cascade_name in source code.\n"); exit(-1); };
  

  createCornerKernels();
  ellipse(skinCrCbHist, cv::Point(113, 155.6), cv::Size(23.4, 15.2),
          43.0, 0.0, 360.0, cv::Scalar(255, 255, 255), -1);

  cv::flip(frame, frame, 1);
  frame.copyTo(debugImage);

  imshow("frame",frame);
  //waitKey(0);
  // Apply the classifier to the frame
  if( !frame.empty() ) {
        detectAndDisplay( frame );
  }
  else {
        printf(" --(!) No captured frame -- Break!");
        return -1;
  }

  releaseCornerKernels();
  return 0;
}


 jint  Java_com_camera_test_CameraView_IsBlink
  (JNIEnv* env, jclass cla, jbyteArray buf, int w, int h) {
  jbyte *cbuf;
  cbuf = env->GetByteArrayElements(buf, NULL);
  if (cbuf == NULL) {
    return 0;
  }

  Mat myimg(h, w, CV_8UC4, (unsigned char*) cbuf);
  return is_blink(myimg );
}

//int main( int argc, char** argv )
//{
//	CvCapture* capture;
//	 capture = cvCaptureFromCAM( -1 );
//  if( capture ) {
//    while( true ) {
//		Mat frame = cvQueryFrame( capture );
//		is_blink(frame);
//		int c = cv::waitKey(10);
//	}
//  }
//	//Mat frame = imread("C:/Users/admin/Desktop/6.jpg",1);
//	//is_blink(frame);
//}
int findEyes(cv::Mat frame_gray, cv::Rect face) {
  cv::Mat faceROI = frame_gray(face);
  cv::Mat debugFace = faceROI;

  if (kSmoothFaceImage) {
    double sigma = kSmoothFaceFactor * face.width;
    GaussianBlur( faceROI, faceROI, cv::Size( 0, 0 ), sigma);
  }
  //-- Find eye regions and draw them
  int eye_region_width = face.width * (kEyePercentWidth/100.0);
  int eye_region_height = face.width * (kEyePercentHeight/100.0);
  int eye_region_top = face.height * (kEyePercentTop/100.0);
  cv::Rect leftEyeRegion(face.width*(kEyePercentSide/100.0),
                         eye_region_top,eye_region_width,eye_region_height);
  cv::Rect rightEyeRegion(face.width - eye_region_width - face.width*(kEyePercentSide/100.0),
                          eye_region_top,eye_region_width,eye_region_height);

  //-- Find Eye Centers
  cv::Point leftPupil = findEyeCenter(faceROI,leftEyeRegion,"Left Eye");
  cv::Point rightPupil = findEyeCenter(faceROI,rightEyeRegion,"Right Eye");
  // get corner regions
  cv::Rect leftRightCornerRegion(leftEyeRegion);
  leftRightCornerRegion.width -= leftPupil.x;
  leftRightCornerRegion.x += leftPupil.x;
  leftRightCornerRegion.height /= 2;
  leftRightCornerRegion.y += leftRightCornerRegion.height / 2;
  cv::Rect leftLeftCornerRegion(leftEyeRegion);
  leftLeftCornerRegion.width = leftPupil.x;
  leftLeftCornerRegion.height /= 2;
  leftLeftCornerRegion.y += leftLeftCornerRegion.height / 2;
  cv::Rect rightLeftCornerRegion(rightEyeRegion);
  rightLeftCornerRegion.width = rightPupil.x;
  rightLeftCornerRegion.height /= 2;
  rightLeftCornerRegion.y += rightLeftCornerRegion.height / 2;
  cv::Rect rightRightCornerRegion(rightEyeRegion);
  rightRightCornerRegion.width -= rightPupil.x;
  rightRightCornerRegion.x += rightPupil.x;
  rightRightCornerRegion.height /= 2;
  rightRightCornerRegion.y += rightRightCornerRegion.height / 2;

 //blink_detection
  cv::Rect leftEye(leftLeftCornerRegion.x,leftLeftCornerRegion.y,
	  leftRightCornerRegion.x+leftRightCornerRegion.width-leftLeftCornerRegion.x,
	  leftRightCornerRegion.y+leftRightCornerRegion.height-leftLeftCornerRegion.y);
  cv::Rect rightEye(rightLeftCornerRegion.x,rightLeftCornerRegion.y,
	  rightRightCornerRegion.x+rightRightCornerRegion.width-rightLeftCornerRegion.x,
	  rightRightCornerRegion.y+rightRightCornerRegion.height-rightLeftCornerRegion.y);
  
  cv::Mat leftROI = debugFace(leftEye);
  cv::Mat rightROI = debugFace(rightEye);

  int scale = 9.5;

  rectangle(debugFace,Rect(rightEye.x+rightEye.width/scale,rightEye.y+rightEye.height/scale,rightEye.width-2*rightEye.width/scale,rightEye.height-2*rightEye.height/scale),200);
  rectangle(debugFace,rightEye,200);

  rectangle(debugFace,Rect(leftEye.x+leftEye.width/scale,leftEye.y+leftEye.height/scale,leftEye.width-2*leftEye.width/scale,leftEye.height-2*leftEye.height/scale),200);
  rectangle(debugFace,leftEye,200);
  
  
  // change eye centers to face coordinates
  rightPupil.x += rightEyeRegion.x;
  rightPupil.y += rightEyeRegion.y;
  leftPupil.x += leftEyeRegion.x;
  leftPupil.y += leftEyeRegion.y;
  // draw eye centers
  circle(debugFace, rightPupil, 3, 1234);
  circle(debugFace, leftPupil, 3, 1234);

  imshow("abcdc", faceROI);
  //waitKey(0);
  
  if(rightPupil.inside(Rect(rightEye.x+rightEye.width/scale,rightEye.y+rightEye.height/scale,rightEye.width-2*rightEye.width/scale,rightEye.height-2*rightEye.height/scale))
	  && leftPupil.inside(Rect(leftEye.x+leftEye.width/scale,leftEye.y+leftEye.height/scale,leftEye.width-2*leftEye.width/scale,leftEye.height-2*leftEye.height/scale)))
  {
	   cout<<"open eyes"<<endl;
	   return 1;
  }
  else if(rightPupil.inside(Rect(rightEye.x+rightEye.width/scale,rightEye.y+rightEye.height/scale,rightEye.width-2*rightEye.width/scale,rightEye.height-2*rightEye.height/scale))
	  || leftPupil.inside(Rect(leftEye.x+leftEye.width/scale,leftEye.y+leftEye.height/scale,leftEye.width-2*leftEye.width/scale,leftEye.height-2*leftEye.height/scale)))
  {
	   cout<<"twinkle eyes"<<endl;
	   return 2;
  }
  else
  {
	  cout<<"closed eyes"<<endl;
	  return 3;
  }
}
/**
 * @function detectAndDisplay
 */
int detectAndDisplay( cv::Mat frame ) {
  std::vector<cv::Rect> faces;
  //cv::Mat frame_gray;

 /* std::vector<cv::Mat> rgbChannels(3);
  cv::split(frame, rgbChannels);
  cv::Mat frame_gray = rgbChannels[2];*/

  Mat frame_gray;
  cvtColor(frame, frame_gray, CV_BGR2GRAY);

  //-- Detect faces
  face_cascade.detectMultiScale( frame_gray, faces, 1.1, 2, 0|CV_HAAR_SCALE_IMAGE|CV_HAAR_FIND_BIGGEST_OBJECT, cv::Size(150, 150) );

  for( int i = 0; i < faces.size(); i++ )
  {
    rectangle(debugImage, faces[i], 1234);
  }
  //-- Show what you got
  if (faces.size() > 0) {
    return findEyes(frame_gray, faces[0]);
  }
  else
  {
	  cout<<"no face"<<endl;
	  return -2;
  }	  
}
}
