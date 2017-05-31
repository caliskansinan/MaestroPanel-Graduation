package net.sinancaliskan.maestropanel.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by SinanCaliskan on 12.03.2017.
 */
                                     //inherited application class
public class SharedInformation extends Application{
    private static SharedPreferences sharedPreferences;
    private static Context context;                                                               //set default application context

    public void onCreate(){
        super.onCreate();
        SharedInformation.context=getApplicationContext();
    }

    public static SharedPreferences get()                                                         //return sharedpreferences for reading user settings
    {
        return context.getSharedPreferences(context.getPackageName() + "_preferences",0);
    }
    public static void saveData(String key,String value)                                          //save user settings in string format
    {
        if (sharedPreferences==null)
            sharedPreferences=get();

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor .putString(key, value);
        prefsEditor.commit();
    }
    public static void saveData(String key,boolean value)                                         //save user settings in boolean format
    {
        if (sharedPreferences==null)
            sharedPreferences=get();

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor .putBoolean(key, value);
        prefsEditor.commit();
    }

    public static String getData(String key)                                                      //return key value
    {
        if (sharedPreferences==null)
            sharedPreferences=get();
        return sharedPreferences.getString(key, "");
    }
    public static boolean getBoolean(String key)
    {
        if (sharedPreferences==null)
            sharedPreferences=get();
        return sharedPreferences.getBoolean(key, false);
    }
    public static void changeLanguage(String language){                                           //sample en , tr
        String languageToLoad  = language;                                                        //set language
        Locale locale = new Locale(languageToLoad);                                               //set locale
        Locale.setDefault(locale);                                                                //set default language
        Configuration config = new Configuration();                                               //create new configuration
        config.locale = locale;                                                                   //application
        context.getResources().updateConfiguration(config,                                        //update application configuration with our changes
                context.getResources().getDisplayMetrics());
    }
}
