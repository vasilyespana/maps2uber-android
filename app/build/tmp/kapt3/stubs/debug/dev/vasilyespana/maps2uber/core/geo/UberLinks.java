package dev.vasilyespana.maps2uber.core.geo;

import java.math.BigDecimal;

/**
 * Port of the maps2uber worker's link/probe math.
 *
 * Uber link format (byte-exact vs the worker):
 *  https://m.uber.com/ul/?action=setPickup
 *  [&pickup[latitude]=..&pickup[longitude]=..&pickup[nickname]=..]
 *  &dropoff[latitude]=..&dropoff[longitude]=..&dropoff[nickname]=..&dropoff[formatted_address]=..
 * Text fields use encodeURIComponent semantics.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J2\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\t2\u0006\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u0004J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000fJ\u000e\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u0004J&\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0017\u001a\u00020\u0004J\u001c\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u001a0\u00192\u0006\u0010\u001b\u001a\u00020\u00042\u0006\u0010\u001c\u001a\u00020\u0004J0\u0010\u001d\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u00042\u0006\u0010\u001c\u001a\u00020\u00042\u0006\u0010\u001e\u001a\u00020\u000f2\u0006\u0010\u001f\u001a\u00020\u000f2\b\u0010 \u001a\u0004\u0018\u00010!R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Ldev/vasilyespana/maps2uber/core/geo/UberLinks;", "", "()V", "EARTH_RADIUS_M", "", "PROBE_COUNT", "", "PROBE_DISTANCE_M", "destPoint", "Lkotlin/Pair;", "latDeg", "lngDeg", "bearingDeg", "distanceM", "encodeUriComponent", "", "s", "formatCoord", "d", "haversineM", "lat1", "lng1", "lat2", "lng2", "probePoints", "", "Ldev/vasilyespana/maps2uber/core/geo/ProbePoint;", "lat", "lng", "uberLink", "name", "address", "pickup", "Ldev/vasilyespana/maps2uber/core/geo/Pickup;", "app_debug"})
public final class UberLinks {
    public static final double PROBE_DISTANCE_M = 100.0;
    public static final double EARTH_RADIUS_M = 6371000.0;
    public static final int PROBE_COUNT = 10;
    @org.jetbrains.annotations.NotNull()
    public static final dev.vasilyespana.maps2uber.core.geo.UberLinks INSTANCE = null;
    
    private UberLinks() {
        super();
    }
    
    /**
     * JavaScript encodeURIComponent semantics (UTF-8).
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String encodeUriComponent(@org.jetbrains.annotations.NotNull()
    java.lang.String s) {
        return null;
    }
    
    /**
     * Like JavaScript Number.toString for typical coordinate magnitudes.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatCoord(double d) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String uberLink(double lat, double lng, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String address, @org.jetbrains.annotations.Nullable()
    dev.vasilyespana.maps2uber.core.geo.Pickup pickup) {
        return null;
    }
    
    /**
     * Haversine destination point. Returns (lat, lng) in degrees.
     * Same formula as the worker's destPoint().
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlin.Pair<java.lang.Double, java.lang.Double> destPoint(double latDeg, double lngDeg, double bearingDeg, double distanceM) {
        return null;
    }
    
    /**
     * 10 probe points at bearings 0, 36, …, 324 deg, 100 m out.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<dev.vasilyespana.maps2uber.core.geo.ProbePoint> probePoints(double lat, double lng) {
        return null;
    }
    
    public final double haversineM(double lat1, double lng1, double lat2, double lng2) {
        return 0.0;
    }
}