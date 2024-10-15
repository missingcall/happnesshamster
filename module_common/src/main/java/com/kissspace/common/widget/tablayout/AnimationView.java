package com.kissspace.common.widget.tablayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class AnimationView extends LinearLayout {
    public AnimationView(Context context) {
        super(context,null);
    }

    public AnimationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
