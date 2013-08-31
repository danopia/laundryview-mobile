package net.danopia.mobile.laundryview.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Layout allowing for visual checking.
 *
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
            Context context = getContext();
            assert context != null;
            Resources.Theme theme = context.getTheme();
            assert theme != null;
            TypedArray array = theme.obtainStyledAttributes(new int[]{android.R.attr.colorFocusedHighlight,});
            assert array != null;
            setBackgroundColor(array.getColor(0, Color.argb(127, 255, 255, 255)));
        } else
            setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }
}