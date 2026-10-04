package dev.vasilyespana.maps2uber;

import dev.vasilyespana.maps2uber.core.geo.Pickup;
import dev.vasilyespana.maps2uber.core.geo.UberLinks;
import org.junit.Test;

/**
 * Fail-first: port of the worker's link/probe math.
 * Expected strings were derived from the worker's documented format:
 *  https://m.uber.com/ul/?action=setPickup
 *  [&pickup[latitude]=..&pickup[longitude]=..&pickup[nickname]=..]
 *  &dropoff[latitude]=..&dropoff[longitude]=..&dropoff[nickname]=..&dropoff[formatted_address]=..
 * with encodeURIComponent semantics on the text fields.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\b\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007J\b\u0010\u0005\u001a\u00020\u0004H\u0007J\b\u0010\u0006\u001a\u00020\u0004H\u0007J\b\u0010\u0007\u001a\u00020\u0004H\u0007J\b\u0010\b\u001a\u00020\u0004H\u0007J\b\u0010\t\u001a\u00020\u0004H\u0007J\b\u0010\n\u001a\u00020\u0004H\u0007J\b\u0010\u000b\u001a\u00020\u0004H\u0007\u00a8\u0006\f"}, d2 = {"Ldev/vasilyespana/maps2uber/UberLinksTest;", "", "()V", "destPoint_bearingEast_100m", "", "destPoint_bearingNorth_100m", "destPoint_zeroDistance_returnsStart", "formatCoord_matchesJsNumberToString", "probePoints_tenPoints_bearingsEvery36deg", "uberLink_noPickup_byteExact", "uberLink_specialChars_encodedLikeEncodeURIComponent", "uberLink_withPickupPreset_byteExact", "app_debugUnitTest"})
public final class UberLinksTest {
    
    public UberLinksTest() {
        super();
    }
    
    @org.junit.Test()
    public final void uberLink_noPickup_byteExact() {
    }
    
    @org.junit.Test()
    public final void uberLink_withPickupPreset_byteExact() {
    }
    
    @org.junit.Test()
    public final void uberLink_specialChars_encodedLikeEncodeURIComponent() {
    }
    
    @org.junit.Test()
    public final void destPoint_bearingNorth_100m() {
    }
    
    @org.junit.Test()
    public final void destPoint_bearingEast_100m() {
    }
    
    @org.junit.Test()
    public final void destPoint_zeroDistance_returnsStart() {
    }
    
    @org.junit.Test()
    public final void probePoints_tenPoints_bearingsEvery36deg() {
    }
    
    @org.junit.Test()
    public final void formatCoord_matchesJsNumberToString() {
    }
}