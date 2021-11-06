package com.eskimobile.jetvpn.common.widget;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import org.jetbrains.annotations.NotNull;

public class LetterSpacingSpan extends MetricAffectingSpan {
    private float letterSpacing;

    /**
     * @param letterSpacing
     */
    public LetterSpacingSpan(float letterSpacing) {
        this.letterSpacing = letterSpacing;
    }

    public float getLetterSpacing() {
        return letterSpacing;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        apply(ds);
    }

    @Override
    public void updateMeasureState(@NotNull TextPaint paint) {
        apply(paint);
    }

    private void apply(TextPaint paint) {
        paint.setLetterSpacing(letterSpacing);
    }

}
