package uk.redcode.flarex.module;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.github.mikephil.charting.charts.LineChart;

import uk.redcode.flarex.fragment.FragmentDashboard;

public class Module {

    public FragmentDashboard parent;
    public ScrollView scrollView = null;

    public Module(FragmentDashboard parent) {
        this.parent = parent;
    }

    public Module(FragmentDashboard parent, ScrollView scrollView) {
        this.parent = parent;
        this.scrollView = scrollView;
    }

    public void onDraw(LayoutInflater inflater, ViewGroup container) {}

    @SuppressLint("ClickableViewAccessibility")
    public void syncChartWithScroll(LineChart chart) {
        chart.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                    return false;
                }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    scrollView.requestDisallowInterceptTouchEvent(false);
                    return false;
                }
            }
            return false;
        });
    }

    public void refresh() {}
}
