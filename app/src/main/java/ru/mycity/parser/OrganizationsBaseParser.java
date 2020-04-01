package ru.mycity.parser;

import android.support.annotation.NonNull;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.util.ArrayList;

import ru.mycity.data.Organization;
import ru.mycity.data.OrganizationPhone;


abstract class OrganizationsBaseParser extends JsonStreamParser
{
    private static final String LATITUDE        = "latitude";
    private static final String LONGITUDE       = "longitude";
    private static final String OPEN_TIME       = "open_time";
    private static final String WORK_MONDAY     = "work_monday";
    private static final String WORK_TUESDAY    = "work_tuesday";
    private static final String WORK_WEDNESDAY  = "work_wednesday";
    private static final String WORK_THURSDAY   = "work_thursday";
    private static final String WORK_FRIDAY     = "work_friday";
    private static final String WORK_SATURDAY   = "work_saturday";
    private static final String WORK_SUNDAY     = "work_sunday";
    private static final String WORK_ALWAYS     = "work_always";
    //private static final String CATEGORIES      = "categories";

    @NonNull
    protected static Organization readOrganization(JsonReader reader, boolean skipDeleted) throws IOException
    {
        reader.beginObject();
        Organization organization = new Organization();
        boolean skip = false;

        while (reader.hasNext())
        {
            final String name = reader.nextName();
            if ((JsonToken.NULL == reader.peek()) || skip)
            {
                reader.skipValue();
                continue;
            }
            final int len = name.length();

            if (equals(CommonNames.ORGANIZATION_ID, name, len))
            {
                organization.id = reader.nextLong();
                continue;
            }

            if (null == organization.title && equals(CommonNames.TITLE, name, len))
            {
                organization.title =  reader.nextString();
                continue;
            }

            if (null == organization.site && equals(CommonNames.SITE, name, len))
            {
                organization.site = reader.nextString();
                continue;
            }

            if (null == organization.address && equals(CommonNames.ADDRESS, name, len))
            {
                organization.address = reader.nextString();
                continue;
            }

            if (equals(LATITUDE, name, len))
            {
                String s = reader.nextString();
                if (null != s && 0 != s.length())
                {
                    final double d;
                    try
                    {
                        d = Double.parseDouble(s);
                    }
                    catch (NumberFormatException e)
                    {
                        organization.hasCoordinates = false;
                        continue;
                    }
                    organization.latitude = (int) (d * 1E6);
                    organization.hasCoordinates = true;
                }
                else
                {
                    organization.hasCoordinates = false;
                }
                continue;
            }

            if (equals(LONGITUDE, name, len))
            {
                String s = reader.nextString();
                if (null != s && 0 != s.length())
                {
                    final double d;
                    try
                    {
                        d = Double.parseDouble(s);
                    }
                    catch (NumberFormatException e)
                    {
                        organization.hasCoordinates = false;
                        continue;
                    }
                    organization.longitude = (int) (d * 1E6);
                    organization.hasCoordinates = true;
                }
                else
                {
                    organization.hasCoordinates = false;
                }
                continue;
            }

            if (null == organization.type && equals(CommonNames.TYPE, name, len))
            {
                String s = reader.nextString();
                if (null != s && 0 != s.length())
                    organization.type = s;
                continue;
            }

            if (null == organization.open_time && equals(OPEN_TIME, name, len))
            {
                String s = reader.nextString();
                if (null != s && 0 != s.length())
                    organization.open_time = s;
                continue;
            }

            if (null == organization.info && equals(CommonNames.INFO, name, len))
            {
                organization.info = reader.nextString();
                continue;
            }

            if (null == organization.work_monday && equals(WORK_MONDAY, name, len))
            {
                String s = reader.nextString();
                if (null != s && 0 != s.length())
                    organization.work_monday = s;
                continue;
            }

            if (null == organization.work_tuesday && equals(WORK_TUESDAY, name, len))
            {
                String s = reader.nextString();
                if (null != s && 0 != s.length())
                    organization.work_tuesday = s;
                continue;
            }

            if (null == organization.work_wednesday && equals(WORK_WEDNESDAY, name, len))
            {
                String s = reader.nextString();
                if (null != s && 0 != s.length())
                    organization.work_wednesday = s;
                continue;
            }

            if (null == organization.work_thursday && equals(WORK_THURSDAY, name, len))
            {
                String s = reader.nextString();
                if (null != s && 0 != s.length())
                    organization.work_thursday = s;
                continue;
            }

            if (null == organization.work_friday && equals(WORK_FRIDAY, name, len))
            {
                String s = reader.nextString();
                if (null != s && 0 != s.length())
                    organization.work_friday = s;
                continue;
            }

            if (null == organization.work_saturday && equals(WORK_SATURDAY, name, len))
            {
                String s = reader.nextString();
                if (null != s && 0 != s.length())
                    organization.work_saturday = s;
                continue;
            }

            if (null == organization.work_sunday && equals(WORK_SUNDAY, name, len))
            {
                String s = reader.nextString();
                if (null != s && 0 != s.length())
                    organization.work_sunday = s;
                continue;
            }

            if (equals(WORK_ALWAYS, name, len))
            {
                organization.work_always = 1 == reader.nextInt();
                continue;
            }

            if (equals(CommonNames.HIGHLIGHT, name, len))
            {
                organization.highlight = 1 == reader.nextInt();
                continue;
            }

            if (equals(CommonNames.PROMOTED, name, len))
            {
                organization.promoted = reader.nextInt();
                continue;
            }

            //if (equals(CommonNames.POS, name, len))
            //{
            //    organization.pos = reader.nextInt();
            //    continue;
            //}

            //if (equals(CATEGORIES, name, len))
            //{
            //    readCategories(reader, organizationCategories, organization);
            //    continue;
            //}

            if (equals(CommonNames.PHONES, name, len))
            {
                readPhones(reader, organization);
                continue;
            }

            if (equals(CommonNames.IS_DELETED, name, len))
            {
                boolean deleted = 1 == reader.nextInt();
                if (deleted && skipDeleted)
                {
                    organization.id = -1;
                    skip = true;
                }
                else
                {
                    organization.deleted = deleted;
                }
                continue;
            }

            if (equals(CommonNames.PUBLISHED, name, len))
            {
                boolean published = 1 == reader.nextInt();

                if (!published && skipDeleted)
                {
                    organization.id = -1;
                    skip = true;
                }
                else
                {
                    organization.published = published;
                }
                continue;
            }

            if (equals(CommonNames.UPDATED_AT, name, len))
            {
                organization.updatedAt = reader.nextLong();
                continue;
            }

            if (null == organization.image && equals(CommonNames.IMAGE, name, len))
            {
                organization.image = reader.nextString();
                continue;
            }

            if (null == organization.video && equals(CommonNames.VIDEO, name, len))
            {
                organization.video = reader.nextString();
                continue;
            }

            if (equals(CommonNames.IS_VIDEO_HIDDEN, name, len))
            {
                organization.is_video_hidden = 1 == reader.nextInt();
                continue;
            }

            reader.skipValue();
        }
        reader.endObject();
        return organization;
    }


