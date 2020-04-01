package ru.mycity.utils;


import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlReplacer
{
    private final CharSequence mSource;

    private final Matcher mMatcher;
    private int mAppendPosition;
    private boolean hasUrls;

    private static final Pattern pattern = Pattern.compile("(\\[[^\\]]+\\]\\()?" +
            //Patterns.WEB_URL

            /*
            "((?:(http|https|Http|Https):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)" + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_" + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?" + "(?:" + Patterns.DOMAIN_NAME + ")" + "(?:\\:\\d{1,5})?)" // plus option port number
            + "(\\/(?:(?:[" + Patterns.GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params
            + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"+-++
            //+ "(?:\\b|$)"
            */
            "(https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])"
            + "\\)?");

    public UrlReplacer(CharSequence source)
    {
        mSource = source;
        mMatcher = pattern.matcher(source);
        mAppendPosition = 0;
    }

    public CharSequence doReplace()
    {
        SpannableStringBuilder buffer = new SpannableStringBuilder();
        while (mMatcher.find())
        {
            appendReplacement(buffer);
        }
        return appendTail(buffer);
    }

    private void appendReplacement(SpannableStringBuilder buffer)
    {
        buffer.append(mSource.subSequence(mAppendPosition, mMatcher.start()));

        String found = mMatcher.group(0);
        String url = found;
        String name = found;

        if (mMatcher.groupCount() > 0)
        {
            String s = mMatcher.group(1);

            if (null != s)
            {
                final int len = s.length();
                if (len > 3 && '[' == s.charAt(0) && '(' == s.charAt(len - 1))
                {
                    url = found.substring(len, found.length() - 1);
                    name = found.substring(1, len - 2);
                }
            }
        }

        final int len = buffer.length();
        buffer.append(name);
        buffer.setSpan(new URLSpan(url), len, buffer.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mAppendPosition = mMatcher.end();
        hasUrls = true;
    }

    public SpannableStringBuilder appendTail(SpannableStringBuilder buffer)
    {
        buffer.append(mSource.subSequence(mAppendPosition, mSource.length()));
        return buffer;
    }

    public boolean foundUrls()
    {
        return hasUrls;
    }


}
