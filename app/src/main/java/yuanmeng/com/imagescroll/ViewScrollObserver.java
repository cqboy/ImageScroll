package yuanmeng.com.imagescroll;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.RelativeLayout.LayoutParams;

/**
 * @Description: TODO(-列表控件滑动监听-)
 * @author wsx - heikepianzi@qq.com
 * @date 2016/04/09.
 */

public class ViewScrollObserver implements OnScrollListener {

    private int lastFirstVisibleItem;
    private int lastTop;
    private int scrollPosition;
    private int lastHeight;

    private float startY, delta;
    private View mLayout;
    private OnListViewScrollListener listener;

    public interface OnListViewScrollListener {
        void onScrollUpDownChanged(float delta, int scrollPosition);
//        void onScrollIdle();
    }

    public ViewScrollObserver(ViewGroup viewGroup, View mlayout) {

        mLayout = mlayout;
        if ((viewGroup instanceof AbsListView)) {
            // 使用系统滑动回调
            ((AbsListView) viewGroup).setOnScrollListener(this);
        } else {
            // 使用手势
            viewGroup.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            startY = event.getY();
                            delta = 0;
                            break;
                        case MotionEvent.ACTION_MOVE:

                            delta = event.getY() - startY;
                            tranY(delta);
                            startY = event.getY();
                            break;
                        default:
                            break;
                    }
                    scrollPosition += -delta;
                    if (listener != null && delta != 0)
                        listener.onScrollUpDownChanged(delta, scrollPosition);
                    return false;
                }
            });
        }
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d("TAG", "onScroll: firstVisibleItem=" + firstVisibleItem + "   visibleItemCount=" + visibleItemCount + "   totalItemCount=" + totalItemCount);
        View firstChild = view.getChildAt(0);
        if (firstChild == null) {
            return;
        }
        int top = firstChild.getTop();
        int height = firstChild.getHeight();
        int skipped = 0;
        // 到顶部
        if (lastFirstVisibleItem == firstVisibleItem) {
            delta = lastTop - top;

            // 上滑
        } else if (firstVisibleItem > lastFirstVisibleItem) {
            skipped = firstVisibleItem - lastFirstVisibleItem - 1;
            delta = skipped * height + lastHeight + lastTop - top;
        } else {

            // 下滑
            skipped = lastFirstVisibleItem - firstVisibleItem - 1;
            delta = skipped * -height + lastTop - (height + top);
        }
        boolean exact = skipped == 0;
        scrollPosition += -delta;
        tranY(-delta);
        if (listener != null && delta != 0) {
            listener.onScrollUpDownChanged(-delta, scrollPosition);
        }
        // 数据更新
        lastFirstVisibleItem = firstVisibleItem;
        lastTop = top;
        lastHeight = firstChild.getHeight();
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (listener != null && scrollState == SCROLL_STATE_IDLE) {
//            listener.onScrollIdle();
        }
    }

    public void setOnScrollUpAndDownListener(OnListViewScrollListener listener) {
        this.listener = listener;
    }

    /**
     * @param delta --移动位置
     * @Description: TODO(控件滑动, 配合改变布局)
     */

    private void tranY(float delta) {
        if (mLayout == null)
            return;
        // --改变布局
        LayoutParams params = (LayoutParams) mLayout.getLayoutParams();
        float tran_y = -params.topMargin + delta;
//        System.out.println("tran_y=" + tran_y);
        if (tran_y <= 0) { // --
            params.topMargin = 0;
        } else if (tran_y > mLayout.getHeight()) { // --滑动距离超过mLayout高度
            params.topMargin = -mLayout.getHeight();
        } else { // --滑动改变布局
            params.topMargin = (int) -tran_y;
        }
        mLayout.setLayoutParams(params);
    }
}
