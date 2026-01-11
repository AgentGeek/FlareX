package uk.redcode.flarex.object;

import java.util.ArrayList;

public class CFIncident {

    public String date;
    public final ArrayList<Incident> incidents = new ArrayList<>();

    public static class Incident {

        public String title;
        public final ArrayList<Update> updates = new ArrayList<>();

        public String getUpdates() {
            StringBuilder str = new StringBuilder();
            for (Update update : updates) {
                str.append(
                    String.format("<strong>%s</strong> - %s<br>%s<br><br>", update.status, update.text, update.date)
                );
            }
            return str.toString();
        }
    }

    public static class Update {

        public String date;
        public String status;
        public String text;

    }
}