    private static void readPhones(JsonReader reader, Organization organization) throws IOException
    {
        reader.beginArray();

        ArrayList<OrganizationPhone> phones = null;

        while (reader.hasNext())
        {
            if (JsonToken.NULL == reader.peek())
            {
                reader.skipValue();
                continue;
            }
            reader.beginObject();
            String phone = null;
            String description = null;
            while (reader.hasNext())
            {
                if (JsonToken.NULL == reader.peek())
                {
                    reader.skipValue();
                    continue;
                }

                final String name = reader.nextName();
                final int len = name.length();

                if (null == phone && equals(CommonNames.PHONE, name, len))
                {
                    String s = reader.nextString();
                    if (null != s && 0 != s.length())
                        phone = s;
                    continue;
                }
                if (null == description && equals(CommonNames.DESCRIPTION, name, len))
                {
                    description = reader.nextString();
                    continue;
                }

                reader.skipValue();
            }

            if (null != phone && 0 != phone.length())
            {
                OrganizationPhone op = new OrganizationPhone();
                //op.organizationId = organization.id;
                op.description = description;
                op.phone = phone;
                if (null == phones)
                    phones = new ArrayList<>();
                phones.add(op);
            }
            reader.endObject();
        }
        reader.endArray();
        final int size;
        if (null != phones && 0 != (size = phones.size()))
        {
            organization.phones_count = size;
            organization.phones = phones;
        }
    }
}
