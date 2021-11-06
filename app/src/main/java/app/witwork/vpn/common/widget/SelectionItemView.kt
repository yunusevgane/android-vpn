package com.eskimobile.jetvpn.common.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.eskimobile.jetvpn.R
import kotlinx.android.synthetic.main.item_selection.view.*

class SelectionItemView(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    companion object {
        const val ACTION_CHECK_BOX = 0
        const val ACTION_PREMIUM = 1
        const val ACTION_ARROW = 2
    }

    private var itemView: View = View.inflate(context, R.layout.item_selection, this)

    init {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context?, attrs: AttributeSet?) {
        context?.obtainStyledAttributes(attrs, R.styleable.SelectionItemView)
            ?.apply {
                //Flag
                val icon = this.getDrawable(R.styleable.SelectionItemView_siv_icon)
                setFlag(icon)
                //Title
                val title = this.getString(R.styleable.SelectionItemView_siv_title)
                setTitle(title)

                //Descripton
                val description = this.getString(R.styleable.SelectionItemView_siv_description)
                setDescription(description)

                //Price
                val price = this.getString(R.styleable.SelectionItemView_siv_price)
                setPrice(price)

                //Action
                val ordinal = this.getInt(R.styleable.SelectionItemView_siv_action, 0)
                setEndAction(ordinal)
            }
            ?.recycle()
    }

    private fun setFlag(icon: Drawable?) {
        if (icon != null) {
            itemView.img_flag.setImageDrawable(icon)
            itemView.img_flag.visibility = View.VISIBLE
        } else {
            itemView.img_flag.visibility = View.GONE
        }
    }

    fun setFlag(resId: Int) {
        if (resId != -1) {
            itemView.img_flag.setImageResource(resId)
            itemView.img_flag.visibility = View.VISIBLE
        } else {
            itemView.img_flag.visibility = View.GONE
        }
    }

    fun setTitle(title: String?) {
        itemView.tv_title.text = title
    }

    fun setDescription(description: String?) {
        if (description != null) {
            itemView.tv_description.text = description
            itemView.tv_description.visibility = View.VISIBLE
        } else {
            itemView.tv_description.visibility = View.GONE
        }
    }

    fun setEndAction(ordinal: Int) {
        val iconRes = when (ordinal) {
            ACTION_PREMIUM -> R.drawable.ic_crowd
            ACTION_ARROW -> R.drawable.ic_arrowright
            else -> R.drawable.ic_check_state
        }

        itemView.img_check.setBackgroundResource(iconRes)
    }

    fun setPrice(price: String?) {
        if (price != null) {
            itemView.tv_price.text = price
            itemView.tv_price.visibility = View.VISIBLE
        } else {
            itemView.tv_price.visibility = View.GONE
        }
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        itemView.img_check.isChecked = selected
    }

    fun purchase() {
        this.isEnabled = false
        itemView.img_check.isEnabled = false
        itemView.tv_title.append(context?.getString(R.string.current))
        itemView.tv_title.alpha = 0.5f
        itemView.tv_price.alpha = 0.5f
    }

    fun initWith(title: String) {
        this.isEnabled = true
        itemView.img_check.isEnabled = true
        itemView.tv_title.text = title
        itemView.tv_title.alpha = 1f
        itemView.tv_price.alpha = 1f
    }
}