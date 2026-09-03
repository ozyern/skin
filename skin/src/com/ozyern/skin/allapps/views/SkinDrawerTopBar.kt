package com.ozyern.skin.allapps.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.FrameLayout
import android.widget.TextView
import com.android.launcher3.R

/**
 * The pinned bar at the top of the app drawer: an "All / Categories" segmented pill plus an
 * overflow button. Purely a view -- it reports selection through [onTabSelected] and leaves the
 * data switching to the drawer.
 */
class SkinDrawerTopBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    /** Invoked with true when "Categories" is picked, false for "All". */
    var onTabSelected: ((categories: Boolean) -> Unit)? = null

    /** Invoked when the overflow button is tapped, with the button as the anchor. */
    var onOverflowClick: ((anchor: View) -> Unit)? = null

    private lateinit var allTab: TextView
    private lateinit var categoriesTab: TextView
    private lateinit var overflow: ImageView

    var categoriesSelected: Boolean = false
        private set

    override fun onFinishInflate() {
        super.onFinishInflate()
        allTab = findViewById(R.id.skin_tab_all)
        categoriesTab = findViewById(R.id.skin_tab_categories)
        overflow = findViewById(R.id.skin_drawer_overflow)

        allTab.setOnClickListener { select(categories = false, notify = true) }
        categoriesTab.setOnClickListener { select(categories = true, notify = true) }
        overflow.setOnClickListener { onOverflowClick?.invoke(it) }

        select(categories = false, notify = false)
    }

    /** Moves the selection without firing [onTabSelected]; used to restore state. */
    fun setSelectedTab(categories: Boolean) = select(categories, notify = false)

    private fun select(categories: Boolean, notify: Boolean) {
        val changed = categoriesSelected != categories || !allTab.isSelected && !categoriesTab.isSelected
        categoriesSelected = categories
        allTab.isSelected = !categories
        categoriesTab.isSelected = categories
        // Selected label carries more weight, matching the reference design.
        allTab.alpha = if (categories) INACTIVE_ALPHA else 1f
        categoriesTab.alpha = if (categories) 1f else INACTIVE_ALPHA
        if (notify && changed) onTabSelected?.invoke(categories)
    }

    private companion object {
        const val INACTIVE_ALPHA = 0.7f
    }
}
