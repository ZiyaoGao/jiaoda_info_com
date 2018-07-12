package com.moudle.app.widget;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.moudle.app.R;
import com.moudle.app.common.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 头部标题模块
 */

public class TitleModel {
	Activity mActivity;
	private LinearLayout toptitle_left;
	private LinearLayout toptitle_right;
	private LinearLayout toptitle_center;
	private View title_line_tips;
	private View titleView;
	private List<View> leftViews=new ArrayList<>();
	private List<View> rightViews=new ArrayList<>();
	private List<View> centerViews=new ArrayList<>();
	public TitleModel(Activity activity) {
		super();
		this.mActivity = activity;
		titleView = View.inflate(mActivity, R.layout.toptitle, null);
		toptitle_left = (LinearLayout) titleView
				.findViewById(R.id.toptitle_left);
		toptitle_right = (LinearLayout) titleView
				.findViewById(R.id.toptitle_right);
		toptitle_center = (LinearLayout) titleView
				.findViewById(R.id.toptitle_center);
		title_line_tips = titleView.findViewById(R.id.title_line_tips);
	}
	public TitleModel(Activity activity, boolean isVertical) {
		super();
		this.mActivity = activity;
		this.isVertical=isVertical;
		titleView = View.inflate(mActivity, R.layout.toptitle, null);
		toptitle_left = (LinearLayout) titleView
				.findViewById(R.id.toptitle_left);
		toptitle_right = (LinearLayout) titleView
				.findViewById(R.id.toptitle_right);
		toptitle_center = (LinearLayout) titleView
				.findViewById(R.id.toptitle_center);
	}
	public View getLeftView(int i){
		return 	toptitle_left.getChildAt(i);
	}
	public View getCenterView(int i){
		return toptitle_center.getChildAt(i);
	}
	public View getTitleLine(){
		return title_line_tips;
	}
	public View getRightView(int i){
		return toptitle_right.getChildAt(i);
	}
	public LinearLayout getLeftViewGroup(){
		return 	toptitle_left;
	}
	public LinearLayout getCenterViewGroup(){
		return toptitle_center;
	}
	public LinearLayout getRightViewGroup(){
		return toptitle_right;
	}
	public void clearLeft(){
		toptitle_left.removeAllViews();
	}
	public void clearCenter(){
		toptitle_center.removeAllViews();
	}
	public void clearRight(){
		toptitle_right.removeAllViews();
	}
	/**
	 * 提交
	 * @param
	 */
	public void submit(){

			titleView.setBackgroundColor(0Xffffffff);
		//左边添加view
		for (View view: leftViews){
			toptitle_left.addView(view);
		}
		//中间添加view
		for (View view: centerViews){
			toptitle_center.addView(view);
		}
		//右边添加view
		for (View view: rightViews){
			toptitle_right.addView(view);
		}
		rightViews.clear();
		centerViews.clear();
		leftViews.clear();
	}
	public View getTitleView() {
		return titleView;
	}
	private  boolean isVertical=true;

	public boolean isVertical() {
		return isVertical;
	}

	private int defMargin;
	private int defpadding;

