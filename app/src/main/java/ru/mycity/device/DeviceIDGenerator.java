package ru.mycity.device;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
import java.util.UUID;


public class DeviceIDGenerator
{

    //Required permission <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    /*
     DID - Device Identificator. the IMEI, MEID, or ESN of the phone. Phone only. IMEI in most cases.
     SNM- Serial number
     SID - Subscriber Identificator. Phone Only. unique subscriber ID, for example, the IMSI for a GSM phone.
     AID - This is a 64-bit quantity that is generated and stored when the  device first boots. It is RESET when the device is wiped. 
     RND - Random
    */

    private static final String  SN_PREFIX   = "SNM"; // Serial number;

    private static final String  DEV_PREFIX  = "DID"; // Device Identificator.
    // the IMEI, MEID, or ESN of the phone. Phone only

    private static final String  SUBS_PREFIX = "SID"; // Subscriber Identificator. Phone Only.
    // unique subscriber ID, for example, the IMSI for a GSM phone.

    private static final String  AID_PREFIX  = "AID"; // ANDROID_ID
    // This is a 64-bit quantity that is generated and stored when the
    // device first boots. It is RESET when the device is wiped.

    private static final String  RND_PREFIX  = "RND"; // Random UID



    private boolean checkDeviceId(String deviceId)
    {
        int len;
        if ((len = deviceId.length()) < 8)
            return false;
        len--; // Бывает заполнено мусором, нулями, кавычками
        char first = deviceId.charAt(0);
        for (int i = 0; i < len; i ++)
        {
            if (deviceId.charAt(i) != first)
                return true;
        }
        return false;
    }

    public String build(Context context)
    {
        String deviceId     =  null;
        String subscriberId =  null;
        String serial       = null;
        //String sim          =  null;

        try
        {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId     =  tm.getDeviceId();
            subscriberId =  tm.getSubscriberId();
            //sim        =  tm.getSimSerialNumber();
        }
        catch (Throwable e)
        {
            //TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(context), e, false);
        }

        if (null != deviceId && checkDeviceId(deviceId))
            return DEV_PREFIX + deviceId;

        //Serial Number, бывает мусор
        final String sn = getSerialNumber();
        if (null != sn && checkDeviceId(sn))
        {
            serial = SN_PREFIX +  sn;
            return serial;
        }

        // Subscriber Identificator
        if (null != subscriberId && 0 != subscriberId.length())
        {
            serial = SUBS_PREFIX + subscriberId;
            return serial;
        }

        //ANDROID
        String androidId = null;
        try
        {
            androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        }
        catch (Throwable e)
        {
            //TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(context), e, false);
        }

        if (null != androidId && androidId.length() > 0 && !"9774d56d682e549c".equals(androidId))
        {
            serial = AID_PREFIX +  androidId;
            return serial;
        }
        return null;
    }

    public String generateRandom()
    {
        return RND_PREFIX + UUID.randomUUID();
    }


    private  String getSerialNumber()
    {
        //   String hwID = android.os.SystemProperties.get("ro.serialno", "unknown");

        String serial = null;
        Class<?> c    = null;
        try
        {
            c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        }
        catch (Throwable ignored)
        {
        }

        if (null != serial)
            return serial;

        try
        {
            //Class<?> c = Class.forName( "android.os.SystemProperties" );
            Method[] methods = c.getMethods();
            Object[] params = new Object[] { new String( "ro.serialno" ) , new String("Unknown" ) };
            serial = (String)(methods[2].invoke( c, params ));
        }
        catch (Throwable ignored)
        {
        }
        return serial;
    }
}

