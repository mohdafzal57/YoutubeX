# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Gson rules
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod

## Injection rules
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type

# Keep DTOs from being obfuscated as they are used for JSON serialization/deserialization
-keep class com.mak.youtubex.data.remote.dto.** { *; }

# Keep Retrofit API interfaces
-keep interface com.mak.youtubex.data.remote.api.** { *; }

# Keep Hilt/Dagger generated classes if needed (usually handled by AAR rules, but being safe)
-keep class **_HiltModules* { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }
