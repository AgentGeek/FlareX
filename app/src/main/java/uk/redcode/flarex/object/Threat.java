package uk.redcode.flarex.object;

import android.content.Context;

import uk.redcode.flarex.R;

public class Threat {

    private static final String TAG = "Threat";

    public String key;
    public int count = 0;

    public Threat() {}

    public Threat(int count, String key) {
        this.count = count;
        this.key = key;
    }

    public String getLabel(Context context) {
        switch (key) {
            case "bic.ban.unknown": return context.getString(R.string.bad_browser);
            case "hot.ban.unknown": return context.getString(R.string.blocked_hotlink);
            case "hot.ban.ip": return context.getString(R.string.ban_ip);
            case "macro.ban.ip": return context.getString(R.string.bad_ip);
            case "user.ban.ctry": return context.getString(R.string.country_block);
            case "user.ban.ip": return context.getString(R.string.ip_block_user);
            case "user.ban.ipr16": return context.getString(R.string.ip_range_16);
            case "user.ban.ipr24": return context.getString(R.string.ip_range_24);
            case "macro.chl.captchaErr": return context.getString(R.string.captcha_error);
            case "macro.chl.captchaFail": return context.getString(R.string.human_challenged);
            case "macro.chl.captchaNew": return context.getString(R.string.new_captcha_cf);
            case "macro.chl.jschlFail": return context.getString(R.string.browser_challenged);
            case "macro.chl.jschlNew": return context.getString(R.string.challenged_threat);
            case "macro.chl.jschlErr": return context.getString(R.string.bot_request);
            case "user.chl.captchaNew": return context.getString(R.string.new_captcha_user);
            default:
                Logger.warning(TAG, "Label ?? -> "+key);
                return context.getString(R.string.unknown);
        }
    }

    public Integer getColor(Context context) {
        switch (key) {
            case "bic.ban.unknown": return context.getColor(R.color.threat_bad_browser);
            case "hot.ban.unknown": return context.getColor(R.color.threat_blocked_hotlink);
            case "hot.ban.ip": return context.getColor(R.color.threat_ban_ip);
            case "macro.ban.ip": return context.getColor(R.color.threat_bad_ip);
            case "user.ban.ctry": return context.getColor(R.color.threat_country_block);
            case "user.ban.ip": return context.getColor(R.color.threat_ip_block_user);
            case "user.ban.ipr16": return context.getColor(R.color.threat_ip_range_16);
            case "user.ban.ipr24": return context.getColor(R.color.threat_ip_range_24);
            case "macro.chl.captchaErr": return context.getColor(R.color.threat_captcha_error);
            case "macro.chl.captchaFail": return context.getColor(R.color.threat_human_challenged);
            case "macro.chl.captchaNew": return context.getColor(R.color.threat_new_captcha_cf);
            case "macro.chl.jschlFail": return context.getColor(R.color.threat_browser_challenged);
            case "macro.chl.jschlNew": return context.getColor(R.color.threat_challenged_threat);
            case "macro.chl.jschlErr": return context.getColor(R.color.threat_bot_request);
            case "user.chl.captchaNew": return context.getColor(R.color.threat_new_captcha_user);
            default:
                Logger.warning(TAG, "Color ?? -> "+key);
                return context.getColor(R.color.threat_unknown);
        }
    }
}
