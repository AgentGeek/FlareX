package uk.redcode.flarex.object;

import java.util.ArrayList;

import uk.redcode.flarex.MainActivity;
import uk.redcode.flarex.R;
import uk.redcode.flarex.params.AppParamBlogNotification;
import uk.redcode.flarex.params.AppParamDailyStats;
import uk.redcode.flarex.params.AppParamFeedback;
import uk.redcode.flarex.params.AppParamImageCompression;
import uk.redcode.flarex.params.AppParamLinks;
import uk.redcode.flarex.params.AppParamLocking;
import uk.redcode.flarex.params.AppParamLogs;
import uk.redcode.flarex.params.AppParamRememberAccount;
import uk.redcode.flarex.params.AppParamRememberZone;
import uk.redcode.flarex.params.AppParamSendCrash;
import uk.redcode.flarex.params.AppParamSyncChart;
import uk.redcode.flarex.params.AppParamTheme;
import uk.redcode.flarex.params.AppParamVersion;
import uk.redcode.flarex.params.ParamAddressObfuscation;
import uk.redcode.flarex.params.ParamAlwaysHTTPS;
import uk.redcode.flarex.params.ParamAlwaysOnline;
import uk.redcode.flarex.params.ParamAuthenticateOrigin;
import uk.redcode.flarex.params.ParamAutoMinify;
import uk.redcode.flarex.params.ParamBrotli;
import uk.redcode.flarex.params.ParamCacheTTL;
import uk.redcode.flarex.params.ParamCachingLevel;
import uk.redcode.flarex.params.ParamDevelopmentMode;
import uk.redcode.flarex.params.ParamEarlyHints;
import uk.redcode.flarex.params.ParamEdgeCertificates;
import uk.redcode.flarex.params.ParamEncryptionMode;
import uk.redcode.flarex.params.ParamHTTP2;
import uk.redcode.flarex.params.ParamHTTP3;
import uk.redcode.flarex.params.ParamHTTPSRewrites;
import uk.redcode.flarex.params.ParamHotlinkProtection;
import uk.redcode.flarex.params.ParamIPGeo;
import uk.redcode.flarex.params.ParamIPv6;
import uk.redcode.flarex.params.ParamMinimumTLS;
import uk.redcode.flarex.params.ParamMirage;
import uk.redcode.flarex.params.ParamOnionRouting;
import uk.redcode.flarex.params.ParamOpportunisticEncryption;
import uk.redcode.flarex.params.ParamOriginCertificates;
import uk.redcode.flarex.params.ParamPolish;
import uk.redcode.flarex.params.ParamPrivacyPass;
import uk.redcode.flarex.params.ParamPseudoIPv4;
import uk.redcode.flarex.params.ParamPurgeCache;
import uk.redcode.flarex.params.ParamRocketLoader;
import uk.redcode.flarex.params.ParamSSLRecommender;
import uk.redcode.flarex.params.ParamServersideExcludes;
import uk.redcode.flarex.params.ParamTLS13;
import uk.redcode.flarex.params.ParamWebP;
import uk.redcode.flarex.params.ParamWebSockets;
import uk.redcode.flarex.ui.LayoutManager;

public class Parameter {

    public static final int ZONE = 0;
    public static final int SSL_TLS = 1;
    public static final int CERTIFICATES = 2;
    public static final int NETWORK = 3;
    public static final int CACHING = 4;
    public static final int SPEED = 5;
    public static final int SCRAPE_SHIELD = 6;
    public static final int NOTIFICATIONS = 7;
    public static final int APP = 99;

    public static ArrayList<Integer> getCategories() {
        ArrayList<Integer> list = new ArrayList<>();

        if (LayoutManager.get(LayoutManager.ZONE_CONFIG)) {
            list.add(ZONE);
            list.add(SSL_TLS);
            list.add(NETWORK);
            list.add(CACHING);
            list.add(SPEED);
            list.add(SCRAPE_SHIELD);
        }
        if (LayoutManager.get(LayoutManager.CERTIFICATES)) list.add(CERTIFICATES);
        if (LayoutManager.get(LayoutManager.NOTIFICATIONS)) list.add(NOTIFICATIONS);

        list.add(APP);
        return list;
    }

