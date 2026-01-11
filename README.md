This app is a modified work of original ColdCloud by [Stevy](https://gitlab.com/stevyneutron/coldcloud-android) i have removed several parts from this app and makde some changes here and there to make it work with latest android SDK as a fan myself i just couldn't see this awesome app being in forgotten sack. There might be some more changes but overall i won't be focusing on this one bad boy at all.

![Get it on Google Play](https://pcdn.ximg.us/images/public/GetItOnGooglePlay.png)

Note: I have changed the package name and app name as there are some changes without them it won't be the fan favorite ColdCloud especially the Google Maps integration.
I had to remove it completely as i want to publish the app to appstore and i don't want to meddle with new privacy declarations.
There might be some more breaking changes in upcoming future but i can't gurantee anything apart from making the app compatible with latest SDK.
Data collection through Sentry DSN is also removed(blame me, i just don't like sentry)
There might be some old non functioning things in the app which might or might not crash the app for you.

The code is opensourced as per original author and i won't be doing anything of the sorts to include tracking or ads on this app.

### You are more than welcome to push changes and i will make sure to test them out and publish an update on google play store as soon as i can.


Original Readme is Below
---
[![pipeline status](https://gitlab.com/stevyneutron/coldcloud-android/badges/main/pipeline.svg)](https://gitlab.com/stevyneutron/coldcloud-android/-/commits/main)
[![Latest Release](https://gitlab.com/stevyneutron/coldcloud-android/-/badges/release.svg)](https://gitlab.com/stevyneutron/coldcloud-android/-/releases)

# ColdCloud
ColdCloud is a powerful and simple to use application, allowing you to manage your Cloudflare domains

## Token Permissions

Here the different token permissions that you can add to enable/disable some feature of the application:
`Zone.Analytics` Allow graphql, display chart

`Zone.DNS` Read|Edit DNS Record

`Zone.Zone Settings` Read|Edit "Under Attack", "Development Mode", Zone configuration, SSL/TLS configuration

`Zone.SSL and Certificates` Read Certificates  

`User.Memberships - ?` Notification not available yet with token  

`Zone.Zone - Zone.Firewall Services` Read Firewall Event & Rules  

## RoadMap

### Dashboard
- [x] [v0.1.0] Module Development Mode
- [x] [v0.1.2] Module Under Attack
- [x] [v0.1.0] Module Visitor
- [x] [v0.1.0] Module Bandwidth
- [x] [v0.1.2] Module Security
- [x] [v0.1.2] Module Performance
### Zone
- [x] [v0.1.4] Always Online
- [x] [v0.1.4] Mirage
- [x] [v0.1.4] Polish
- [x] [v0.1.4] WebP
- [x] [v0.1.4] Privacy Pass
- [x] [v0.1.4] HTTP2 & HTTP3
### DNS
- [x] [v0.1.0] List DNS Record
- [x] [v0.1.1] Add DNS Record
- [x] [v0.1.5] Edit DNS Record
- [x] [v0.1.1] Delete DNS Record
### SSL/TLS
- [x] [v0.1.2] Change encryption mode
- [x] [v0.1.0] (Beta) SSL/TLS Recommender (don't even know what is that)
- [x] [v0.1.0] Always Use HTTPS
- [x] [v0.1.0] Opportunistic Encryption
- [x] [v0.1.0] TLS 1.3
- [x] [v0.1.0] Automatic HTTPS Rewrites
- [x] [v0.1.0] Minimum TLS  Version
- [x] [v0.1.0] Authenticated Origin Pulls
- [x] [v0.1.0] (Beta) Certificate Transparency Monitoring
### Certificate
- [x] [v0.1.4] Origin Certificates
- [ ] Client Certificates
- [x] [v0.1.0] Edge Certificates
### Network
- [ ] (Premium) HTTP/2
- [ ] (Premium) HTTP/3 (with QUIC)
- [ ] (Premium) 0-RTT Connection Resumption
- [ ] (Premium) Pseudo IPv4
- [ ] (Premium) Maximum Upload Size
- [x] [v0.1.0] IPv6 Compatibility
- [x] [v0.1.0] WebSockets
- [x] [v0.1.0] Onion Routing
- [x] [v0.1.0] IP Geolocation
### Firewall
- [x] [v0.1.0] Firewall Events
- [ ] Fire wall Rules
- [ ] IP Access Rules
- [ ] User Agent Blocking
### Speed
- [x] [v0.1.2] Auto Minify
- [x] [v0.1.0] Brotli
- [x] [v0.1.0] Rocket Loader
- [ ] Mobile Redirect
- [x] [v0.1.2] Early Hints
### Caching
- [x] [v0.1.2] Purge Cache
- [x] [v0.1.2] Caching Level
- [x] [v0.1.2] Browser Cache TTL
- [x] [v0.1.0] (Beta) Always Online
- [x] [v0.1.0] Development Mode
### Scrape Shield
- [x] [v0.1.0] Email Address Obfuscation
- [x] [v0.1.0] Server-side Excludes
- [x] [v0.1.0] Hotlink Protection
### Community
- [x] [v0.1.5] Community
- [x] [v0.1.5] Read Topic
- [ ] Search
### App Settings
- [x] [v0.1.2] Chart Sync
- [x] [v0.1.3] Daily Stat
- [x] [v0.1.0] Remember Zone
- [x] [v0.1.3] Daily Stats (Beta)
- [x] [v0.1.4] Theme
- [ ] ImageManager Compression & Maximum Storage Space