	public TitleModel putRightView(View view) {

		rightViews.add(view);
		return this;
	}
	public TitleModel putCenterView(View view) {
		centerViews.add(view);
		return this;
	}
	public TitleModel putLeftView(View view){
		leftViews.add(view);
		return this;
	}
	private void initDefaultParams(LayoutParams layoutParams){
		defMargin=ScreenUtils.convertDIP2PX(mActivity, 2);
		defpadding=ScreenUtils.convertDIP2PX(mActivity, 3);
		layoutParams.setMargins(defMargin, defMargin, ScreenUtils.convertDIP2PX(mActivity, 10), defMargin);
	}
	/**
	 * 通过此方法生成并返回左边textview imageview imagebutton button
	 *
	 * @param t
	 * @return
	 */
	public <T> T creatLeftView(Class<T> t) {
		LayoutParams leftLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		initDefaultParams(leftLayoutParams);
		if (t == TextView.class) {

			TextView textView = new TextView(mActivity);
			textView.setTextSize(18);
			textView.setTextColor(mActivity.getResources().getColor(R.color.title_name_color));
			textView.setLayoutParams(leftLayoutParams);
			textView.setPadding(defpadding,
					defpadding, defpadding,
					defpadding);
			textView.setGravity(Gravity.CENTER);
			leftViews.add(textView);
			return (T) textView;
		} else if (t == ImageView.class) {

			ImageView imageView = new ImageView(mActivity);
			leftLayoutParams.setMargins(defMargin, defMargin, defMargin, defMargin);
			imageView.setLayoutParams(leftLayoutParams);
			imageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mActivity.finish();
				}
			});
			imageView.setImageResource(R.drawable.btn_header_return);
			leftViews.add(imageView);

			return (T) imageView;
		} else if (t == ImageButton.class) {
			ImageButton imageButton = new ImageButton(mActivity);
			imageButton.setLayoutParams(leftLayoutParams);
			imageButton.setPadding(defpadding,
					defpadding,defpadding,
					defpadding);
			leftViews.add(imageButton);
			return (T) imageButton;
		} else if (t == Button.class) {
			Button button = new Button(mActivity);
			button.setPadding(defpadding,
					defpadding, defpadding,
					defpadding);
			button.setLayoutParams(leftLayoutParams);
			button.setGravity(Gravity.CENTER);
			leftViews.add(button);
			return (T) button;
		} else {
			throw new IllegalArgumentException("不支持的头部控件类型");
		}

	}
	/**
	 * 通过此方法生成并返回右边textview imageview imagebutton button
	 *
	 * @param t
	 * @return
	 */
	public <T> T creatRightView(Class<T> t) {
		LayoutParams rightLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		initDefaultParams(rightLayoutParams);
		if (t == TextView.class) {
			TextView textView = new TextView(mActivity);
			textView.setTextSize(18);
			textView.setTextColor(mActivity.getResources().getColor(R.color.title_name_color));
			textView.setLayoutParams(rightLayoutParams);
			textView.setGravity(Gravity.CENTER);
			textView.setPadding(defpadding,
					defpadding, defpadding,
					defpadding);
			rightViews.add(textView);
			return (T) textView;
		} else if (t == ImageView.class) {
			ImageView imageView = new ImageView(mActivity);
			imageView.setImageResource(R.drawable.demo_tribe_red_icon);
			imageView.setLayoutParams(rightLayoutParams);
			rightViews.add(imageView);
			return (T) imageView;
		} else if (t == ImageButton.class) {
			ImageButton imageButton = new ImageButton(mActivity);
			imageButton.setLayoutParams(rightLayoutParams);
			imageButton.setPadding(defpadding,
					defpadding, defpadding,
					defpadding);
			rightViews.add(imageButton);
			return (T) imageButton;
		} else if (t == Button.class) {
			Button button = new Button(mActivity);
			button.setPadding(defpadding,
					defpadding, defpadding,
					defpadding);
			button.setLayoutParams(rightLayoutParams);
			button.setGravity(Gravity.CENTER);
			rightViews.add(button);
			return (T) button;
		} else {
			throw new IllegalArgumentException("不支持的头部控件类型");
		}

	}
	/**
	 * 通过此方法生成并返回中间textview imageview imagebutton button
	 *
	 * @param t
	 * @return
	 */
	public <T> T creatCenterView(Class<T> t) {
		LayoutParams centerLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		initDefaultParams(centerLayoutParams);
		if (t == TextView.class) {
			TextView textView = new TextView(mActivity);
			textView.setTextSize(18);
			textView.setTextColor(mActivity.getResources().getColor(R.color.title_name_color));
			textView.setPadding( defpadding,defpadding,defpadding,defpadding);
			textView.setLayoutParams(centerLayoutParams);
			textView.setGravity(Gravity.CENTER);
			centerViews.add(textView);
			return (T) textView;
		} else if (t == ImageView.class) {
			ImageView imageView = new ImageView(mActivity);
			imageView.setLayoutParams(centerLayoutParams);
			centerViews.add(imageView);
			return (T) imageView;
		} else if (t == EditText.class) {
			EditText edit = new EditText(mActivity);
			edit.setLayoutParams(centerLayoutParams);
			edit.setPadding(defpadding,
					defpadding, defpadding,
					defpadding);
			centerViews.add(edit);
			return (T) edit;
		} else {
			throw new IllegalArgumentException("不支持的头部控件类型");
		}

	}

	
}
