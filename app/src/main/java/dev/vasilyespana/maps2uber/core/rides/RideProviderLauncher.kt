package dev.vasilyespana.maps2uber.core.rides

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.net.toUri

/**
 * Launches ride-provider apps and builds install fallbacks.
 *
 * Install checks use PackageManager; the manifest declares `<queries>` for
 * each provider package (Android 11+ package visibility).
 */
object RideProviderLauncher {

    fun isInstalled(context: Context, provider: RideProvider): Boolean {
        val pkg = provider.playStorePackage ?: return false
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    pkg,
                    PackageManager.PackageInfoFlags.of(0),
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(pkg, 0)
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Intent that opens the provider with the destination (and origin when
     * known), or null when the provider app isn't installed — the caller
     * should show the install fallback sheet instead.
     */
    fun rideIntent(
        context: Context,
        provider: RideProvider,
        origin: RideLatLng?,
        dest: RideLatLng,
        destName: String,
    ): Intent? {
        if (!isInstalled(context, provider)) return null
        val deepLink = provider.buildDeepLink(origin, dest, destName)
        if (deepLink != null) {
            return Intent(Intent.ACTION_VIEW, deepLink.toUri())
        }
        // No verified deep link for this provider: open the app itself.
        return provider.playStorePackage?.let {
            context.packageManager.getLaunchIntentForPackage(it)
        }
    }

    /** Play Store listing intent, or null when the package is unknown. */
    fun playStoreIntent(provider: RideProvider): Intent? {
        val pkg = provider.playStorePackage ?: return null
        return Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=$pkg".toUri(),
        )
    }
}
