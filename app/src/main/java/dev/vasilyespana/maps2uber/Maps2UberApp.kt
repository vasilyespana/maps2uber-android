package dev.vasilyespana.maps2uber

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration
import java.io.File

@HiltAndroidApp
class Maps2UberApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // osmdroid (OSM tiles, no API key): keep the tile cache inside our
        // private cache dir (scoped-storage friendly) and identify politely.
        Configuration.getInstance().apply {
            userAgentValue = "Maps2Uber/1.0 (dev.vasilyespana.maps2uber)"
            osmdroidBasePath = File(cacheDir, "osmdroid").also { it.mkdirs() }
            osmdroidTileCache = File(cacheDir, "osmdroid/tiles").also { it.mkdirs() }
        }
    }
}
