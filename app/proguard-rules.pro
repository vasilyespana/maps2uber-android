# Keep Gson-serialized models (parsed from the resolve API).
-keep class dev.vasilyespana.maps2uber.core.network.** { *; }
-keep class com.google.gson.** { *; }
# osmdroid uses reflection for tile providers/overlays — keep it whole.
-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**
