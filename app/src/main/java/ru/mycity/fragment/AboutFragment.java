package ru.mycity.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

import java.util.ArrayList;

import ru.mycity.IPermissionCodes;
import ru.mycity.R;
import ru.mycity._Application;
import ru.mycity.data.About;
import ru.mycity.database.DbAboutHelper;
import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.utils.Density;
import ru.utils.ImageHelper;
import ru.utils.PermissionsUtils;
import ru.utils.PhoneUtils;


public class AboutFragment extends BaseFragment implements View.OnClickListener
{
    public static final String NAME = "AboutFragment";

    private CharSequence title;
    private String phone;
    private DisplayImageOptions displayOptions;
    private ImageLoader imageLoader;

    public AboutFragment()
    {
    }

    public void setData(CharSequence title)
    {
        this.title = title;
    }

    protected CharSequence getTitle()
    {
        return title;
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.phone)
        {
            makePhoneCall(phone, true);
        }
    }

    private void makePhoneCall(String phone, boolean checkPermissions)
    {
        Activity activity = getActivity();
        if (null == activity)
        {
            return;
        }
        if (checkPermissions)
        {
            if (!PermissionsUtils.checkPermission(activity, android.Manifest.permission.CALL_PHONE))
            {
                requestPermissions(NAME, new String[]{android.Manifest.permission.CALL_PHONE}, IPermissionCodes.PERMISSION_CALL_PHONE_RC);
                return;
            }
        }
        if (true == PhoneUtils.makeCall(activity, phone))
        {
            TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(activity), ITrackerEvents.CATEGORY_PAGES, ITrackerEvents.ACTION_CALL, ITrackerEvents.LABEL_ACTION_CALL, ITrackerEvents.LABEL_TARGET_ABOUT, "phone=" + phone));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (null != permissions && null != grantResults)
        {
            switch (requestCode)
            {
                case IPermissionCodes.PERMISSION_CALL_PHONE_RC:
                {
                    if (PermissionsUtils.isAllPermissionsGranted(permissions, grantResults))
                    {
                        makePhoneCall(phone, false);
                    }
                }
                break;
            }
        }
    }

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.about_fragment, container, false);
        _Application application = (_Application) inflater.getContext().getApplicationContext();

        About about = DbAboutHelper.read(application.getDbHelper().getReadableDatabase());
        //JSONObject data = DataUtils.readJSONObject(rootView.getContext(), "about_1.dat", "about.dat");
        if (null != about)
        {
            View body = rootView.findViewById(R.id.body);
            ArrayList<About.Owner> owners = about.owners;
            if (null != owners && !owners.isEmpty())
            {
                ViewGroup vg = (ViewGroup) body;
                int index = 2;

                for (About.Owner owner : owners)
                {
                    addOwner(vg, inflater, index++, owner);
                }
            }

            /*
            JSONArray owners = data.optJSONArray("owners");
            final int len;
            if (null != owners && (0 != (len = owners.length())))
            {
                ViewGroup vg = (ViewGroup) body;
                int index = 2;
                for (int i = 0; i < len; i++)
                {
                    JSONObject owner = owners.optJSONObject(i);
                    if (null != owner)
                    {
                        addOwner(vg, inflater, index++, owner);
                    }
                }
            }
            */

            //String description = data.optString("about_project");
            String description = about.about_project;
            if (null != description && 0 != description.length())
            {
                TextView tv = (TextView) body.findViewById(android.R.id.text1);
                String appName = getString(R.string.app_name);
                CharSequence text = description;
                int index = description.indexOf(appName);
                int l = index + appName.length();
                int last = description.length();
                if (index >= 0)
                {
                    while (index >= 0)
                    {
                        if (Character.isWhitespace(description.charAt(index)))
                        {
                            index++;
                            break;
                        }
                        index--;
                    }
                    while (l < last)
                    {
                        if (Character.isWhitespace(description.charAt(l)))
                        {
                            l--;
                            break;
                        }
                        l++;
                    }
                }
                if (index >= 0 && l < last)
                {
                    SpannableString sp = new SpannableString(description);
                    sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), index, l, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    text = sp;
                }
                tv.setText(text);
            }
            //phone = data.optString("phone");
            phone = about.phone;
            if (null != phone && 0 != phone.length())
            {
                TextView tvPhone = (TextView) body.findViewById(R.id.phone);
                SpannableString sp = new SpannableString(phone);
                sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sp.setSpan(new UnderlineSpan(), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                tvPhone.setText(sp);
                tvPhone.setOnClickListener(this);
            }
        }
        return rootView;
    }

    private void addOwner(ViewGroup vg, LayoutInflater inflater, int index, About.Owner owner /*, JSONObject owner*/)
    {
        final Context context = vg.getContext();
        View v = inflater.inflate(R.layout.owner, null, false);

        /*
        String pic_link = owner.optString("pic_link");
        String name = owner.optString("name");
        String desc = owner.optString("desc");
        */
        String pic_link = owner.pic_link;
        String name = owner.name;
        String desc = owner.desc;

        if (null != name && 0 != name.length())
        {
            TextView tv = (TextView) v.findViewById(android.R.id.text1);
            tv.setText(name);
        }

        if (null != desc && 0 != desc.length())
        {
            TextView tv = (TextView) v.findViewById(android.R.id.text2);
            tv.setText(desc);
        }

        if (null != pic_link && 0 != pic_link.length())
        {
            ImageView iv = (ImageView) v.findViewById(android.R.id.icon);
            if (null == imageLoader)
            {
                initImageLoader(context);
            }
            //pic_link = "http://zblogged.com/wp-content/uploads/2015/11/17.jpg";
            //http://52.28.179.65/files/about/000000001.jpg
            //TODO Temp remove in future!
            if ('/' == pic_link.charAt(0))
                pic_link = "http://52.28.179.65" + pic_link;
            imageLoader.displayImage(pic_link, iv, displayOptions);
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = Density.getIntValue(context, 10);
        lp.leftMargin = Density.getIntValue(context, 12);
        lp.rightMargin = lp.leftMargin;
        vg.addView(v, index, lp);
    }

    private void initImageLoader(Context context)
    {
        _Application application = (_Application) context.getApplicationContext();
        imageLoader = application.getImageLoader();
        //final int width = Density.getIntValue(context, 50);
        displayOptions = application.generateDefaultImageOptionsBuilder()
                //.displayer(new RoundedBitmapDisplayer(10))
                //.bitmapConfig(Bitmap.Config.ARGB_8888)

                //.imageScaleType(ImageScaleType.EXACTLY)

                //.displayer(new CircleBitmapDisplayer(0xFF686868, 2))
                .displayer(new BitmapDisplayer()
                   {
                       @Override
                       public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom)
                       {
                           final Context ctx = imageAware.getWrappedView().getContext();
                           final int width = Density.getIntValue(ctx, 50);
                           Bitmap resized = Bitmap.createScaledBitmap(bitmap, width, width, true);

                           imageAware.setImageBitmap(
                                   //ImageHelper.getCircleBitmap(bitmap, width)
                                   ImageHelper.getCroppedBitmap(resized)
                           );
                           if (!resized.isRecycled())
                               resized.recycle();
                       }
                   })
                .build();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(view), ITrackerEvents.CATEGORY_SECTION, ITrackerEvents.ACTION_OPEN, ITrackerEvents.LABEL_ACTION_SECTION, ITrackerEvents.LABEL_TARGET_ABOUT, null, System.currentTimeMillis() - startTime));
    }
}
