package yuanmeng.com.imagescroll;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * @Description: TODO(--)
 * @author wsx - heikepianzi@qq.com
 * @date 2016/04/09.
 */

public class MainActivity extends AppCompatActivity {

    List<Integer> imgs = new ArrayList<>();

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0)
                imgs.add(R.mipmap.test_pic1);
            if (i % 3 == 1)
                imgs.add(R.mipmap.test_pic2);
            if (i % 3 == 2)
                imgs.add(R.mipmap.test_pic3);
        }
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ImageAdapter());
    }

    class ImageAdapter extends BaseAdapter {

        List<Integer> viewHeights = new ArrayList<>();
        // 滑动换算比率
        float scrollRatio = 5;
        int minHeight;
        int maxHeight;

        public ImageAdapter() {

            // 数据动态填充，此处高度写死了的（最好是 >=imageview的父控件高度 + metrics.heightPixels / scrollRatio，滑动会出现空白）
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            minHeight = (int) (200 * metrics.density);
            maxHeight = (int) (minHeight + metrics.heightPixels / scrollRatio);
            for (int i = 0; i < imgs.size(); i++) {
                viewHeights.add(maxHeight);
            }

            ViewScrollObserver scrollObserver = new ViewScrollObserver(listView, findViewById(R.id.aa));
            scrollObserver.setOnScrollUpAndDownListener(new ViewScrollObserver.OnListViewScrollListener() {
                float topDelta, bottomDelta;

                @Override
                public void onScrollUpDownChanged(float delta, int action) {

                    // 细化转换数据（不然0.2(float)也会算做1(int) ）
                    float scrollDelta = delta / scrollRatio;
                    if (delta > 0) {
                        topDelta += scrollDelta;
                        if (topDelta >= 1) {
                            scrollDelta = (int) Math.floor(topDelta);
                            topDelta -= scrollDelta;
                        } else {
                            return;
                        }
                    } else {
                        bottomDelta += scrollDelta;
                        if (bottomDelta <= -1) {
                            scrollDelta = (int) Math.floor(bottomDelta) + 1;
                            bottomDelta -= scrollDelta;
                        } else {
                            return;
                        }
                    }
                    // 循环获取listview中的子项，并动态改变高度
                    for (int i = 0; i < listView.getChildCount(); i++) {
                        View v = listView.getChildAt(i).findViewById(R.id.img);
                        ViewGroup.LayoutParams params = v.getLayoutParams();
                        params.height += scrollDelta;
                        if (params.height > maxHeight)
                            params.height = maxHeight;
                        if (params.height < minHeight)
                            params.height = minHeight;
                        viewHeights.set((Integer) v.getTag(), params.height);
                    }
                    // listview动态刷新
                    listView.invalidateViews();
                }
            });
        }

        @Override
        public int getCount() {
            return imgs.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Viewholder holder;
            if (convertView == null) {
                holder = new Viewholder();
                convertView = getLayoutInflater().inflate(R.layout.activity_main_item, parent, false);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                convertView.setTag(holder);
            } else {
                holder = (Viewholder) convertView.getTag();
            }
            // 设置img高度
            ViewGroup.LayoutParams params = holder.img.getLayoutParams();
            params.height = viewHeights.get(position);
            holder.img.requestLayout();
            holder.img.setTag(position);

            holder.img.setImageResource(imgs.get(position));
            return convertView;
        }

        class Viewholder {
            ImageView img;
        }
    }
}
