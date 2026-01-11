package uk.redcode.flarex.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import uk.redcode.flarex.object.ChartStat;

public class ChartView extends View {

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setList(ArrayList<ChartStat> list) {
        invalidate();
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    /*
        Drawing part
     */

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
