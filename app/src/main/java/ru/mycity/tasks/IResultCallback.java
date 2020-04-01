package ru.mycity.tasks;

public interface IResultCallback
{
    public static class Result
    {
    }
    void onFinished(int rc, Result result);
    void onFailed(int rc, Throwable error);
    void onFailed(int rc, String description);
}
