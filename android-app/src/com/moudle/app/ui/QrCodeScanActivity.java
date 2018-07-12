package com.moudle.app.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;

import com.moudle.app.AppContext;
import com.moudle.app.R;
import com.moudle.app.base.BaseActivity;
import com.moudle.app.common.UIHelper;
import com.moudle.app.widget.zxing.camera.CameraManager;
import com.moudle.app.widget.zxing.hanler.InitScan;
import com.moudle.app.widget.zxing.hanler.IsChineseOrNot;
import com.moudle.app.widget.zxing.view.ViewfinderView;
import com.google.zxing.Result;
import com.moudle.app.widget.zxing.hanler.CaptureActivityHandler;


/**
 * @Description 二维码扫描Activity
 * @Author Li Chao
 * @Date 2015/12/10 11:23
 */
public class QrCodeScanActivity extends BaseActivity {
    private AppContext appContext;
    private CaptureActivityHandler mHandler;// 相机Activity Handler
    private SurfaceView mSurfaceView;
    public InitScan initScan;
    private ViewfinderView viewfinderView;
    private InitScan.ScanHandler scanHandler = new InitScan.ScanHandler() {
        @Override
        public void handleDecode(Result result, Bitmap bitmap) {
            String resultString = result.getText();
            String utf = IsChineseOrNot.resultString(resultString);
            if (utf.length() == 10) {
                //   UIHelper.ToastMessage(QrCodeScanActivity.this, utf);
                setResult(RESULT_OK,new Intent().putExtra("objectId",utf));
                finish();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initView();
        initData();
        appContext = (AppContext) getApplicationContext();
    }

    @Override
    public void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.preview_view);
        viewfinderView = (ViewfinderView) findViewById(R.id.scan_finderView);
        //((TextView) findViewById(R.id.acitivity_name)).setText(R.string.qr_code_scan);
    }

    @Override
    public void initData() {
        if (initScan == null) {
            initScan = new InitScan(this, viewfinderView, mSurfaceView);
        }
       /* if (mHandler == null) {// 用于刷新扫描
            mHandler = (CaptureActivityHandler) initScan.getHandler();
        }
        mHandler.restartPreviewAndDecode();*/
        initScan.setScanHandler(scanHandler);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        if (mHandler == null) {
            mHandler = (CaptureActivityHandler) initScan.getHandler();
            mHandler.quitSynchronously();
            mHandler = null;
        }
        CameraManager.get().closeDriver();
        initScan.inactivityTimer.shutdown();
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private View.OnClickListener red_pack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }
    };
}
