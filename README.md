
## 1、需求分析及思路分析
今天新鲜出炉的需求来了：产品要在首页上放置一个悬浮图标，这个图标既起着宣传的作用（图标上面有活动标题），也是一个按钮，点击之后能跳转到某个详情页面。而且为了用户体验更好，在滑动界面时，这个图标要乖乖地藏起来，不能影响用户操作。我仔细分析了一下，哟，这不就是中午点外卖时用的饿了么上面的购物车按钮么？

![悬浮购物车按钮显示](https://user-gold-cdn.xitu.io/2017/9/29/072cf1b3d2dd80ce780e0e20b31ca6fe)


![悬浮购物车按钮半隐藏](https://user-gold-cdn.xitu.io/2017/9/29/c6989087a8b9c7f8049c043776f9a371)

用户没有触摸界面时，购物车就正常悬浮在右下角，当界面滑动时，它就自觉地将自身的一半缩到屏幕之外，而且会变得半透明，不再遮挡底下的内容。到了这一步，相信大家都会想到是用触摸事件来实现了。

```java
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(event);
    }
```
触摸事件有三种，每一步的作用和实现的效果都不一样：
- 手指按下（ACTION_DOWN）：用户手指在屏幕上按下时，记下此时的y坐标作为起始y坐标（startY）；
- 手指抬起（ACTION_UP）：获取此时的坐标作为结束坐标与
- 手指滑动（ACTION_MOVE）：根据手指滑动的距离

## 2、项目创建及布局编写
创建一个新项目，MainActivity的布局如下：
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.lindroid.floatshoppingcart.MainActivity">

   <ListView
       android:id="@+id/listView"
       android:layout_width="match_parent"
       android:layout_height="match_parent">
   </ListView>

   <ImageView
       android:id="@+id/iv_cart"
       android:layout_alignParentBottom="true"
       android:layout_alignParentRight="true"
       android:layout_marginBottom="55dp"
       android:layout_marginRight="20dp"
       android:src="@mipmap/ic_shopping_cart"
       android:layout_width="50dp"
       android:layout_height="50dp" />

</RelativeLayout>

```
右下角的ImageView就是我们的主角，图标是我自己找的购物车图标。为了模拟界面滑动，我在底下简单放了一个ListView，并填充了一些数据。

```java
public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ImageView ivCart;
    private List<String> titles = new ArrayList<>();

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
        listView.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,titles));
    }

}
```

此时的效果如下：
![布局效果图](https://user-gold-cdn.xitu.io/2017/9/29/c412a12b03b2adf15c8b6c4de3b540f2)
布局写完，下面就来实现我们想要的效果了。

## 3、悬浮按钮的动画效果
悬浮按钮的动画效果很简单，就俩：
- 位移动画：悬浮按钮向右平移，直至一半在屏幕之外；
- 渐变动画：悬浮按钮在位移的同时逐渐变得半透明，此处将透明度设为0.5.
### 3.1 位移动画
首先我们要明确悬浮按钮的位移距离，如下图所示：

![平移示意图](https://user-gold-cdn.xitu.io/2017/9/29/6fc911f9c2dd0764618116448a9f6c6b)
悬浮按钮的总位移等于它的右侧到右边屏幕的距离（蓝线），再加上它的半径（紫线）。半径我们可以用
`getMeasuredWidth`获取它的宽度再除于2，那么蓝线的长度呢？

我们无法直接获取控件右侧到右边屏幕的距离，但是我们可以换个思路，先获取整个屏幕的宽度，再减去按钮右侧到左边的距离就行了，而后者可以使用getRight轻松得到。获取手机屏幕宽高可以使用下面的方法：

```java
    private int[] getDisplayMetrics(Context context) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int W = mDisplayMetrics.widthPixels;
        int H = mDisplayMetrics.heightPixels;
        int array[] = {W, H};
        return array;
    }
