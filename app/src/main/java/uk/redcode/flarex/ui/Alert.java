package uk.redcode.flarex.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import java.util.Timer;
import java.util.TimerTask;

import uk.redcode.flarex.MainActivity;
import uk.redcode.flarex.R;

public class Alert {

    public static final int INFO = 0;
    public static final int SUCCESS = 1;
    public static final int ERROR = 2;

    private final int type;
    private int time = 0;
    private final int maxTime;
    private String text = null;
    private int textId = -1;
    private Timer timer = null;

    private MainActivity activity = null;
    private MaterialCardView card = null;
    private LinearLayout container = null;
    private ObjectAnimator startAnimation = null;
    private ObjectAnimator endAnimation = null;

    public Alert(int type, String text) {
        this.type = type;
        this.text = text;
        this.maxTime = getTimer();
    }

    public Alert(int type, int resId) {
        this.type = type;
        this.textId = resId;
        this.maxTime = getTimer();
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    private int getTimer() {
        switch (type) {
            case SUCCESS: case INFO: return 2 * 1000; // 2s
            case ERROR: return 4 * 1000; // 4s
            default: return 3 * 1000; // 3s
        }
    }

    private int getIcon() {
        switch (type) {
            case INFO: return R.drawable.ic_info;
            case SUCCESS: return R.drawable.ic_status_ok;
            case ERROR: return R.drawable.ic_error;
            default: return 3 * 1000; // 3s
        }
    }

    /*
        Drawing part
     */

    public void show(LinearLayout container) {
        if (activity == null) return;
        MaterialCardView card = (MaterialCardView) activity.getLayoutInflater().inflate(R.layout.alert, container, false);

        ((ImageView) card.findViewById(R.id.alert_icon)).setImageResource(getIcon());
        ((TextView) card.findViewById(R.id.alert_text)).setText(textId != -1 ? activity.getString(textId) : text);
        ((ProgressBar) card.findViewById(R.id.alert_progress)).setMax(maxTime);

        card.findViewById(R.id.alert_close).setOnClickListener(view -> {
            if (timer != null) timer.cancel();
            endAnimation.start();
            //container.removeView(card);
        });

        this.card = card;
        this.container = container;

        registerAnimation();
        startAnimation.start();
    }

    private void registerAnimation() {
        float value = container.getWidth() + card.getWidth();
        endAnimation = ObjectAnimator.ofFloat(card, "translationX", value);
        endAnimation.setDuration(500);
        endAnimation.setInterpolator(new DecelerateInterpolator());
        endAnimation.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationEnd(Animator animator) { container.removeView(card); }
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });

        startAnimation = ObjectAnimator.ofFloat(card, "translationX", -value, 0);
        startAnimation.setDuration(500);
        startAnimation.setInterpolator(new DecelerateInterpolator());
        startAnimation.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationEnd(Animator animator) { setTimer(); }
            @Override public void onAnimationStart(Animator animator) { container.addView(card); }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    private void setTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time += 1;
                if (time >= maxTime) {
                    this.cancel();
                    activity.runOnUiThread(() -> endAnimation.start());
                } else {
                    ((ProgressBar) card.findViewById(R.id.alert_progress)).setProgress(time);
                }
            }
        }, 0, 1);
    }


}