    public static int getName(int category) {
        switch (category) {
            case ZONE: return R.string.zone;
            case SSL_TLS: return R.string.ssl_tls;
            case CERTIFICATES: return R.string.certificates;
            case NETWORK: return R.string.network;
            case CACHING: return R.string.caching;
            case SPEED:  return R.string.speed;
            case SCRAPE_SHIELD: return R.string.scrape_shield;
            case NOTIFICATIONS: return R.string.notifications;
            case APP: return R.string.coldcloud;
            default: return R.string.question;
        }
    }

    public static int getIcon(int category) {
        switch (category) {
            case ZONE: return R.drawable.ic_globe;
            case SSL_TLS: return R.drawable.ic_lock;
            case CERTIFICATES: return R.drawable.ic_certificate;
            case NETWORK: return R.drawable.ic_network;
            case CACHING: return R.drawable.ic_cache;
            case SPEED: return R.drawable.ic_speed;
            case SCRAPE_SHIELD: return R.drawable.ic_scrape_shield;
            case NOTIFICATIONS: return R.drawable.ic_bell;
            case APP: return R.drawable.ic_cog;
            default: return R.string.question;
        }
    }

    public static ArrayList<Param> getParams(int category, MainActivity main) {
        ArrayList<Param> list = new ArrayList<>();

        switch (category) {
            case ZONE:
                list.add(new ParamAlwaysOnline());
                list.add(new ParamMirage());
                list.add(new ParamPolish());
                list.add(new ParamWebP());
                list.add(new ParamPrivacyPass());
                list.add(new ParamHTTP2());
                list.add(new ParamHTTP3());
                return list;

            case SSL_TLS:
                list.add(new ParamEncryptionMode());
                list.add(new ParamAlwaysHTTPS());
                list.add(new ParamOpportunisticEncryption());
                list.add(new ParamTLS13());
                list.add(new ParamHTTPSRewrites());
                list.add(new ParamMinimumTLS());
                list.add(new ParamAuthenticateOrigin());
                list.add(new ParamSSLRecommender());
                return list;

            case CERTIFICATES:
                list.add(new ParamEdgeCertificates());
                list.add(new ParamOriginCertificates());
                return list;

            case NETWORK:
                list.add(new ParamIPv6());
                list.add(new ParamWebSockets());
                list.add(new ParamOnionRouting());
                list.add(new ParamPseudoIPv4());
                list.add(new ParamIPGeo());
                //list.add(new ParamMaximumUpload());
                return list;

            case CACHING:
                list.add(new ParamPurgeCache());
                list.add(new ParamCachingLevel());
                list.add(new ParamCacheTTL());
                list.add(new ParamAlwaysOnline());
                list.add(new ParamDevelopmentMode());
                return list;

            case SPEED:
                list.add(new ParamAutoMinify());
                list.add(new ParamBrotli());
                list.add(new ParamRocketLoader());
                //list.add(new ParamMobileRedirect());
                list.add(new ParamEarlyHints());
                return list;

            case SCRAPE_SHIELD:
                list.add(new ParamAddressObfuscation());
                list.add(new ParamServersideExcludes());
                list.add(new ParamHotlinkProtection());
                return list;

            case APP:
                list.add(new AppParamSyncChart());
                list.add(new AppParamRememberZone());
                list.add(new AppParamRememberAccount());
                list.add(new AppParamLocking().setActivity(main));
                list.add(new AppParamBlogNotification());
                list.add(new AppParamDailyStats());
                list.add(new AppParamTheme().setActivity(main));
                list.add(new AppParamLogs());
                list.add(new AppParamSendCrash());
                list.add(new AppParamImageCompression());
                //list.add(new AppParamCredit());
                list.add(new AppParamFeedback().setActivity(main));
                list.add(new AppParamLinks().setActivity(main));
                list.add(new AppParamVersion());
                return list;

            default:
                return list;
        }
    }
}
