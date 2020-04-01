package ru.mycity.data;



import java.util.Calendar;
import java.util.List;

/*
    "organization_id":264,
    "title":"Такси \"Зеленоградское такси\"",
    "site":"",
    "address":"",
    "latitude":"",
    "longitude":"",
    "type":"org",
    "open_time":"Круглосуточно",
    "info":"Есть детское автокресло. Поездки по городу от 130 рублей.",
    "work_monday":"",
    "work_tuesday":"",
    "work_wednesday":"",
    "work_thursday":"",
    "work_friday":"",
    "work_saturday":"",
    "work_sunday":"",
    "work_always":1,
    "highlight":0,
    "promoted":0,
    "pos":264,
    "categories":[
        230
    ],
    "phones":[
        {
            "phone":"+7(499)731-22-22",
            "description":""
        },
        {
            "phone":"+7(499)731-33-32",
            "description":""
        },
        {
            "phone":"+7(495)642-62-62",
            "description":""
        },
        {
            "phone":"+7(905)535-69-99",
            "description":""
        },
        {
            "phone":"+7(916)995-69-99",
            "description":""
        },
        {
            "phone":"+7(499)731-11-11",
            "description":""
        },
        {
            "phone":"+7(499)731-44-44",
            "description":""
        },
        {
            "phone":"+7(499)731-66-66",
            "description":""
        },
        {
            "phone":"+7(499)731-99-99",
            "description":""
        }
    ]
 */
public class Organization
{
    public long id;
    public String title;
    public String site;
    public String address;
    public int latitude;
    public int longitude;
    public boolean hasCoordinates;
    public String type;
    public String open_time;
    public String info;

    public String work_monday;
    public String work_tuesday;
    public String work_wednesday;
    public String work_thursday;
    public String work_friday;
    public String work_saturday;
    public String work_sunday;
    public boolean work_always;
    public boolean deleted;
    public boolean published;

    public boolean highlight;
    public int promoted;
    public int  phones_count;
    public int pos;
    public long updatedAt;
    public String image;

    public String video;
    public boolean is_video_hidden;

    public List<OrganizationPhone> phones;

    //For display
    public String workingHoursTitle;

    private WorkingHoursData whData;

    @Override
    public String toString()
    {
        return title;
    }

    public boolean isWorkingNow()
    {
        if (work_always)
            return true;
        long time = System.currentTimeMillis();
        if (null == whData)
        {
            whData = new WorkingHoursData();
        }
        else
        {
            if ((time - whData.lastCheckedTime) <= WorkingHoursData.TIME_OUT)
            {
                return whData.lastState;
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        boolean state = false; //0 == id % 2; //TEMP DEBUG
        switch (day)
        {
            case Calendar.MONDAY:
                 state = whData.isWorkingHours(calendar, work_monday, 0);
                 break;
            case Calendar.TUESDAY:
                 state = whData.isWorkingHours(calendar, work_tuesday, 1);
                 break;
            case Calendar.WEDNESDAY:
                 state = whData.isWorkingHours(calendar, work_wednesday, 2);
                 break;
            case Calendar.THURSDAY:
                 state = whData.isWorkingHours(calendar, work_thursday, 3);
                 break;
            case Calendar.FRIDAY:
                 state = whData.isWorkingHours(calendar, work_friday, 4);
                 break;
            case Calendar.SATURDAY:
                 state = whData.isWorkingHours(calendar, work_saturday, 5);
                 break;
            case Calendar.SUNDAY:
                 state = whData.isWorkingHours(calendar, work_sunday, 6);
                 break;
        }
        whData.lastState =  state;
        whData.lastCheckedTime = time;
        return state;
    }
}

