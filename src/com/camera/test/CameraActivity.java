

package com.camera.test;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
 
//http://www.apkbus.com/android-69728-2-1.html

public class CameraActivity extends Activity {
    private CameraView view;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置横屏模式以及全屏模式
 
        view = new CameraView(this);// 通过一个surfaceview的view来实现拍照
        view.setId(1);
        view = new CameraView(this, this);
        setContentView(R.layout.camera_layout);
        RelativeLayout relative = (RelativeLayout) this.findViewById(R.id.ly);
        RelativeLayout.LayoutParams Layout = new RelativeLayout.LayoutParams(3, 3);// 设置surfaceview使其满足需求无法观看预览
        Layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
        Layout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
 
        relative.addView(view, Layout);
 
    }
 
}
 
 
 
 

 
//（3）camera_layout.xml
 
