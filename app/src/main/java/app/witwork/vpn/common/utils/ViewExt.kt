package com.eskimobile.jetvpn.common.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.eskimobile.jetvpn.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.tabs.TabLayout

private const val DURATION = 500L
private const val DELAY = 2000L

fun View.animSnackbar() {
    this.visibility = View.VISIBLE
    val animEnter = ObjectAnimator.ofFloat(this, "translationY", this.height / 4f, 0f)
        .apply {
            duration = DURATION
        }
    val animFadeEnter = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
        .apply {
            duration = DURATION
        }

    val animExit = ObjectAnimator.ofFloat(this@animSnackbar, "translationY", 0f, this@animSnackbar.height / 4f)
        .apply {
            duration = DURATION
            startDelay = DELAY
        }
    val animFadeExit = ObjectAnimator.ofFloat(this@animSnackbar, "alpha", 1f, 0f)
        .apply {
            duration = DURATION
            startDelay = DELAY
        }
    AnimatorSet()
        .apply {
            playTogether(animEnter, animFadeEnter, animExit, animFadeExit)
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    this@animSnackbar.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }
            })
        }
        .start()
}

fun LineChart.init(percenArray: Array<Int>) {
    val values = mutableListOf<Entry>()
    percenArray.forEachIndexed { index, value ->
        values.add(Entry(index * 1f, value * 1f))
    }

    // create a dataset and give it a type
    val set1 = LineDataSet(values, "DataSet 1")
    set1.lineWidth = 1.75f
    set1.circleRadius = 5f
    set1.circleHoleRadius = 2.5f
    set1.setDrawFilled(true)
    set1.fillColor = ContextCompat.getColor(context!!, R.color.colorBackgroundLineChart)
    set1.color = ContextCompat.getColor(context!!, R.color.colorLineChart)
    set1.setCircleColor(ContextCompat.getColor(context!!, R.color.colorLineChart))
    set1.setDrawValues(false)

    // create a data object with the data sets
    val data = LineData(set1)

    this.description?.isEnabled = false
    this.setTouchEnabled(false)
    this.isDragEnabled = true
    this.setScaleEnabled(true)
    this.setPinchZoom(false)

    // set custom chart offsets (automatic offset calculation is hereby disabled)
    this.setViewPortOffsets(10f, 0f, 10f, 0f)

    // add data
    this.data = data

    val legent = this.legend
    legent?.isEnabled = false

    this.axisLeft?.isEnabled = false
    this.axisLeft?.spaceTop = 40f
    this.axisLeft?.spaceBottom = 40f
    this.axisRight?.isEnabled = false

    this.xAxis?.isEnabled = false
}

fun TabLayout.setLetterSpacing(letterSpacing: Float) {
    (0 until tabCount)
        .forEach {
            (this.getTabAt(it)?.view?.getChildAt(1) as? TextView)?.letterSpacing = letterSpacing
        }
}

fun EditText.setFontFamilyHint(fontRes: Int, letterSpacing: Float = 0f) {
    val hintText = this.hint.toString()
    val font = ResourcesCompat.getFont(this.context, fontRes)
    val span = SpannableString(hintText)
        .apply {
        }
    this.hint = span
}