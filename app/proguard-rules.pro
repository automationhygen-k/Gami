# Keep core game systems referenced via reflection by engine integration layers.
-keep class com.gami.juice.** { *; }

# Strip noisy logging in release.
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int i(...);
    public static int v(...);
}
