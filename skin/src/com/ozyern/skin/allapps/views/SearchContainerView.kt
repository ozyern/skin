package com.ozyern.skin.allapps.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.RelativeLayout
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

    override fun createSearchUiDelegate() = SkinSearchUiDelegate(this)

    override fun onFinishInflate() {
        super.onFinishInflate()
        addTopBar()
    }

    /**
     * Adds the pinned All/Categories bar above the floating header. The search bar is floated to
     * the bottom by [SkinSearchUiDelegate], which frees the top of the drawer for this.
     */
    private fun addTopBar() {
        if (topBar != null) return
        val bar = LayoutInflater.from(context)
            .inflate(R.layout.skin_drawer_top_bar, this, false) as SkinDrawerTopBar

        val lp = bar.layoutParams as LayoutParams
        lp.addRule(ALIGN_PARENT_TOP)
        addView(bar, lp)

        // The header normally sits below the search container; with search floated away it needs
        // to sit below this bar instead.
        findViewById<View>(R.id.all_apps_header)?.let { header ->
            (header.layoutParams as? LayoutParams)?.let { hlp ->
                hlp.addRule(BELOW, bar.id)
                header.layoutParams = hlp
            }
        }

        bar.onTabSelected = { categories -> setCategoryMode(categories) }
        bar.onOverflowClick = { anchor -> showOverflowMenu(anchor) }
        topBar = bar
    }

    private fun setCategoryMode(categories: Boolean) {
        (personalAppList as? SkinAlphabeticalAppsList<*>)?.categoryMode = categories
    }

    private fun showOverflowMenu(anchor: View) {
        PopupMenu(context, anchor).apply {
            menuInflater.inflate(R.menu.skin_drawer_overflow, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_drawer_settings -> {
                        context.startActivity(PreferenceActivity.createIntent(context, AppDrawer))
                        true
                    }
                    R.id.menu_hidden_apps -> {
                        context.startActivity(PreferenceActivity.createIntent(context, AppDrawerHiddenApps))
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }
}
