package com.friendlyplaces.friendlyapp.utilities;

import com.friendlyplaces.friendlyapp.model.FriendlyPlace;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by Nil Ordoñez on 28/3/18.
 */

public class MarkerColorUtil {
    public static float getColor(FriendlyPlace item) {
        return (item.positiveVotes < item.negativeVotes) ? BitmapDescriptorFactory.HUE_ORANGE : BitmapDescriptorFactory.HUE_GREEN;
    }
}
