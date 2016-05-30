package io.oversky524.centeredchildviewpager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MyViewPagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_view_pager);

        ChildCenteredViewPager mvp = (ChildCenteredViewPager)findViewById(R.id.my_view_pager);
        mvp.setPageTransformer(new ChildCenteredViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                final float absPos = Math.abs(position);
                if(absPos > 1){
                    page.setScaleX(SMALL);
                    page.setScaleY(SMALL);
                    return;
                }
                final float prop = SMALL + (BIG - SMALL) * (1 - Math.abs(position));
                page.setScaleX(prop);
                page.setScaleY(prop);
            }
        });
        mvp.setAdapter(new ChildCenteredViewPager.CenteredPageAdapter() {
            @Override
            public int getCount() {
                return 8;
            }

            @Override
            public View getView(int position, ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.vp_item_imageview, parent, false);
                ImageView imageView = (ImageView)view.findViewById(R.id.list_item);
                imageView.setImageResource(R.mipmap.av);
                ((TextView)view.findViewById(R.id.number)).setText("" + position);
                return view;
            }

            @Override
            public void destroyItem(int position) {

            }

            @Override
            public float getWidthFactor(int position) {
                return 0.8f;
            }
        });
    }
    static final float BIG = 1.F, SMALL = .85F;
}
