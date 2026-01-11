package uk.redcode.flarex.dialog;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.chip.Chip;

import uk.redcode.flarex.R;

public class DialogChart extends DialogFragment {

    private Chip label;
    private TextView titleView;
    private PieChart pieChart;

    public String title = "No Title";
    private ChartInterface listener = null;

    public void setListener(ChartInterface listener) {
        this.listener = listener;
    }

    public interface ChartInterface {
        void onDrawChart(PieChart chart);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_chart, container);

        titleView = root.findViewById(R.id.title);
        pieChart = root.findViewById(R.id.dialog_chart);
        label = root.findViewById(R.id.dialog_label);
        root.findViewById(R.id.close).setOnClickListener(view -> DialogChart.this.dismiss());

        label.setVisibility(View.INVISIBLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return root;
    }

    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();

        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        // Set the width of the dialog proportional to 95% of the screen width
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleView.setText(title);
        if (listener != null) listener.onDrawChart(pieChart);
    }

    public void setLabel(@Nullable String text) {
        if (text == null) {
            label.setVisibility(View.INVISIBLE);
            return;
        }

        label.setVisibility(View.VISIBLE);
        label.setText(text);
    }

}
