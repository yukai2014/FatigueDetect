

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);// ���ú���ģʽ�Լ�ȫ��ģʽ
 
        view = new CameraView(this);// ͨ��һ��surfaceview��view��ʵ������
        view.setId(1);
        view = new CameraView(this, this);
        setContentView(R.layout.camera_layout);
        RelativeLayout relative = (RelativeLayout) this.findViewById(R.id.ly);
        RelativeLayout.LayoutParams Layout = new RelativeLayout.LayoutParams(3, 3);// ����surfaceviewʹ�����������޷��ۿ�Ԥ��
        Layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
        Layout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
 
        relative.addView(view, Layout);
 
    }
 
}
 
 
 
 

 
//��3��camera_layout.xml
 
