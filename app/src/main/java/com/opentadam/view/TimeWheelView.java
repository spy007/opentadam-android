/*
 * Copyright (C) 2019 TadamGroup, LLC.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.opentadam.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.opentadam.Injector;
import com.opentadam.R;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ALL")
public class TimeWheelView extends ScrollView {
    public static final String TAG = TimeWheelView.class.getSimpleName();
    public static final int OFF_SET_DEFAULT = 1;
    private static final int SCROLL_DIRECTION_UP = 0;
    //    private ScrollView scrollView;
    private static final int SCROLL_DIRECTION_DOWN = 1;
    //    String[] items;
    List<String> items;
    int offset = OFF_SET_DEFAULT;
    int displayItemCount;
    int selectedIndex = 1;
    int initialY;
    Runnable scrollerTask;
    int newCheck = 50;

    public int getItemHeight() {
        return itemHeight;
    }

    int itemHeight = 0;
    int[] selectedAreaBorder;
    Paint paint;
    int viewWidth;
    public Context context;
    private LinearLayout views;
    private int scrollDirection = -1;
    private OnTimeWheelViewListener onTimeWheelViewListener;


    private int colorItem = Injector.getAppContext().getResources().getColor(R.color.text_default_color);
    private int colorItemSecond = Injector.getAppContext().getResources().getColor(R.color.text_sec_time);
    private int colorDivider = Injector.getAppContext().getResources().getColor(R.color.time_devider);
    private int colorSecondLinght = Injector.getAppContext().getResources().getColor(R.color.text_sec);

    public TimeWheelView(Context context) {
        super(context);
        init(context);
    }

    public TimeWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimeWheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setColorItem(int colorItem) {
        this.colorItem = colorItem;
    }

    public void setColorItemSecond(int colorItemSecond) {
        this.colorItemSecond = colorItemSecond;
    }

    public void setColorDivider(int colorDivider) {
        this.colorDivider = colorDivider;
    }

    private List<String> getItems() {
        return items;
    }

    public void setItems(List<String> list) {
        if (null == items) {
            items = new ArrayList<String>();
        }
        items.clear();
        items.addAll(list);

        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }

        initData();

    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void init(Context context) {
        this.context = context;

        this.setVerticalScrollBarEnabled(false);

        setViewsN(context);


    }

    private void setViewsN(Context context) {
        views = new LinearLayout(context);
        views.setOrientation(LinearLayout.VERTICAL);
        this.addView(views);

        scrollerTask = new Runnable() {

            public void run() {

                int newY = getScrollY();
                if (initialY - newY == 0) { // stopped
                    final int remainder = initialY % itemHeight;
                    final int divided = initialY / itemHeight;

                    if (remainder == 0) {
                        selectedIndex = divided + offset;

                        onSeletedCallBack();
                    } else {
                        if (remainder > itemHeight / 2) {
                            TimeWheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    TimeWheelView.this.smoothScrollTo(0, initialY - remainder + itemHeight);
                                    selectedIndex = divided + offset + 1;
                                    onSeletedCallBack();
                                }
                            });
                        } else {
                            TimeWheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    TimeWheelView.this.smoothScrollTo(0, initialY - remainder);
                                    selectedIndex = divided + offset;
                                    onSeletedCallBack();
                                }
                            });
                        }


                    }


                } else {
                    initialY = getScrollY();
                    TimeWheelView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    public void startScrollerTask() {

        initialY = getScrollY();
        this.postDelayed(scrollerTask, newCheck);
    }

    private void initData() {
        displayItemCount = offset * 2 + 1;

        for (String item : items) {
            views.addView(createView(item));
        }

        refreshItemView(0);
    }

    private TextView createView(String item) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setSingleLine(true);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        String custom_font = "fonts/Roboto/Roboto-Light.ttf";
        Typeface CF = Typeface.createFromAsset(Injector.getAppContext().getAssets(), custom_font);
        tv.setTypeface(CF);
        if (item.length() == 5) {
            item = item.replace(":", "      :      ");
        }
        tv.setText(item);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        int padding = dip2px(10);
        tv.setPadding(0, padding, padding, padding);
        if (0 == itemHeight) {
            itemHeight = getViewMeasuredHeight(tv);
            views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
            this.setLayoutParams(new LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount));
        }
        return tv;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        refreshItemView(t);

        if (t > oldt) {

            scrollDirection = SCROLL_DIRECTION_DOWN;
        } else {

            scrollDirection = SCROLL_DIRECTION_UP;

        }


    }

    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;

        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }

        }

        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }
            if (position == i) {
                itemView.setTextColor(colorItem);
            } else if (i > position + 1 || i < position - 1) {
                itemView.setTextColor(colorSecondLinght);
            } else {
                itemView.setTextColor(colorItemSecond);
            }
        }
    }

    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = itemHeight * offset;
            selectedAreaBorder[1] = itemHeight * (offset + 1);
        }
        return selectedAreaBorder;
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {

        if (viewWidth == 0) {
            viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        }

        if (null == paint) {
            paint = new Paint();
            paint.setColor(colorDivider);
            paint.setStrokeWidth(dip2px(1f));
        }

        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawLine(viewWidth * 0, obtainSelectedAreaBorder()[0], viewWidth * 6 / 6, obtainSelectedAreaBorder()[0], paint);
                canvas.drawLine(viewWidth * 0, obtainSelectedAreaBorder()[1], viewWidth * 6 / 6, obtainSelectedAreaBorder()[1], paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSPARENT;
            }
        };


        super.setBackgroundDrawable(background);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }


    private void onSeletedCallBack() {
        if (null != onTimeWheelViewListener) {
            onTimeWheelViewListener.onSelected(selectedIndex, items.get(selectedIndex));
        }

    }

    public void setSeletion(int position) {
        final int p = position;
        selectedIndex = p + offset;
        this.post(new Runnable() {
            @Override
            public void run() {
                TimeWheelView.this.smoothScrollTo(0, p * itemHeight);
            }
        });

    }

    public String getSeletedItem() {
        return items.get(selectedIndex);
    }

    public int getSeletedIndex() {
        return selectedIndex - offset;
    }


    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {

            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }

    public OnTimeWheelViewListener getOnTimeWheelViewListener() {
        return onTimeWheelViewListener;
    }

    public void setOnTimeWheelViewListener(OnTimeWheelViewListener onTimeWheelViewListener) {
        this.onTimeWheelViewListener = onTimeWheelViewListener;
    }

    private int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int getViewMeasuredHeight(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }

    public static class OnTimeWheelViewListener {
        public void onSelected(int selectedIndex, String item) {
        }
    }

}