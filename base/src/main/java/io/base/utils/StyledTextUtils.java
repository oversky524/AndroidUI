package io.base.utils;

import android.content.Context;
import android.support.annotation.StyleRes;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;

/**
 * Created by gaochao on 2016/2/2.
 */
public class StyledTextUtils {
    private StyledTextUtils(){}

    /**
     * 对str中first和second子串，施加同一个TextAppearance
     * */
    public static SpannableString setTextAppearanceSpan(String str, String first, String second, @StyleRes int textStyle, Context context){
        SpannableString spannableString = new SpannableString(str);
        int start = str.indexOf(first), end = start + first.length();
        spannableString.setSpan(new TextAppearanceSpan(context, textStyle), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        start = str.indexOf(second, end);
        end = start + second.length();
        spannableString.setSpan(new TextAppearanceSpan(context, textStyle), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
