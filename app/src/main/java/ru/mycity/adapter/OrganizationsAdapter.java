package ru.mycity.adapter;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.mycity.R;
import ru.mycity._Application;
import ru.mycity.data.Organization;
import ru.mycity.database.DbOrganizationsHelper;
import ru.utils.Density;
import ru.utils.FilterableArrayAdapter;

public class OrganizationsAdapter extends FilterableArrayAdapter<Organization> implements View.OnClickListener
{
    private final LayoutInflater inflater;
    private final String workingHoursTitle;
    private final String schedule;
    //private String [] days;
    private final IPhoneClick onPhoneClick;
    private String round_the_clock;

    private CharSequence prefixString;
    private long categoryId;
    //private Context context;
    //private final int width;

    public OrganizationsAdapter(final LayoutInflater inflater, final ArrayList<Organization> items,
                                long categoryId, IPhoneClick onPhoneClick)
    {
        super(inflater.getContext(), 0, items);
        Resources res = inflater.getContext().getResources();
        workingHoursTitle = res.getString(R.string.working_hours_title) + '\n';
        schedule = res.getString(R.string.schedule);
        this.inflater = inflater;
        this.onPhoneClick = onPhoneClick;
        this.categoryId = categoryId;
        if (null != items)
            mOriginalValues = items;
        //temp debug
        //if (!items.isEmpty())
        //    items.get(0).promoted = 1;
    }

    public ArrayList<Organization> getItems()
    {
        return mObjects;
    }

    //public boolean isFiltered()
    //{
    //    return mObjects != mOriginalValues;
    //}


    @Override
    public int getItemViewType(int position)
    {
        Organization item = getItem(position);
        /*
        int i = (0 == item.promoted) ? 0 : 2;
        if (item.phones_count > 0)
            i++;
        return i;
        */

        int i = (0 == item.phones_count) ? 0 : 1;

        if (0 == item.promoted)
        {
            return (item.highlight ? 2 : 0) + i;
        }

        return (item.highlight ? 4 : 6) + i;
    }

