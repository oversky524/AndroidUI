package io.oversky524.styledtext;

import android.app.Activity;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Rasterizer;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.MaskFilterSpan;
import android.text.style.RasterizerSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuggestionSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StyledTextActivity extends Activity {
    private ArrayList<Data> mData = new ArrayList<>();

    private void getTexts(){
        mData.add( new Data("BackgroundColorSpan", get(new BackgroundColorSpan(Color.parseColor("#ff0000")))));
        mData.add( new Data("ClickableSpan", get(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(StyledTextActivity.this, "ClickableSpan", Toast.LENGTH_LONG).show();
            }
        })));
        mData.add( new Data("ForegroundColorSpan", get(new ForegroundColorSpan(Color.parseColor("#ff0000")))));
        mData.add( new Data("MaskFilterSpan - BlurMaskFilter", get(new MaskFilterSpan(new BlurMaskFilter(2, BlurMaskFilter.Blur.INNER)))));
        mData.add( new Data("MaskFilterSpan - EmbossMaskFilter", get(new MaskFilterSpan(new EmbossMaskFilter(new float[]{1,1,1}, .5f, .5f, 2)))));
        mData.add( new Data("AbsoluteSizeSpan", get(new AbsoluteSizeSpan(6, true))));
        mData.add( new Data("TextAppearanceSpan", get(new TextAppearanceSpan(this, R.style.TextAppearanceDemo))));
        mData.add( new Data("RasterizerSpan", get(new RasterizerSpan(new Rasterizer()))));
        mData.add( new Data("StrikethroughSpan", get(new StrikethroughSpan())));
        mData.add( new Data("UnderlineSpan", get(new UnderlineSpan())));
        mData.add( new Data("RelativeSizeSpan", get(new RelativeSizeSpan(.5f))));
        mData.add( new Data("ScaleXSpan", get(new ScaleXSpan(1.5f))));
        mData.add( new Data("StyleSpan", get(new StyleSpan(Typeface.BOLD_ITALIC))));
        mData.add( new Data("SubscriptSpan", get(new SubscriptSpan())));
        mData.add( new Data("SuperscriptSpan", get(new SuperscriptSpan())));
        mData.add( new Data("TypefaceSpan", get(new TypefaceSpan("monospace"))));
        mData.add( new Data("ImageSpan", get(new ImageSpan(this, android.R.drawable.arrow_up_float))));
        mData.add( new Data("SuggestionSpan", get(new SuggestionSpan(
                this, new String[]{"ab", "cd"}, SuggestionSpan.FLAG_AUTO_CORRECTION))));

        for(int i=0; i<mData.size(); ++i){
            addView(mData.get(i));
        }
    }

    private static final String str = "abcdefghijklmnopqrstuvwxyz";
    private SpannableString get(Object span){
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(span, 5, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private void addView(Data data){
        View view = getLayoutInflater().inflate(R.layout.lv_item_styled_text, mParent, false);
        TextView title = (TextView)view.findViewById(R.id.title);
        title.setText(data.title);
        TextView content = (TextView)view.findViewById(R.id.content);
        content.setText(data.content);
        content.setMovementMethod(LinkMovementMethod.getInstance());
        mParent.addView(view);
    }

    private ViewGroup mParent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_styled_text);
        mParent = (ViewGroup)findViewById(android.R.id.list);

        getTexts();

        /*ListView listView = getListView();
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mData.size();
            }

            @Override
            public Object getItem(int position) {
                return mData.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder;
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.lv_item_styled_text, parent, false);
                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(viewHolder);
                }else{
                    viewHolder = (ViewHolder)convertView.getTag();
                }
                viewHolder.setup(mData.get(position));
                return convertView;
            }
        });*/
    }

    /*private static class ViewHolder{
        private TextView title;
        private TextView content;

        public ViewHolder(View view){
            title = (TextView)view.findViewById(R.id.title);
            content = (TextView)view.findViewById(R.id.content);
        }

        public void setup(Data data){
            title.setText(data.title);
            content.setText(data.content);
        }
    }*/

    private static class Data{
        String title;
        SpannableString content;

        Data(String title, SpannableString content){
            this.title = title;
            this.content = content;
        }
    }
}
