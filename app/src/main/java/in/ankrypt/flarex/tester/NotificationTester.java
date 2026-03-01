package in.ankrypt.flarex.tester;

import android.content.Context;

import java.util.ArrayList;

import in.ankrypt.flarex.adapter.TokenTestAdapter;
import in.ankrypt.flarex.network.CFApi;
import in.ankrypt.flarex.object.Notification;
import in.ankrypt.flarex.ui.LayoutManager;

public class NotificationTester extends Tester {

    private static final String key = LayoutManager.NOTIFICATIONS;

    public NotificationTester(Context context) {
        super(context);
        this.name = "Notifications";
        this.permission = "";
    }

    @Override
    public void runTest(int position, TokenTestAdapter adapter, String zone, TestListener listener) {
        super.runTest(position, adapter, zone, listener);

        this.icon = WARNING;
        this.result = "Notifications not available with token";
        setLayout(key, false);
        setLoading(false);
        listener.onFinish(zone);


        /*String accountId = User.getAccountId(context);
        if (accountId.isEmpty()) {
            CFApi.getAccountId(context, (success, result) -> {
                if (!success) {
                    this.icon = WARNING;
                    this.result = "Can't read account id";
                    setLoading(false);
                    listener.onFinish(zone);
                    return;
                }

                getNotifications(zone, listener);
            });
            return;
        }

        getNotifications(zone, listener);*/
    }

    private void getNotifications(String zone, TestListener listener) {
        CFApi.getNotifications(context, new CFApi.NotificationListener() {
            @Override
            public void onResult(ArrayList<Notification> notifications) {
                NotificationTester.this.icon = SUCCESS;
                NotificationTester.this.result = "Can read notifications";
                setLoading(false);
                listener.onFinish(zone);
            }

            @Override
            public void onError(Exception e) {
                NotificationTester.this.icon = WARNING;
                NotificationTester.this.result = "Can't read notifications";
                setLoading(false);
                listener.onFinish(zone);
            }
        });
    }
}