```

### 3.2 渐变动画
渐变动画就比较简单了，只需要设置起始透明度和结束透明度即可。

### 3.3 动画集合
两种动画是同时开始和结束的，我们可以设置一个动画集合：
```java
    private void hideFloatImage(int distance) {
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
```
动画发生之后不需要归位，所以记得`setFillAfter`要设为true。

### 3.4 悬浮按钮回归原位的动画
前面我们讨论的都是界面滑动，按钮向右隐藏的动画，而用户的手指离开屏幕时，悬浮按钮是要回归原位，这时候的动画效果就跟之前的相反了，所以只需小小修改一下参数即可：
```java
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
        AlphaAnimation al = new AlphaAnimation(1f, 0.5f);
        al.setDuration(300);

        AnimationSet set = new AnimationSet(true);
        //动画完成后不回到原位
        set.setFillAfter(true);
        set.addAnimation(ta);
        set.addAnimation(al);
        ivCart.startAnimation(set);
    }
```

注意一下这里的位移动画的起始坐标。由于补间动画的特性，动画发生位移之后，移动的只是控件的内容，而不是控件本身，所以我们要以控件所在位置为坐标原点，而不是发生位移后的内容！故这里的起始坐标是水平移动的距离，结束坐标是回到坐标原点，也就是0。

## 4、监听手指触摸事件
分析完动画效果之后，我们现在就要来调用了，前面已经说过了是在触摸事件中监听，那么好的，我们现在就将触摸事件和动画的代码集合起来吧：
```java
    private float startY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(startY - event.getY()) > 10) {
                    hideFloatImage(moveDistance);
                }
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showFloatImage(moveDistance);
                            }
                        });
                        return false;
                    }
                }).sendEmptyMessageDelayed(0, 1500);
                break;
        }
        return super.dispatchTouchEvent(event);
    }
```
手指按下时，我们只需要获取到起始坐标。这里要注意的是我们的手指在手机屏幕上的触摸是一个面（手指的与屏幕的接触面积）而不仅仅是一个点，所以`MotionEvent.ACTION_MOVE`是很容易就触发的。为了避免用户手指一按下悬浮按钮就移动，我们可以设置一个值，当手指滑动的距离超过它时才视为有效的滑动。手指抬起时则延迟1.5s再让悬浮图案还原。当然，不要忘了动画是要主线程中进行的。

运行一下，就可以看到如下的效果了：

![初始效果](http://img.blog.csdn.net/20171119212136542?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvTGluZHJvaWQyMA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

## 5、优化动画效果
到现在我们差不多实现了我们想要的效果了，但是如果你试着快速滑动一下就会发现一个可怕的问题：
![快速滑动出现的问题](http://img.blog.csdn.net/20171119212720351?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvTGluZHJvaWQyMA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

频繁滑动时动画就会频繁地触发，甚至你快速滑动几次后，悬浮按钮看起来就像抽了风一样。这显然是不行的，我们接下来就做如下的优化：
1. 当悬浮按钮处于显示状态时，不会触发显示动画，处于隐藏状态时，不会触发隐藏动画；
2. 当手指按下抬起，延时执行显示动画的1.5s内，如果用户再次按下抬起手指，则**中止**之前的动画，并重新计算延迟时间。

### 优化1
这个我们只需要加一个布尔值`isShowFloatImage`来控制即可。每次调用动画判断一下。

### 优化2
之前是通过Handler发送延迟消息来执行动画的，这样无法控制动画的中止。那么现在，我们就需要用另外一种方法来控制显示动画了。这里我选择了Java的计时器`Timer`。当用户的手指抬起时，我们记下当前时间`upTime`，下次用户再次按下手指时将当前时间与`upTime`比较，差值小于1.5s的话则中止动画。

完成上面两步优化之后的代码如下：
```java
    private Timer timer;
    /**用户手指按下后抬起的实际*/
    private long upTime;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (System.currentTimeMillis() - upTime < 1500) {
				    //本次按下距离上次的抬起小于1.5s时，取消Timer
                    timer.cancel();
                }
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(startY - event.getY()) > 10) {
                    if (isShowFloatImage){
                        hideFloatImage(moveDistance);
                    }
                }
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (!isShowFloatImage){
				    //开始1.5s倒计时
                    upTime = System.currentTimeMillis();
                    timer = new Timer();
                    timer.schedule(new FloatTask(), 1500);
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    class FloatTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showFloatImage(moveDistance);
                }
            });
        }
    }
```
再次运行一下，就会发现动画不会频繁地触发，比之前的体验更好了。
