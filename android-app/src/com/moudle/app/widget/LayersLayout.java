package com.moudle.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class LayersLayout extends LinearLayout {
    /**
     * 自定义图层
     */
    public ViewFlow viewFlow;
    private String TAG = "moudle";
    public boolean onHorizontal = false;
    float x = 0.0f;
    float y = 0.0f;

    private ViewFlowOutEvent mViewFlowOutEvent;

    public LayersLayout(Context context) {
        super(context);
    }

    public LayersLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setView(ViewFlow viewFlow) {
        this.viewFlow = viewFlow;
    }

    // 对触屏事件进行重定向
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("LayerMotionEvent", String.valueOf(ev.getAction()));
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onHorizontal = false;
                x = ev.getX();
                y = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                onHorizontal = false;
                break;
            default:
                break;
        }

        if (ViewFlow.onTouch) {
            float dx = Math.abs(ev.getX() - x);
            float dy = Math.abs(ev.getY() - y);
            Log.e("MyViewFlow", "dx:=" + dx);
            Log.e("MyViewFlow", "dy:=" + dy);
            if (dx > 20) {
                onHorizontal = true;
            }
            if (onHorizontal) {
                Log.e(TAG, "viewFlow处理");
                return true;
            } else {
                if (mViewFlowOutEvent != null && dy == 0 && ev.getAction() == MotionEvent.ACTION_UP) {//dy==0 因为下拉动作 所以做限制
                    mViewFlowOutEvent.onClick();
                }
                Log.e(TAG, "listview处理");
                return super.onInterceptTouchEvent(ev);
            }
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    // 对触屏事件进行处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("LayersLayoutEvent：", String.valueOf(event.getAction()));
        if (ViewFlow.onTouch) {
            return viewFlow.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    public interface ViewFlowOutEvent {
        void onClick();
    }

    public void setmViewFlowOutEvent(ViewFlowOutEvent mViewFlowOutEvent) {
        this.mViewFlowOutEvent = mViewFlowOutEvent;
    }
}
