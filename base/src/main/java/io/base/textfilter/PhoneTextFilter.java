package io.base.textfilter;

import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by gaochao on 2015/11/14.
 */
public class PhoneTextFilter implements TextWatcher {
    private static final String TAG = PhoneTextFilter.class.getSimpleName();
    private char[] oldText, newText;
    private int newTextInsertLoc = -1;
    private TextView mTargetTv;

    public PhoneTextFilter(TextView target){
        mTargetTv = target;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        oldText = new char[count];
        TextUtils.getChars(s, start, start + count, oldText, 0);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        newText = new char[count];
        TextUtils.getChars(s, start, start + count, newText, 0);
//        DebugLogUtils.v(TAG, "newText=" + new String(newText) + ",start=" + start + ",count=" + count);
        newTextInsertLoc = start;
    }

    @Override
    public void afterTextChanged(Editable s) {
        String content = s.toString();
        if(newText.length < 1){//此种情况对应删除字符
            if(newTextInsertLoc == s.length() && oldText[0] == ' '){
                return;
            }
            setText(reformat(content));
            return;
        }
//        DebugLogUtils.v(TAG, "newTextInsertLoc=" + newTextInsertLoc + ",textLength=" + s.length());
        int length = s.length();
        if(newTextInsertLoc + newText.length == length){
//            DebugLogUtils.v(TAG, "tail insertion");
            if(length == 3 || length == 8){
                setText(s.toString() + ' ');
            }
            return;
        }

        String[] splits = content.split(" ");
        switch (newTextInsertLoc) {
            case 0:
            case 1:
            case 2:
                length = splits[0].length();
                if(length < 3){
                    ;//do nothing
                } else if (length == 3) {
                    setCursorToEnd();
                }else {
                    setText(reformat(content));
                }
                break;

            case 3:
            case 8:
                setText(reformat(content));
                break;

            case 4:
            case 5:
            case 6:
            case 7:
                length = splits[1].length();
                if(length < 4){
                    ;//do nothing
                }else if (length == 4) {
                    setCursorToEnd();
                }else{
                    setText(reformat(content));
                }
                break;

            case 9:
            case 10:
            case 11:
            case 12:
                length = splits[2].length();
                if(length < 4){
                    ;//do nothing
                }else if (length == 4) {
                    setCursorToEnd();
                }else{
                    setText(reformat(content));
                }
                break;
        }
    }

    private String reformat(String str){
        String newStr = str.replace(" ", "");
        int length = newStr.length();
        if(length > 3 && length < 7){
            return newStr.substring(0, 3) + ' ' + newStr.substring(3, length);
        }else if(length >= 7){
            return newStr.substring(0, 3) + ' ' + newStr.substring(3, 7) + ' ' + newStr.substring(7, length);
        }
        return newStr;
    }

    private void setText(String text){
        TextView textView = mTargetTv;
        textView.removeTextChangedListener(this);
        textView.setText(text);
        textView.addTextChangedListener(this);
        Selection.setSelection(textView.getEditableText(), textView.getText().length());
    }

    private void setCursorToEnd(){
        TextView textView = mTargetTv;
        Selection.setSelection(textView.getEditableText(), textView.getText().length());
    }
}
