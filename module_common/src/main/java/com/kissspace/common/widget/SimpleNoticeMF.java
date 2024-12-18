package com.kissspace.common.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.kissspace.module_common.R;


/**
 * 简单字幕
 */
public class SimpleNoticeMF extends MarqueeFactory<TextView, CharSequence> {
    private LayoutInflater inflater;

    public SimpleNoticeMF(Context context) {
        super(context);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public TextView generateMarqueeItemView(CharSequence data) {
        TextView view = (TextView) inflater.inflate(R.layout.marqueen_layout_notice_item, null);
        view.setText(data);
        return view;
    }
}