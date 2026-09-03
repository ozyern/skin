package com.ozyern.skin.allapps.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
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
    override fun getDrawerTopInset(): Int = if (topBar == null) 0 else topBarHeight

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
