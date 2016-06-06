package io.oversky524.lyric;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 歌词显示View
 *
 * 限定歌词必须可在一行中显示
 *
 * 当前是一个简单版本：当前歌词和非当前歌词的字体大小颜色一致，背景也一致
 */
public class LyricView extends View {
    private static final boolean DEBUG_DRAW = true;
    private static final String TAG = LyricView.class.getSimpleName();
    private TextPaint mDebugTp;
    private TextPaint mLyricTp, mUnfocusLyricTp;
    private float mTextSize, mUnfocusTextSize;//歌词的字体大小：当前歌词与非当前歌词
    private int mLineSpace;//行距
    private Paint.FontMetrics mTextFm, mUnfocusTextFm;
    private int mTextHeight, mUnfocusTextheight;//歌词行高度：当前歌词与非当前歌词
    private int mTextColor, mUnfocusTextColor;//歌词的颜色：当前歌词与非当前歌词
    private int mCurLyricLineIndex;//当前显示的歌词行
    private int mDisplayedLyricLines;//显示出来的歌词行数
    private String[] mLyricArray;//所有歌词行数组
    private int[] mLyricWidthArray;//使用当前歌词字体大小计算的每行歌词的宽度
    private int[] mUnfocusLyricWidthArray;//使用非当前歌词字体大小计算的每行歌词的宽度

    public void setLyrics(String[] lyrics){
        final int length = lyrics.length;
        mLyricArray = new String[length];
        System.arraycopy(lyrics, 0, mLyricArray, 0, length);
        TextPaint tp = mLyricTp, utp = mUnfocusLyricTp;
        int[] lyricWidths = new int[length];
        int[] unfocusLyricWidths = new int[length];
        for(int i=0; i<length; ++i){
            String lyric = lyrics[i];
            lyricWidths[i] = (int)(.5f + tp.measureText(lyric));
            unfocusLyricWidths[i] = (int)(.5f + utp.measureText(lyric));
        }
        mLyricWidthArray = lyricWidths;
        mUnfocusLyricWidthArray = unfocusLyricWidths;
    }

    public LyricView(Context context) {
        super(context);
        init(null, 0);
    }

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public LyricView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.LyricView, defStyle, 0);

        mTextColor = a.getColor(R.styleable.LyricView_text_color, Color.RED);
        mUnfocusTextColor = a.getColor(R.styleable.LyricView_unfocus_text_color, Color.BLACK);
        mTextSize = a.getDimensionPixelSize(R.styleable.LyricView_text_size, 1);
        mUnfocusTextSize = a.getDimensionPixelSize(R.styleable.LyricView_unfocus_text_size, 1);
        mDisplayedLyricLines = a.getInt(R.styleable.LyricView_displayed_line, 3);
        if(mDisplayedLyricLines % 2 == 0) throw new RuntimeException("The number of displayed lines should be odd");
        mLineSpace = a.getDimensionPixelSize(R.styleable.LyricView_line_space, 0);

        a.recycle();

        TextPaint textPaint = new TextPaint();
        mLyricTp = textPaint;
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        textPaint = new TextPaint();
        mUnfocusLyricTp = textPaint;
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        if(DEBUG_DRAW){
            mDebugTp = new TextPaint();
            mDebugTp.setColor(Color.RED);
        }

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void debugDraw(Canvas canvas, int yPos){
        if(DEBUG_DRAW){
            canvas.drawLine(0, yPos, getWidth(), yPos, mDebugTp);
        }
    }

    private void invalidateTextPaintAndMeasurements() {
        TextPaint textPaint = mLyricTp;
        textPaint.setColor(mTextColor);
        textPaint.setTextSize(mTextSize);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        mTextFm = fontMetrics;
        mTextHeight = (int)(fontMetrics.bottom - fontMetrics.top + .5f);

        textPaint = mUnfocusLyricTp;
        textPaint.setColor(mUnfocusTextColor);
        textPaint.setTextSize(mUnfocusTextSize);
        fontMetrics = textPaint.getFontMetrics();
        mUnfocusTextFm = fontMetrics;
        mUnfocusTextheight = (int)(fontMetrics.bottom - fontMetrics.top + .5f);
    }

    private void showLyricsInternal(Canvas canvas, int yStartPos){
        if(DEBUG_DRAW) Log.v(TAG, "yStartPos=" + yStartPos);
        if(mLyricArray == null) return;
        final int unfocusLines = mDisplayedLyricLines - 1, half = unfocusLines / 2;
        final int height = getHeight(), curIndex = mCurLyricLineIndex;
        int start = curIndex - half, end = Math.min(mLyricArray.length - 1, curIndex + half);
        debugDraw(canvas, height/2);
        final int width = getWidth();
        for(int i=start; i<=end; ++i){
            if(i < 0){
                yStartPos += mUnfocusTextheight + mLineSpace;
                continue;
            }
            if(i != curIndex){
                int x = (width - mUnfocusLyricWidthArray[i])/2;
                canvas.drawText(mLyricArray[i], x, yStartPos - mUnfocusTextFm.top, mUnfocusLyricTp);
                yStartPos += mUnfocusTextheight + mLineSpace;
            }else {
                int x = (width - mLyricWidthArray[i])/2;
                canvas.drawText(mLyricArray[i], x, yStartPos - mTextFm.top, mLyricTp);
                yStartPos += mTextHeight + mLineSpace;
            }
        }
    }

    private int getYStartPos(){
        final int unfocusLines = mDisplayedLyricLines - 1;
        return (getHeight() - mUnfocusTextheight * unfocusLines - mTextHeight - unfocusLines * mLineSpace)/2;
    }

    public void setDisplayedLine(int displayedLine){ setDisplayedLine(displayedLine, DEFAULT_DURATION); }

    public void setDisplayedLine(int displayedLine, int duration){
        mStartTime = System.currentTimeMillis();
        mCurLyricLineIndex = displayedLine;
        mDuration = duration;
        invalidate();
        postOnAnimation(mYTranslationRunnable);
    }

    private static final int DEFAULT_DURATION = 500;
    private long mStartTime = Long.MIN_VALUE;
    private int mCurYOffset = Integer.MIN_VALUE;
    private long mDuration = DEFAULT_DURATION;
    private Runnable mYTranslationRunnable = new Runnable() {
        @Override
        public void run() {
            long current = System.currentTimeMillis();
            if(mStartTime + mDuration >= current){
                invalidate();
                final int yStartPos = getYStartPos();
                mCurYOffset = yStartPos + (int) ((float)mTextHeight * (current - mStartTime) / mDuration + .5f);
                if(DEBUG_DRAW) Log.v(TAG, "yStartPos=" + yStartPos + ",mCurYOffset=" + mCurYOffset +
                        ",yEndPos=" + (mTextHeight + yStartPos));
                postOnAnimation(mYTranslationRunnable);
            }else{
                mCurYOffset = Integer.MIN_VALUE;
                mStartTime = Long.MIN_VALUE;
                invalidate();
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        if(mCurYOffset != Integer.MIN_VALUE){
            showLyricsInternal(canvas, mCurYOffset);
        }else{
            showLyricsInternal(canvas, getYStartPos());
        }
    }

    public static String[] loadLyrics(InputStream inputStream, boolean close){
        ArrayList<String> lyricList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            while ((line = reader.readLine()) != null){
                lyricList.add(line);
            }
            if(close) reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] lyricArray = new String[lyricList.size()];
        lyricList.toArray(lyricArray);
        return lyricArray;
    }

    public static String[] loadLyrics(String whole){
        if(TextUtils.isEmpty(whole)) return new String[0];

        return whole.split("\n");
    }
}
