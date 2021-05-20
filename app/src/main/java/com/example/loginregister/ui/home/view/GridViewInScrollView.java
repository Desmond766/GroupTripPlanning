package com.example.loginregister.ui.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class GridViewInScrollView extends GridView{

	public boolean hasScrollBar = true;
	public GridViewInScrollView(Context context) {
		super(context);
	}

	public GridViewInScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GridViewInScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = heightMeasureSpec;
		if (hasScrollBar) {
			expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);//根据子控件的高度设置高度
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
