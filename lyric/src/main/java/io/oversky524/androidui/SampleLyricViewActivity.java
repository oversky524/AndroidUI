package io.oversky524.androidui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import io.oversky524.lyric.LyricView;
import io.oversky524.lyric.R;


public class SampleLyricViewActivity extends Activity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_lyric_view);

        String[] lyrics = {"客官请进",
                "1",
                "（打鸡蛋声效~~~切菜声效~~油炸声效~~）",
                "2",
                "演唱：少司命",
                "作词：willen",
                "作曲：willen",
                "编曲：willen",
                "混编：Mr.鱼",
                "3",
                "客官里面请几位",
                "请上座好茶来奉陪",
                "一双筷从南吃到北",
                "中华五千年留下的韵味"};
        ((LyricView)findViewById(R.id.lyrics)).setLyrics(lyrics);

        findViewById(R.id.set_line).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LyricView)findViewById(R.id.lyrics)).setDisplayedLine(2);
            }
        });
    }

}
