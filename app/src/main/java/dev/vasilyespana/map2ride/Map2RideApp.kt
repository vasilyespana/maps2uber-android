package dev.vasilyespana.map2ride

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration
import java.io.File

@HiltAndroidApp
class Map2RideApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // osmdroid (OSM tiles, no API key): keep the tile cache inside our
        // private cache dir (scoped-storage friendly) and identify politely.
        Configuration.getInstance().apply {
            userAgentValue = "Map2Ride/1.5.1 (dev.vasilyespana.map2ride)"
            osmdroidBasePath = File(cacheDir, "osmdroid").also { it.mkdirs() }
            osmdroidTileCache = File(cacheDir, "osmdroid/tiles").also { it.mkdirs() }
        }
    }
}
