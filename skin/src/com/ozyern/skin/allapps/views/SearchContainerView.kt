package com.ozyern.skin.allapps.views

import android.content.Context
import android.util.AttributeSet
import com.ozyern.skin.search.SkinSearchUiDelegate
import com.android.launcher3.allapps.LauncherAllAppsContainerView

class SearchContainerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LauncherAllAppsContainerView(context, attrs, defStyleAttr) {

    override fun createSearchUiDelegate() = SkinSearchUiDelegate(this)
}
