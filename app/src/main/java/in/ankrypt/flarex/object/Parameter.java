package in.ankrypt.flarex.object;

import java.util.ArrayList;

import in.ankrypt.flarex.MainActivity;
import in.ankrypt.flarex.R;
import in.ankrypt.flarex.params.AppParamBlogNotification;
import in.ankrypt.flarex.params.AppParamDailyStats;
import in.ankrypt.flarex.params.AppParamFeedback;
import in.ankrypt.flarex.params.AppParamImageCompression;
import in.ankrypt.flarex.params.AppParamLinks;
import in.ankrypt.flarex.params.AppParamLocking;
import in.ankrypt.flarex.params.AppParamLogs;
import in.ankrypt.flarex.params.AppParamRememberAccount;
import in.ankrypt.flarex.params.AppParamRememberZone;
import in.ankrypt.flarex.params.AppParamSendCrash;
import in.ankrypt.flarex.params.AppParamSyncChart;
import in.ankrypt.flarex.params.AppParamTheme;
import in.ankrypt.flarex.params.AppParamVersion;
import in.ankrypt.flarex.params.ParamAddressObfuscation;
import in.ankrypt.flarex.params.ParamAlwaysHTTPS;
import in.ankrypt.flarex.params.ParamAlwaysOnline;
import in.ankrypt.flarex.params.ParamAuthenticateOrigin;
import in.ankrypt.flarex.params.ParamAutoMinify;
import in.ankrypt.flarex.params.ParamBrotli;
import in.ankrypt.flarex.params.ParamCacheTTL;
import in.ankrypt.flarex.params.ParamCachingLevel;
import in.ankrypt.flarex.params.ParamDevelopmentMode;
import in.ankrypt.flarex.params.ParamEarlyHints;
import in.ankrypt.flarex.params.ParamEdgeCertificates;
import in.ankrypt.flarex.params.ParamEncryptionMode;
import in.ankrypt.flarex.params.ParamHTTP2;
import in.ankrypt.flarex.params.ParamHTTP3;
import in.ankrypt.flarex.params.ParamHTTPSRewrites;
import in.ankrypt.flarex.params.ParamHotlinkProtection;
import in.ankrypt.flarex.params.ParamIPGeo;
import in.ankrypt.flarex.params.ParamIPv6;
import in.ankrypt.flarex.params.ParamMinimumTLS;
import in.ankrypt.flarex.params.ParamMirage;
import in.ankrypt.flarex.params.ParamOnionRouting;
import in.ankrypt.flarex.params.ParamOpportunisticEncryption;
import in.ankrypt.flarex.params.ParamOriginCertificates;
import in.ankrypt.flarex.params.ParamPolish;
import in.ankrypt.flarex.params.ParamPrivacyPass;
import in.ankrypt.flarex.params.ParamPseudoIPv4;
import in.ankrypt.flarex.params.ParamPurgeCache;
import in.ankrypt.flarex.params.ParamRocketLoader;
import in.ankrypt.flarex.params.ParamSSLRecommender;
import in.ankrypt.flarex.params.ParamServersideExcludes;
import in.ankrypt.flarex.params.ParamTLS13;
import in.ankrypt.flarex.params.ParamWebP;
import in.ankrypt.flarex.params.ParamWebSockets;
import in.ankrypt.flarex.ui.LayoutManager;

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
            case APP: return R.string.flarex;
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
