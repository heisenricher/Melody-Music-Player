# Proguard Rules for Melody

# Hilt rules
-keep class * implements id.zelory.compressor.Compressor
-keep public class * extends dagger.hilt.internal.GeneratedComponentManager
-keep class **_HiltModules* { *; }

# Media3 Session / Player rules
-keep class androidx.media3.session.** { *; }
-keep class androidx.media3.common.** { *; }
-keep class androidx.media3.exoplayer.** { *; }

# Room DB rules
-keep class * extends androidx.room.RoomDatabase
-keep class * implements androidx.room.RoomDatabase$Callback
-dontwarn androidx.room.RoomDatabase
