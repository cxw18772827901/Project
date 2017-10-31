package com.dave.project.wedjet;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dave.project.R;
import com.dave.project.base.Constants;

import java.util.List;

/*
 需求
 A 轮播图的作用就是显示图片和标题
 B 显示图片
 C 设置标题
 D 设置圆点
 E 定时滚动
 F 对外暴露方法，让外界控制是否滚动
 G 让外部知道当前被点击的条目的角标
 分析
 A 要提供方法，让外界传入要显示的内容的集合 // 使用接口
 B ViewPager 设置adapter
 C 可以对viewpager设置变化监听，在监听中改变textview的标题
 D 在setItems方法中往容器里添加一些圆点，在opcl中改变圆点的enable状态
 E 使用handler，postDelay，在Runnable中改变viewpager的当前条目
 F 添加一个方法和成员变量，在runnable中滚动逻辑进行判断，
 G 通过手势识别器，可以获得点击事件，在点击事件中，可以获得viewpager的当前角标，需要先定义回调接口，让外界事件接口，并传入接口对象，在合适的时机调用对象上的方法即可
 实现
 A
 定义一个接口IShowItem出来。有两个方法：获取图片路径、获取标题
 setItems(List<IShowItem> items) //
 B 
 看代码
 C
 1 可以对viewpager设置变化监听
 2 在监听的方法中改变textview的标题

 D 
 1  在setItems方法中往容器里添加一些圆点
 2 在opcl中改变圆点的enable状态

 E
 1 定义handler
 2 定义runnable
 3 在init方法时候触发
 F
 1 添加成员量和方法
 2 修改runnable的逻辑

 // Universal Image Loader的使用
 1 构造对象 ImageLoader.getInstance()
 2 配置ImageLoader (imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));)
 3 displayImage
G 
1 定义回调接口 OnItemClickListener
2 提供设置接口对象的方法 setOnItemClickListener
3 在合适的时机调用对象上的方法  onSingleTapUp
4 在外界实现逻辑

 对触摸事件机制的回顾：
 如果对一个view设置了onTouchListener
 1 如果onTouch方法返回了true，view自身的onTouchEvent方法不会被执行

 */

/**
 * 自定义轮播图
 */
public class AutoRollLayout extends FrameLayout {

    private Context contexts;

    public interface OnItemClickListener {
        void onItemClick(int index);
    }

    public static interface IShowItem {
        // 将会被显示到viewpager上面
        String getImageUrl();//图片链接

        // 将会被显示到textview上面
        String getTitle();//轮播图的文字

        String getId();//轮播图的类型:1.单一trip;2.行程集合

        String getHrefUrl();//集合行程需要的参数
    }

    protected static final long ROLL_DELAY = Constants.AUTO_DELAY;

    List<? extends IShowItem> items;
    private ViewPager viewpager;
    private TextView titleTv;
    private LinearLayout dotContainer;
    private static Handler handler = new Handler();
    boolean allowAutoRoll;
    OnItemClickListener oicl;

    public AutoRollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AutoRollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public AutoRollLayout(Context context) {
        this(context, null);
    }

    public void setOnItemClickListener(OnItemClickListener oicl) {
        this.oicl = oicl;
    }

    private void init(Context context) {
        contexts = context;
        View view = View.inflate(context, R.layout.layout_auto_roll, this);
        viewpager = (ViewPager) view.findViewById(R.id.arl_view_pager);
        titleTv = (TextView) view.findViewById(R.id.arl_title);
        dotContainer = (LinearLayout) view.findViewById(R.id.arl_dot_container);

        viewpager.setAdapter(pagerAdapter);
        viewpager.setOnTouchListener(viewPageOtl);
        viewpager.setOnPageChangeListener(opcl);

        gestureDetector = new GestureDetector(context, ogl);
    }

    public void setItems(List<? extends IShowItem> items) {
        //每次刷新时取消自动轮播
        handler.removeCallbacks(rollRunnable);
        this.items = items;
        int dotWithd = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 5, getResources()
                        .getDisplayMetrics());
        // 添加前先移除所有的
        dotContainer.removeAllViews();
        // 添加点
        for (IShowItem iShowItem : items) {
            View dot = new View(getContext());
            dot.setBackgroundResource(R.drawable.dot);
            // 指定view的宽高
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    dotWithd, dotWithd);
            // 指定view的右边距
            lp.leftMargin = (int) (dotWithd * 0.75);
            lp.rightMargin = (int) (dotWithd * 0.75);
            dotContainer.addView(dot, lp);
        }
        dotContainer.invalidate();
        if (dotContainer.getChildCount() >= 1) {
            dotContainer.getChildAt(0).setVisibility(View.INVISIBLE);
            dotContainer.getChildAt(dotContainer.getChildCount() - 1).setVisibility(View.INVISIBLE);
        }
        pagerAdapter.notifyDataSetChanged();
        viewpager.setCurrentItem(1, false);//每次刷新数据展示第二个
        opcl.onPageSelected(1);
    }

    public void setAllowAutoRoll(boolean allowAutoRoll) {
        this.allowAutoRoll = allowAutoRoll;
        handler.postDelayed(rollRunnable, ROLL_DELAY);
    }

    Runnable rollRunnable = new Runnable() {
        @Override
        public void run() {
            // 避免重复调用，只保证有一个正在被执行，把所有没有来得及执行任务的都删除掉
            handler.removeCallbacks(this);

            // 如果发现不允许自动滚动了就直接返回
            if (!allowAutoRoll) {
                return;
            }
            // 获取viewpager的当前页面角标
            int currentIndex = viewpager.getCurrentItem();
            // 角标+1，如果已经是最后一个了，就是0
            int next = 0;
            if (currentIndex == pagerAdapter.getCount() - 1) {
                next = 0;
            } else {
                next = currentIndex + 1;
            }
            // 改变viewpager的当前条目
            viewpager.setCurrentItem(next);
//            Log.e("setCurrentItem", "" + items.get(next).getTitle());
            // 发布延时任务，再次改变当前页面
            handler.postDelayed(this, ROLL_DELAY);

        }
    };

    private PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return items == null ? 0 : items.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.auto_roll_iv, null);
//            PicassioUtil.loadImg(contexts, items.get(position).getImageUrl(), -1, null, iv);
//            if (!TextUtils.isEmpty(items.get(position).getImageUrl())) {
//                String icon = items.get(position).getImageUrl();
//                if (!icon.contains("http")) {
//                    icon = Constants.QINIU_GET_ICON_HEADER_BASE + icon;
//                }
//                PicassioUtil.loadImg(BaseApplication.getContext(), icon, R.drawable.ic_head_defult, null, iv);
//            } else {
            iv.setImageResource(R.drawable.ic_head_defult);
//            }
            container.addView(iv);
            return iv;
        }
    };

    //注意,此处处理了ViewPager和ListView冲突情况:当ViewPager左右滑动超过10像素后,ListVIew就不发生滚动
    private OnPageChangeListener opcl = new OnPageChangeListener() {
        private int currentPosition;

        @Override
        public void onPageSelected(int position) {
            titleTv.setText(items.get(position).getTitle());
            // 把当前圆点设置成红的,要把原来的设置成白色的
            for (int i = 0; i < pagerAdapter.getCount(); i++) {
                dotContainer.getChildAt(i).setEnabled(i != position);
            }
            currentPosition = position;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (Math.abs(positionOffsetPixels) > 10) {
                getParent().requestDisallowInterceptTouchEvent(true);
            } else {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (currentPosition == viewpager.getAdapter().getCount() - 1) {
                    viewpager.setCurrentItem(1, false);
                } else if (currentPosition == 0) {
                    viewpager.setCurrentItem(viewpager.getAdapter().getCount() - 2, false);
                }
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    // 不用去继承VIewPager，就可以重写它的onTouchEvent方法
    private OnTouchListener viewPageOtl = new OnTouchListener() {

        // v 就是被触摸的哪个view，也就是被设置OnTouchListener的view --》viewpager
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // 把触摸时间交给内奸分析
            gestureDetector.onTouchEvent(event);
            // 可以获得到viewpager的所有触摸事件
            // 根据不同的触摸事件类型，进行不同的操作
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // 用户开始触摸了 ，，应该停止自动滚动
                    handler.removeCallbacks(rollRunnable);
                    break;
                case MotionEvent.ACTION_MOVE://开始移动了
                    break;
                case MotionEvent.ACTION_UP: // 用户手指离开屏幕,, 应该恢复自动滚动（原来是停的，你摸完之后，）
                    handler.postDelayed(rollRunnable, ROLL_DELAY);
                    break;
            }
            // 原来触摸事件怎么走就怎么走
            //
            // onTouchEvent(event);
            // return true;
            // 上面的代码和下面的意思是一样的
            return false;
        }
    };

    private GestureDetector gestureDetector;

    private OnGestureListener ogl = new OnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
//			Toast.makeText(getContext(), "onSingleTapUp",0).show();
            // 检查当前viewpager显示的页面的角标
            // 想方设法通知外界
            if (oicl != null) {
                oicl.onItemClick(viewpager.getCurrentItem());
            }
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }
    };
}
