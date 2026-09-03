package com.ozyern.skin.allapps.views

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.PopupMenu
import com.ozyern.skin.allapps.SkinAlphabeticalAppsList
import com.ozyern.skin.search.SkinSearchUiDelegate
import com.ozyern.skin.ui.preferences.PreferenceActivity
import com.ozyern.skin.ui.preferences.navigation.AppDrawer
import com.ozyern.skin.ui.preferences.navigation.AppDrawerHiddenApps
import com.android.launcher3.R
import com.android.launcher3.allapps.LauncherAllAppsContainerView

class SearchContainerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LauncherAllAppsContainerView(context, attrs, defStyleAttr) {

    private var topBar: SkinDrawerTopBar? = null

    private val topBarHeight: Int
        get() = resources.getDimensionPixelSize(R.dimen.skin_drawer_top_bar_height)

    override fun createSearchUiDelegate() = SkinSearchUiDelegate(this)

    /**
     * Reserve room for the pinned All/Categories bar. The base class feeds this into both the
     * header's top margin and the list padding, so the bar never overlaps the first row.
     */
    private var systemTopInset = 0
    private var systemBottomInset = 0

    private val searchBarGap: Int
        get() = resources.getDimensionPixelSize(R.dimen.skin_drawer_search_bottom_margin)

    /**
     * Computed from resources and insets rather than from the bar's measured height. The bar lives
     * in the drag layer and is frequently unmeasured when the lists compute their padding, which
     * left the last rows running underneath it.
     */
    override fun getFloatingSearchBarInset(): Int =
        resources.getDimensionPixelSize(R.dimen.search_box_container_height) +
            searchBarGap + systemBottomInset

    override fun getDrawerTopInset(): Int =
        if (topBar == null) 0 else topBarHeight + systemTopInset

    /**
     * The floating search bar is a child of the drag layer, not of this view, so nothing hides it
     * when All Apps closes -- it was still drawn over the workspace. Mirror this view's alpha and
     * visibility, both of which the state transition drives, onto the bar.
     */
    override fun setAlpha(alpha: Float) {
        super.setAlpha(alpha)
        searchView?.alpha = alpha
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        searchView?.visibility = visibility
    }

    override fun setInsets(insets: Rect) {
        super.setInsets(insets)

        // Float the bar clear of the gesture bar instead of sitting flush on the screen edge.
        searchView?.let { bar ->
            (bar.layoutParams as? ViewGroup.MarginLayoutParams)?.let { lp ->
                val wanted = insets.bottom + searchBarGap
                if (lp.bottomMargin != wanted) {
                    lp.bottomMargin = wanted
                    bar.layoutParams = lp
                }
            }
        }
        // Keep the pinned bar clear of the status bar; the base class only insets the header
        // and the lists, which sit below it.
        if (systemTopInset != insets.top || systemBottomInset != insets.bottom) {
            systemTopInset = insets.top
            systemBottomInset = insets.bottom
            topBar?.let { bar ->
                (bar.layoutParams as? LayoutParams)?.let { lp ->
                    lp.topMargin = insets.top
                    bar.layoutParams = lp
                }
            }
            refreshDrawerPadding()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        addTopBar()
    }

    /**
     * Adds the All/Categories bar above the header. The search bar is floated to the bottom by
     * [SkinSearchUiDelegate], which frees the top of the drawer for it.
     */
    private fun addTopBar() {
        if (topBar != null) return
        val bar = LayoutInflater.from(context)
            .inflate(R.layout.skin_drawer_top_bar, this, false) as SkinDrawerTopBar

        val lp = bar.layoutParams as LayoutParams
        lp.addRule(ALIGN_PARENT_TOP)
        addView(bar, lp)

        bar.onTabSelected = ::setCategoryMode
        bar.onOverflowClick = ::showOverflowMenu
        topBar = bar
    }

    private fun setCategoryMode(categories: Boolean) {
        (personalAppList as? SkinAlphabeticalAppsList<*>)?.categoryMode = categories
    }

    private fun showOverflowMenu(anchor: View) {
        PopupMenu(context, anchor).apply {
            menuInflater.inflate(R.menu.skin_drawer_overflow, menu)
            setOnMenuItemClickListener { item ->
                val route = when (item.itemId) {
                    R.id.menu_drawer_settings -> AppDrawer
                    R.id.menu_hidden_apps -> AppDrawerHiddenApps
                    else -> return@setOnMenuItemClickListener false
                }
                context.startActivity(PreferenceActivity.createIntent(context, route))
                true
            }
            show()
        }
    }
}
