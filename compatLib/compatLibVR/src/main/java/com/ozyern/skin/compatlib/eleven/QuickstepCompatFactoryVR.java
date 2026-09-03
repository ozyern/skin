package com.ozyern.skin.compatlib.eleven;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.ozyern.skin.compatlib.ActivityManagerCompat;
import com.ozyern.skin.compatlib.ActivityOptionsCompat;
import com.ozyern.skin.compatlib.ten.QuickstepCompatFactoryVQ;

@RequiresApi(30)
public class QuickstepCompatFactoryVR extends QuickstepCompatFactoryVQ {

    @NonNull
    @Override
    public ActivityManagerCompat getActivityManagerCompat() {
        return new ActivityManagerCompatVR();
    }

    @NonNull
    @Override
    public ActivityOptionsCompat getActivityOptionsCompat() {
        return new ActivityOptionsCompatVR();
    }
}
