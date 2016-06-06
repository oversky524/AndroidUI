package io.oversky524.loopviewpager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;

import java.util.Arrays;

import io.base.ui.FragmentBase;
import io.base.utils.ImageLoadUtils;

/**
 * Created by gaochao on 2016/5/5.
 */
public class HomePageFragment extends FragmentBase {
    private ConvenientBanner mLoopViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        mLoopViewPager = (ConvenientBanner)view.findViewById(R.id.viewpager);
        initLoop();
        return view;
    }

    private void initLoop(){
        mLoopViewPager.setPageTransformer(new DefaultTransformer())
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused})
        .setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, Arrays.asList(images));
    }

    @Override
    public void onPause() {
        super.onPause();
        mLoopViewPager.stopTurning();
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoopViewPager.startTurning(2000);
    }

    private static class NetworkImageHolderView implements Holder<String> {
        private ImageView imageView;
        @Override
        public View createView(Context context) {
            //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context,int position, String data) {
            ImageLoadUtils.intoGlobal(data,imageView);
        }
    }

    static final private String[] images = {
            "http://sj.5068.com/uploads/allimg/140114/1-1401141Q929.jpg",
            "http://www.yjz9.com/uploadfile/2014/1018/20141018062027405.jpg",
            "http://www.pp3.cn/uploads/201503/2015031302.jpg",
            "http://pic.4j4j.cn/upload/pic/20130614/b72a22222f.jpg"
    };
}
