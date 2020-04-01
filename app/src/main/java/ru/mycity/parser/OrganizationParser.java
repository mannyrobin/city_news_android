package ru.mycity.parser;

import com.google.gson.stream.JsonReader;

import java.io.IOException;


public class OrganizationParser extends OrganizationsBaseParser
{
    @Override
    protected Object read(JsonReader reader) throws IOException
    {
        return readOrganization(reader, false);
    }
}
