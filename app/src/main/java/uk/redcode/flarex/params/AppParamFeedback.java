package uk.redcode.flarex.params;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.Param;
import uk.redcode.flarex.object.Zone;

public class AppParamFeedback extends Param {

    Activity activity = null;

    public AppParamFeedback setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        /* Rate Layout */
        View rateLayout = inflater.inflate(R.layout.param_icon, parent, false);
        super.onDraw(rateLayout, zone);
        rateLayout.setOnClickListener(view -> rateTheApp());

        ((TextView) rateLayout.findViewById(R.id.param_name)).setText(R.string.rate_app);
        ((ImageView) rateLayout.findViewById(R.id.param_icon)).setImageResource(R.drawable.ic_star);
        parent.addView(rateLayout);

        /* Feedback Layout */
        View feedbackLayout = inflater.inflate(R.layout.param_icon, parent, false);
        super.onDraw(feedbackLayout, zone);
        feedbackLayout.setOnClickListener(view -> feedback());

        ((TextView) feedbackLayout.findViewById(R.id.param_name)).setText(R.string.feedback);
        ((ImageView) feedbackLayout.findViewById(R.id.param_icon)).setImageResource(R.drawable.ic_feedback);
        parent.addView(feedbackLayout);
    }

    private void feedback() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:redknight@redcode.uk"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "FlareX Feedback");

        context.startActivity(Intent.createChooser(intent, "Send Feedback"));
    }

    private void rateTheApp() {
        goToAppPage();

        /*ReviewManager manager = ReviewManagerFactory.create(context);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                // @ReviewErrorCode int reviewErrorCode = ((TaskException) task.getException()).getErrorCode();
                Log.d("HERE", "rateTheApp: Error getting review info, go to play store page !");
                goToAppPage();
                return;
            }

            ReviewInfo reviewInfo = task.getResult();
            Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
            flow.addOnCompleteListener(taskResult -> {
                if (!taskResult.isSuccessful()) {
                    Log.d("HERE", "rateTheApp: show review error ! got to play store page");
                    goToAppPage();
                    return;
                }
                if (!taskResult.isComplete()) {
                    Log.d("HERE", "rateTheApp: review not completed, goto play store");
                    goToAppPage();
                    return;
                }

                Log.d("HERE", "rateTheApp: finish ? "+taskResult.getResult());
            });
        });*/

    }

    private void goToAppPage() {
        final String packageName = activity.getPackageName();

        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (android.content.ActivityNotFoundException e) {
            e.printStackTrace();
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

}
