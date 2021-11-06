package com.eskimobile.jetvpn.common.widget.bottomnav

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.common.utils.dp2Px

class BottomNavItem(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    init {
        gravity = Gravity.CENTER
        orientation = VERTICAL
        initAttrs(context, attrs)
    }

    private lateinit var imageView: ImageView
    private lateinit var textView: TextView

    private fun initAttrs(context: Context?, attrs: AttributeSet?) {
        context?.obtainStyledAttributes(attrs, R.styleable.BottomNavItem)
            ?.apply {
                val icon = this.getDrawable(R.styleable.BottomNavItem_nav_icon)
                    ?: ContextCompat.getDrawable(context, R.drawable.ic_nav_home)

                val title = this.getString(R.styleable.BottomNavItem_nav_title)
                    ?: context.getString(R.string.nav_tab_1)

                addIcon(icon)
                addSpace()
                addTitle(title)
            }
            ?.recycle()
    }

    private fun addIcon(icon: Drawable?) {
        imageView = ImageView(context)
            .apply {
                setImageDrawable(icon)
            }
        this.addView(imageView, 0)
    }

    private fun addTitle(title: String) {
        textView = TextView(context)
            .apply {
                text = title
                gravity = Gravity.CENTER
                textSize = 10f
                typeface = ResourcesCompat.getFont(context, R.font.campton_bold)
                isAllCaps = true
                letterSpacing = 0.2f
            }
        this.addView(textView, 2)
    }

    private fun addSpace() {
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            .apply {
                height = context.dp2Px(3f)
            }
        val space = View(context)
            .apply {
                layoutParams = params
            }
        addView(space, 1)
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        val colorRes = if (selected) R.color.colorNavSelected else R.color.colorNavUnSelected
        val color = ContextCompat.getColor(context, colorRes)
        imageView.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        textView.setTextColor(color)
    }
}