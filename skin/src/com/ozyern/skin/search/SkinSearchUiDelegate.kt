package com.ozyern.skin.search

import android.view.Gravity
import android.view.ViewGroup
import com.android.launcher3.R
import com.android.launcher3.allapps.ActivityAllAppsContainerView
import com.android.launcher3.allapps.search.AllAppsSearchUiDelegate
import com.android.launcher3.allapps.search.SearchAdapterProvider
import com.android.launcher3.views.ActivityContext

class SkinSearchUiDelegate(private val appsView: ActivityAllAppsContainerView<*>) : AllAppsSearchUiDelegate(appsView) {

    override fun createMainAdapterProvider(): SearchAdapterProvider<*> {
        return SkinSearchAdapterProvider(ActivityContext.lookupContext(appsView.context), appsView)
    }

    /**
     * Float the search bar so it sits at the bottom of the drawer, above the IME, rather than
     * scrolling with the apps list at the top. When this is true the container adds the search
     * bar to the drag layer instead of to itself, so [onInitializeSearchBar] owns its placement.
     */
    override fun isSearchBarFloating(): Boolean = true

    override fun onInitializeSearchBar() {
        val searchBar = appsView.searchView
        val res = searchBar.resources
        val lp = searchBar.layoutParams as? ViewGroup.MarginLayoutParams ?: return

        lp.width = ViewGroup.LayoutParams.MATCH_PARENT
        lp.height = res.getDimensionPixelSize(R.dimen.search_box_container_height)
        lp.leftMargin = res.getDimensionPixelSize(R.dimen.skin_drawer_search_horizontal_margin)
        lp.rightMargin = lp.leftMargin
        lp.bottomMargin = res.getDimensionPixelSize(R.dimen.skin_drawer_search_bottom_margin)
        (lp as? android.widget.FrameLayout.LayoutParams)?.gravity =
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

        searchBar.layoutParams = lp
        searchBar.elevation = res.getDimension(R.dimen.skin_drawer_search_elevation)
    }
}
