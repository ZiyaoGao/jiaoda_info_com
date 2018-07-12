package com.moudle.app.common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public class MessageHelper {

	public static final int MSG_SHOW_TOAST_TEXT = 0x01;

	public static void sendMessage(Handler handler, final int what, Object obj,
			final int arg1) {
		Message msg = new Message();
		msg.what = what;
		msg.obj = obj;
		msg.arg1 = arg1;
		handler.sendMessage(msg);
	}

	public static void sendMessage(Handler handler, final int what, Object obj,
			final int arg1, final int arg2) {
		Message msg = new Message();
		msg.what = what;
		msg.obj = obj;
		msg.arg1 = arg1;
		msg.arg2 = arg2;
		handler.sendMessage(msg);
	}

	public static void sendMessage(Handler handler, final int what) {
		sendMessage(handler, what, null, 0);
	}

	public static void sendMessage(Handler handler, final int what, Object obj) {
		sendMessage(handler, what, obj, 0);
	}

	public static void sendMessage(Handler handler, Object obj) {
		Message msg = new Message();
		msg.obj = obj;
		handler.sendMessage(msg);
	}

	public static void sendMessage(Handler handler, final int what, Bundle data) {
		Message msg = new Message();
		msg.what = what;
		msg.setData(data);
		handler.sendMessage(msg);
	}

	public static class GlobalMsgHandler extends Handler {
		public void handleMessage(Message msg) {
			try {		
			} catch (Exception e) {
			}
		}
	}


}