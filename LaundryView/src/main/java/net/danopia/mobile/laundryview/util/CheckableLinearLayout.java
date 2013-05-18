package net.danopia.mobile.laundryview.util;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by danopia on 5/18/13.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private boolean checked = false;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;

        if (checked) {
            TypedArray array = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.colorFocusedHighlight,});
            setBackgroundColor(array.getColor(0, Color.argb(127, 255, 255, 255)));
        } else
            setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }
}