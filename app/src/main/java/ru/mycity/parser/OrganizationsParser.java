package ru.mycity.parser;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.ArrayList;

import ru.mycity.data.Organization;


public class OrganizationsParser extends OrganizationsBaseParser
{
    protected boolean skipDeleted;

    @Override
    //protected Object read(JsonReader reader, Object prevResult) throws IOException
    protected Object read(JsonReader reader) throws IOException
    {
        reader.beginArray();

        Result result = new Result();
        ArrayList<Organization> list = (_defaultObjectCount > 0)? new ArrayList<Organization>(_defaultObjectCount) :
                new ArrayList<Organization>();
        //ArrayList<OrganizationCategory> organizationCategories = new ArrayList<>(50);
        /*
        Result result = (null != prevResult) ? (Result) prevResult : new Result();

        final ArrayList<Organization> list;
        if (null != result.organizations)
            list = result.organizations;
        else
            list = new ArrayList<>(INetworkRequest.PAGE_SIZE);

        ArrayList<OrganizationCategory> organizationCategories = result.organizationCategories;
        if (null == organizationCategories)
            organizationCategories = new ArrayList<>();
        */
        while (reader.hasNext())
        {
            final Organization organization = readOrganization(reader, skipDeleted);
            if (organization.id  >= 0 )
                list.add(organization);
        }
        reader.endArray();

        result.organizations = list;
        //result.organizationCategories = organizationCategories;
        return result;
    }


    public void setSkipDeleted(boolean skipDeleted)
    {
        this.skipDeleted = skipDeleted;
    }

    /*
    private void readCategories(JsonReader reader, ArrayList<OrganizationCategory> organizationCategories, Organization organization) throws IOException
    {
        reader.beginArray();
        while (reader.hasNext())
        {
            if (JsonToken.NULL == reader.peek())
            {
                reader.skipValue();
                continue;
            }
            OrganizationCategory oc = new OrganizationCategory();
            oc.organizationId = organization.id;
            oc.categoryId = reader.nextLong();
            organizationCategories.add(oc);
        }
        reader.endArray();
    }
    */

    public final static class Result
    {
        public ArrayList<Organization> organizations;
        //public ArrayList<OrganizationCategory> organizationCategories;
    }
}
