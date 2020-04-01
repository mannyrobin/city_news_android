package ru.mycity.parser;

public interface IParser
{
    Object parse(Object src); //, Object prevResult);
    boolean supportStream();

    boolean selfClose();
}


