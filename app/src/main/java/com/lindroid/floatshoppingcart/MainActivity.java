package com.lindroid.floatshoppingcart;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linyulong
 */
public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ImageView ivCart;
    private List<String> titles = new ArrayList<>();
    private boolean isShowFloatImage = true;
    private float startY;
    private int moveDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        for (int i = 0; i < 60; i++) {
            titles.add(new StringBuffer("这是一条数据").append(i).toString());
        }
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listView);
        ivCart = (ImageView) findViewById(R.id.iv_cart);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles));

        ivCart.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("Tag", "控件宽度" + ivCart.getWidth());
                moveDistance = getDisplayMetrics(MainActivity.this)[0] - ivCart.getRight() + ivCart.getWidth() / 2;
                //监听结束之后移除监听事件
                ivCart.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                int i = 0;
                while (i < 5) {
                    hideFloatImage(moveDistance);
                    i++;
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.e("Tag","startY="+startY+",event.getY()="+event.getY()+",相差"+Math.abs(startY - event.getY()));
//                if (Math.abs(startY - event.getY()) > 10) {
//                    Log.e("Tag","隐藏动画调用");
//                    hideFloatImage(moveDistance);
//                }
//                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
//                new Handler(new Handler.Callback() {
//                    @Override
//                    public boolean handleMessage(Message msg) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                showFloatImage(moveDistance);
//                            }
//                        });
//                        return false;
//                    }
//                }).sendEmptyMessageDelayed(0, 1500);

                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }


    /**
     * 显示悬浮图标
     *
     * @param distance
     */
    private void showFloatImage(int distance) {
        isShowFloatImage = false;
        //位移动画
        TranslateAnimation ta = new TranslateAnimation(
                distance,//起始x坐标
                0,//结束x坐标
                0,//起始y坐标
                0);//结束y坐标（正数向下移动）
        ta.setDuration(300);

        //渐变动画
        AlphaAnimation al = new AlphaAnimation(0.5f, 1f);
        al.setDuration(300);

        AnimationSet set = new AnimationSet(true);
        //动画完成后不回到原位
        set.setFillAfter(true);
        set.addAnimation(ta);
        set.addAnimation(al);
        ivCart.startAnimation(set);
    }

    /**
     * 隐藏悬浮图标
     *
     * @param distance
     */
    private void hideFloatImage(int distance) {
        Log.e("Tag", "动画执行");
        isShowFloatImage = false;
        //位移动画
        TranslateAnimation ta = new TranslateAnimation(
                0,//起始x坐标,10表示与初始位置相距10
                distance,//结束x坐标
                0,//起始y坐标
                0);//结束y坐标（正数向下移动）
        ta.setDuration(300);

        //渐变动画
        AlphaAnimation al = new AlphaAnimation(1f, 0.5f);
        al.setDuration(300);

        AnimationSet set = new AnimationSet(true);
        //动画完成后不回到原位
        set.setFillAfter(true);
        set.addAnimation(ta);
        set.addAnimation(al);
        ivCart.startAnimation(set);

    }

    private int[] getDisplayMetrics(Context context) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int widthPixels = mDisplayMetrics.widthPixels;
        int heightPixels = mDisplayMetrics.heightPixels;
        int[] array = {widthPixels, heightPixels};
        return array;
    }
}
