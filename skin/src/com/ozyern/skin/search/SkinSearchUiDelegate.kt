package com.ozyern.skin.search

import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
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
     * Float the search bar to the bottom of the drawer, above the IME, instead of pinning it to
     * the top of the apps list. When this is true the container adds the search bar to the drag
     * layer, so [onInitializeSearchBar] owns its placement.
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
        (lp as? FrameLayout.LayoutParams)?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        searchBar.layoutParams = lp
        searchBar.elevation = res.getDimension(R.dimen.skin_drawer_search_elevation)

        // The bar sits in the drag layer, so it has no measured height when the list first
        // computes its padding. Re-apply once it has been laid out or the last row stays hidden
        // behind it.
        searchBar.addOnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->
            if (bottom - top != oldBottom - oldTop) {
                appsView.refreshDrawerPadding()
            }
        }
    }
}
