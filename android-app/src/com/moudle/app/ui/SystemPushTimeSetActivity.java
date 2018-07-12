package com.moudle.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.moudle.app.AppContext;
import com.moudle.app.R;

/**
 * @Description 推送时间设置Activity
 * @Author Li Chao
 * @Date 2015/12/10 11:23
 */
public class SystemPushTimeSetActivity extends Activity {

    private TimePicker mStartPushTime;
    private TimePicker mEndPushTime;
    private Button mBtnok;
    private Button mBtncancel;
    private AppContext sContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zh_system_timesetting);
        sContext = (AppContext) getApplication();
        init();
    }

    private void init() {
        mStartPushTime = (TimePicker) findViewById(R.id.time_start_push);
        mEndPushTime = (TimePicker) findViewById(R.id.time_end_push);
        mStartPushTime.setIs24HourView(true);
        mEndPushTime.setIs24HourView(true);

        mStartPushTime.setCurrentHour(Integer.parseInt(sContext.getRecommendsBeginTime().split(":")[0]));
        mStartPushTime.setCurrentMinute(Integer.parseInt(sContext.getRecommendsBeginTime().split(":")[1]));

        mEndPushTime.setCurrentHour(Integer.parseInt(sContext.getRecommendsEndTime().split(":")[0]));
        mEndPushTime.setCurrentMinute(Integer.parseInt(sContext.getRecommendsEndTime().split(":")[1]));


        mBtnok = (Button) findViewById(R.id.btn_ok);
        mBtnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //数据是使用Intent返回
                Intent intent = new Intent();
                //把返回数据存入Intent
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                try {

                    Date d = sdf.parse(mStartPushTime.getCurrentHour() + ":" + mStartPushTime.getCurrentMinute());
                    intent.putExtra("begintime", sdf.format(d));
                    d = sdf.parse(mEndPushTime.getCurrentHour() + ":" + mEndPushTime.getCurrentMinute());
                    intent.putExtra("endtime", sdf.format(d));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //设置返回数据
                setResult(RESULT_OK, intent);
                //关闭Activity
                finish();
            }
        });

        mBtncancel = (Button) findViewById(R.id.btn_cancel);
        mBtncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置返回数据
                setResult(RESULT_CANCELED, null);
                //关闭Activity
                finish();
            }
        });

    }


}