    @Override
    public int getViewTypeCount()
    {
        return 8;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        OrganizationViewHolder holder;
        final View result;

        final Organization organization = getItem(position);

        if (null == convertView)
        {
            result = inflater.inflate(R.layout.organizations_list_item, parent, false);

            if (organization.highlight)
            {
                result.setBackgroundResource(R.drawable.li_frame_highlight);
            }
            else
            {
                result.setBackgroundResource(R.drawable.li_frame);
            }

            TextView tvName = (TextView) result.findViewById(R.id.item_text);
            if (organization.promoted > 0)
            {
                ImageView pin = (ImageView) result.findViewById(R.id.pin);
                pin.setImageResource(R.drawable.sponsored);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvName.getLayoutParams();
                params.leftMargin = Density.getIntValue(pin.getContext(), 8);
                pin.setVisibility(View.VISIBLE);
            }

            ImageView phoneView = (ImageView) result.findViewById(R.id.phone);
            phoneView.setOnClickListener(this);
            TextView working_hours = (TextView) result.findViewById(R.id.working_hours);
            TextView address = (TextView) result.findViewById(R.id.address);
            ((RelativeLayout.LayoutParams) address.getLayoutParams()).addRule(RelativeLayout.BELOW, tvName.getId());
            holder = new OrganizationViewHolder(tvName, address, working_hours, phoneView);
            result.setTag(holder);
        }
        else
        {
            holder = (OrganizationViewHolder) convertView.getTag();
            result = convertView;
        }

        ImageView phoneView = holder.phoneView;
        phoneView.setTag(organization);

        if (organization.phones_count > 0)
        {
            if (View.GONE == phoneView.getVisibility())
            {
                phoneView.setVisibility(View.VISIBLE);
            }

            if (organization.isWorkingNow())
            {
                phoneView.setImageResource(R.drawable.call_list);
                if (!phoneView.isEnabled())
                {
                    phoneView.setEnabled(true);
                }
            }
            else
            {
                //phoneView.setImageResource(R.drawable.call_list_inactive);
                //if (phoneView.isEnabled())
                //{
                //    phoneView.setEnabled(false);
                //}
                if (View.GONE != phoneView.getVisibility())
                {
                    phoneView.setVisibility(View.GONE);
                }
            }
        }
        else
        {
            if (View.GONE != phoneView.getVisibility())
            {
                phoneView.setVisibility(View.GONE);
            }
        }

        String titleText = organization.title;

        String workingTime = organization.workingHoursTitle;
        if (null == workingTime)
        {
            workingTime = getWorkingHoursTitle(organization);
            if (null == workingTime)
            {
                workingTime = "";
            }
            organization.workingHoursTitle = workingTime;
        }

        final String address = organization.address;

        int paddingBottom;
        if (TextUtils.isEmpty(address) && TextUtils.isEmpty(workingTime))
            paddingBottom = Density.getIntValue(this.mContext, 8);

        else
            paddingBottom = 0;


        RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) holder.title.getLayoutParams());
        if (params.bottomMargin != paddingBottom)
            params.bottomMargin = paddingBottom;

        holder.title.setText(titleText);

        TextView workingHours = holder.workingHours;
        TextView addressTv = holder.address;
        if (null != address && 0 != address.length())
        {
            if ((null != workingTime && 0 != workingTime.length()))
                paddingBottom = 0;
            else
                paddingBottom = Density.getIntValue(this.mContext, 6);

            params = ((RelativeLayout.LayoutParams) holder.address.getLayoutParams());
            if (params.bottomMargin != paddingBottom)
                params.bottomMargin = paddingBottom;

            addressTv.setText(address);

            if (View.GONE == addressTv.getVisibility())
            {
                addressTv.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            addressTv.setVisibility(View.GONE);
        }

        if (null != workingTime && 0 != workingTime.length())
        {
            workingHours.setText(workingTime);
            if (View.GONE == workingHours.getVisibility())
            {
                workingHours.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            workingHours.setVisibility(View.GONE);
        }
        return result;
    }


    private String getWorkingHoursTitle(Organization organization)
    {
        String openTime = getOpenTime(organization);
        if (null != openTime && 0 != openTime.length())
        {
            return workingHoursTitle + openTime;
        }
        else
        {
            return null;
        }
    }

        /*
        private String buildOpenTime(Organization organization)
        {
           StringBuilder sb = new StringBuilder(20);

           String [] values = { organization.work_monday,
                                organization.work_tuesday,
                                organization.work_wednesday,
                                organization.work_thursday,
                                organization.work_friday,
                                organization.work_saturday,
                                organization.work_sunday
                              };
         String [] _days = this.days;
         if (null == _days)
         {
             _days = inflater.getContext().getResources().getStringArray(R.array.days);
             this.days = _days;
         }

         final int len = values.length;
         String prevVal = null;
         boolean skipBegin = false;
         for (int i = 0; i < len; i++)
         {
            String val = values[i];
            final int l;
            if (null != val && 0 != (l = val.length()))
            {
                if (null == prevVal)
                {
                    if (skipBegin)
                      sb.append('-');
                    sb.append(_days[i]);
                }
                else
                {
                    if (l == prevVal.length() && val.regionMatches(0, prevVal, 0, l))
                    {
                        skipBegin = true;
                    }
                    else
                    {
                      if (skipBegin)
                      {
                         sb.append('-');
                      }
                    }
                }
            }
            prevVal = val;
         }
         return sb.toString();
        }
        */

    private String getOpenTime(Organization organization)
    {
        String openTime = organization.open_time;
        if (null != openTime && 0 != openTime.length())
        {
            if (openTime.startsWith(schedule))
            {
                int l = schedule.length();
                int l2 = openTime.length();
                while (l < l2)
                {
                    if (openTime.charAt(l) != ' ')
                        break;
                    l++;
                }
                return openTime.substring(l);
            }
            return openTime;
        }

        if (organization.work_always)
        {
            if (null == round_the_clock)
                round_the_clock = inflater.getContext().getString(R.string.round_the_clock);
            return round_the_clock;
        }
        //return buildOpenTime(organization);
        return null;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.phone:
            {
                Organization organization = (Organization) v.getTag();
                if (null != organization && null != onPhoneClick)
                {
                    //if (PhoneUtils.isPhoneCallAvailable(organization))
                    onPhoneClick.onClick(organization);
                }
            }
            break;
        }
    }

    public void addOrganizations(ArrayList<Organization> organizations)
    {
        if (null != organizations && !organizations.isEmpty())
        {
            mObjects.addAll(organizations);
            notifyDataSetChanged();
        }
    }

    @Override
    protected void doFilter(String prefix, List<Organization> values, ArrayList<Organization> newValues)
    {
        _Application application =  (_Application) mContext.getApplicationContext();
        ArrayList<Organization> list =
                DbOrganizationsHelper.findOrganizations
                        (application.getDbHelper().getReadableDatabase(), prefix, null, categoryId, DbOrganizationsHelper.PAGE_SIZE, 0);
        if (null != list && !list.isEmpty())
            newValues.addAll(list);

        /*
        final Matcher m = createMatcher(prefix);
        for (T t : values)
        {
            final String value = t.toString();
            m.reset(value);
            if (m.find())
            {
                newValues.add(t);
            }
        }
        */

        this.prefixString = prefix;
        filterSet = true;
    }

    @Override
    protected void onClearFilter()
    {
        this.prefixString = null;
        filterSet = false;
    }

    public String getFilterString()
    {
        return (null != prefixString) ? prefixString.toString() : null;
    }


    public interface IPhoneClick
    {
        void onClick(Organization organization);
    }

    protected final static class OrganizationViewHolder
    {
        public final TextView title;
        public final TextView address;
        public final TextView workingHours;
        public final ImageView phoneView;

        public OrganizationViewHolder(TextView title, TextView address, TextView workingHours, ImageView phoneView)
        {
            this.title = title;
            this.workingHours = workingHours;
            this.address = address;
            this.phoneView = phoneView;
        }
    }

    public void setFilterString(CharSequence prefixString)
    {
        this.prefixString = prefixString;
    }

    @Override
    public ArrayList<Organization> resetFilter()
    {
        this.prefixString = null;
        return super.resetFilter();
    }
}

//http://stackoverflow.com/questions/22498344/is-there-a-better-way-to-restore-searchview-state