package ru.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;


/**
 * Ellipsize the text when the lines of text exceeds the value provided by {@link #makeExpandable} methods.
 * Appends {@link #MORE} or {@link #LESS} as needed.
 * TODO: add animation
 * Created by vedant on 3/10/15.
 */
public class ExpandableTextView extends TextView {
    private static final String TAG = "ExpandableTextView";
    private static final CharSequence ELLIPSIZE = "... ";
    private CharSequence MORE = "more";
    private CharSequence LESS = "less";
    Integer linkColor;

    CharSequence mFullText;
    int mMaxLines;

    public ExpandableTextView(Context context) {
        super(context);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void makeExpandable(int maxLines) {
        makeExpandable(getText(), maxLines);
    }

    private void makeExpandable(CharSequence fullText, int maxLines) {
        mFullText = fullText;
        mMaxLines = maxLines;
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            @SuppressWarnings("deprecation")
            public void onGlobalLayout()
            {
                ViewTreeObserver obs = getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                int count = getLineCount();
                if (count <= mMaxLines)
                {
                    setText(mFullText);
                }
                else
                {
                    //setMovementMethod(LinkMovementMethod.getInstance());

                    MovementMethod m = getMovementMethod();
                    if ((m == null) || !(m instanceof LinkMovementMethod))
                    {
                        if (getLinksClickable())
                        {
                            setMovementMethod(LinkMovementMethod.getInstance());
                        }
                    }
                    showLess();
                }
            }
        });
    }

    /**
     * truncate text and append a clickable {@link #MORE}
     */
    private void showLess() {
        int lineEndIndex = getLayout().getLineEnd(mMaxLines - 1);

        int end = lineEndIndex - (ELLIPSIZE.length() + MORE.length() + 1);
        CharSequence newText = mFullText.subSequence(0, end);
        SpannableStringBuilder builder = new SpannableStringBuilder(newText);
        //TextUtils.copySpansFrom((Spanned) newText, 0, end, null, builder, 0);

        builder.append(ELLIPSIZE);
        int len = builder.length();
        builder.append(MORE);
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget)
            {
                showMore();
            }

            @Override
            public void updateDrawState(TextPaint ds)
            {
                ds.setColor((null != linkColor) ? linkColor : ds.linkColor);
                ds.setUnderlineText(true);
            }
        }, len, builder.length(), 0);
        setText(builder, BufferType.SPANNABLE);
    }

    /**
     * show full text and append a clickable {@link #LESS}
     */
    private void showMore() {

        SpannableStringBuilder builder = new SpannableStringBuilder(mFullText);
        if (mFullText instanceof Spannable)
        {
            TextUtils.copySpansFrom((Spannable) mFullText, 0, mFullText.length(), null, builder, 0);
        }
        builder.append(' ');
        int len = builder.length();
        builder.append(LESS);
        builder.setSpan(new ClickableSpan()
        {
            @Override
            public void onClick(View widget)
            {
                showLess();
            }
            @Override
            public void updateDrawState(TextPaint ds)
            {
                ds.setColor((null != linkColor) ? linkColor : ds.linkColor);
                ds.setUnderlineText(true);
            }

        }, len, builder.length(), 0);
        setText(builder, BufferType.SPANNABLE);
    }

    public void setMore(CharSequence more)
    {
        this.MORE = more;
    }

    public void setLess(CharSequence less)
    {
        this.LESS = less;
    }

    public void setLinkColor(Integer linkColor)
    {
        this.linkColor = linkColor;
    }



}

