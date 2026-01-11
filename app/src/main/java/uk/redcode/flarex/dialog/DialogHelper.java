package uk.redcode.flarex.dialog;

import android.graphics.Point;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import uk.redcode.flarex.R;

public class DialogHelper extends DialogFragment {

    public static final String TAG = "helper";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_helper, container);

        ((TextView) root.findViewById(R.id.helper_text_01)).setText(getText(R.string.helper_text_01));
        ((TextView) root.findViewById(R.id.helper_text_01)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) root.findViewById(R.id.helper_text_02)).setText(getText(R.string.helper_text_02));
        ((TextView) root.findViewById(R.id.helper_text_03)).setText(getText(R.string.helper_text_03));
        ((TextView) root.findViewById(R.id.helper_text_04)).setText(getText(R.string.helper_text_04));
        ((TextView) root.findViewById(R.id.helper_text_05)).setText(getText(R.string.helper_text_05));

        root.findViewById(R.id.close).setOnClickListener(view -> DialogHelper.this.dismiss());

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

}
