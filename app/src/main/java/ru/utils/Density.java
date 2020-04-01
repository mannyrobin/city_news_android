package ru.utils;

import android.content.Context;
import android.content.res.Resources;

public final class Density
 {
     private static float _density = -1;
     private static boolean hasDensity;
     public static final float getDensity(final Context context)
     {
         if (!hasDensity)
         {
             hasDensity = true;
             _density = context.getResources().getDisplayMetrics().density;
             
         }
         return _density;
     }   

     public static final float getDensity(final Resources resources)
     {
         if (!hasDensity)
         {
             hasDensity = true;
             _density = resources.getDisplayMetrics().density;
         }
         return _density;
     }        
     public static final int getIntValue(final Context context, int val)
     {
         if (!hasDensity)
         {
             hasDensity = true;
             _density = context.getResources().getDisplayMetrics().density;
         }
         return (int) (_density  * val + 0.5f);
     }
     
     public static final int getIntValue(final Resources r, int val)
     {
         if (!hasDensity)
         {
             hasDensity = true; 
             _density = r.getDisplayMetrics().density;
             
         }
         return (int) (_density  * val + 0.5f);
     }
     
     public static final int getIntValue(final Resources r, double val)
     {
         if (!hasDensity)
         {
             hasDensity = true;
             _density = r.getDisplayMetrics().density;
         }
         return (int) (_density  * val + 0.5f);
     }     
     
 }
