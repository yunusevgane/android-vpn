package com.eskimobile.jetvpn.common.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.common.utils.dp2Px
import kotlinx.android.synthetic.main.layout_custom_snack_bar.view.*

class SnackBarView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    companion object {
        private const val DURATION = 500L
        private const val DELAY = 2500L

    }

    private val itemView = View.inflate(context, R.layout.layout_custom_snack_bar, this)

    //region #ObjectAnimator
    private val animEnter = ObjectAnimator.ofFloat(this, "translationY", this.height / 4f, 0f)
        .apply {
            duration = DURATION
        }
    private val animFadeEnter = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
        .apply {
            duration = DURATION
        }

    private val animExit = ObjectAnimator.ofFloat(this, "translationY", 0f, this.height / 4f)
        .apply {
            duration = DURATION
            startDelay = DELAY
        }
    private val animFadeExit = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
        .apply {
            duration = DURATION
            startDelay = DELAY
        }
    //endregion

    init {
        setBackgroundResource(R.drawable.bg_snack_bar)
        val padding = context?.dp2Px(16f) ?: 0
        setPadding(padding, padding, padding, padding)
        gravity = Gravity.CENTER_VERTICAL

        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context?, attrs: AttributeSet?) {
        context?.obtainStyledAttributes(attrs, R.styleable.SnackBarView)
            ?.apply {
                val icon = this.getDrawable(R.styleable.SnackBarView_sbv_icon)
                val message = this.getString(R.styleable.SnackBarView_sbv_message)

                setIcon(icon)
                setMessage(message)
            }
            ?.recycle()
    }

    fun setIcon(icon: Drawable?) {
        itemView.imgIcon.setImageDrawable(icon)
    }

    fun setMessage(message: String?) {
        itemView.tvMessage.text = message
    }

    fun toggle() {
        this.visibility = View.VISIBLE
        AnimatorSet()
            .apply {
                playTogether(animEnter, animFadeEnter, animExit, animFadeExit)
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        this@SnackBarView.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                    }

                    override fun onAnimationStart(p0: Animator?) {
                    }
                })
            }
            .start()
    }

    fun show(message: String? = null) {
        message?.let {
            itemView.tvMessage.text = message
        }
        this.visibility = View.VISIBLE
        AnimatorSet()
            .apply {
                playTogether(animFadeEnter)
            }
            .start()
    }

    fun hide() {
        this.visibility = View.INVISIBLE
       /* AnimatorSet()
            .apply {
                playTogether(animEnter, animFadeExit)
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        this@SnackBarView.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                    }

                    override fun onAnimationStart(p0: Animator?) {
                    }
                })
            }
            .start()*/
    }

}