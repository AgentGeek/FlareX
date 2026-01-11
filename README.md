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