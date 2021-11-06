package com.eskimobile.jetvpn.common.widget

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.eskimobile.jetvpn.R
import kotlinx.android.synthetic.main.layout_auth_toolbar.view.*

class AuthToolbar(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private val itemView = View.inflate(context, R.layout.layout_auth_toolbar, this)

    init {
        initAttrs(context, attrs)
        initView()
    }

    var onBtnLeftClicked: (() -> Boolean)? = null
    var onBtnRightClicked: (() -> Unit)? = null

    var leftIcon: Drawable? = null
        set(value) {
            field = value
            field?.let {
                itemView.btnLeft.setImageDrawable(leftIcon)
                itemView.btnLeft.visibility = View.VISIBLE
            } ?: run {
                itemView.btnLeft.visibility = View.INVISIBLE
            }
        }

    var title: String? = null
        set(value) {
            field = value
            field?.let {
                itemView.tvTitle.text = it
                itemView.tvTitle.visibility = View.VISIBLE
            } ?: run {
                itemView.tvTitle.visibility = View.INVISIBLE
            }
        }

    var rightText: String? = null
        set(value) {
            field = value
            field?.let {
                itemView.btnRight.text = it
                itemView.btnRight.visibility = View.VISIBLE
            } ?: run {
                itemView.btnRight.visibility = View.INVISIBLE
            }
        }

    private fun initAttrs(context: Context?, attrs: AttributeSet?) {
        context?.obtainStyledAttributes(attrs, R.styleable.AuthToolbar)
            ?.apply {
                leftIcon = this.getDrawable(R.styleable.AuthToolbar_atb_left)
                title = this.getString(R.styleable.AuthToolbar_atb_title)
                rightText = this.getString(R.styleable.AuthToolbar_atb_right)
            }
            ?.recycle()
    }

    private fun initView() {
        btnLeft.setOnClickListener(this::handleBtnLeftClicked)
        btnRight.setOnClickListener(this::handleBtnRightClicked)
    }

    private fun handleBtnLeftClicked(view: View) {
        val isUse = onBtnLeftClicked?.invoke()
        if (isUse == true) {
            return
        }
        (context as? Activity)?.finish()
    }

    private fun handleBtnRightClicked(view: View) {
        onBtnRightClicked?.invoke()
    }

}