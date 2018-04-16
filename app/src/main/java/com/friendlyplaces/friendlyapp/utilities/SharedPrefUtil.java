package com.friendlyplaces.friendlyapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Nil Ordoñez on 23/3/18.
 */

public class SharedPrefUtil {

    private static final String DETAILED_PLACE_TUTORIAL_KEY = "detailedPlaceTutorial";

    public static boolean hasCompletedDetailedPlaceTutorial(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean("detailedPlaceTutorial",false);
    }

    public static void setDetailedPlaceTutorialCompleted(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DETAILED_PLACE_TUTORIAL_KEY,true);
        editor.apply();
    }

}
