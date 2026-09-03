# The rules from AOSP are located in proguard.flags file, we can just maintain Skin related rules here.

# Optimization options.
-allowaccessmodification
-dontusemixedcaseclassnames
-allowaccessmodification
-keepattributes InnerClasses, *Annotation*, Signature, SourceFile, LineNumberTable

# Remove some Kotlin overhead
-processkotlinnullchecks remove

# Common rules.
-keep class android.window.** { *; }
-keep class android.view.** { *; }

-keepclassmembers class * implements android.os.Parcelable {
  public static final ** CREATOR;
}

# Skin specific rules.
-keep,allowshrinking,allowoptimization class com.ozyern.skin.SkinProto$* { *; }
-keep,allowshrinking,allowoptimization class com.ozyern.skin.SkinApp { *; }
-keep,allowshrinking,allowoptimization class com.ozyern.skin.SkinLauncher { *; }
-keep,allowshrinking,allowoptimization class com.ozyern.skin.compatlib.** { *; }

-keep,allowshrinking,allowoptimization class com.google.protobuf.Timestamp { *; }
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }

# We intentionally remove it to replace Smartspacer's widget popup with our own Launcher3 popup
-dontwarn com.skydoves.balloon.*

# This shouldn't concern us much
-dontwarn androidx.window.extensions.**
-dontwarn androidx.window.sidecar.**
