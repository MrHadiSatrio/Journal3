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

# Added following the suggestions from R8 (i.e., missing_rules.txt).
# These missing classes were referenced from `org.tensorflow.lite.task`
# and `org.slf4j.LoggerFactory`, but I couldn't find any direct resources
# stating it's okay for them to be missing.
# There was, however, a GitHub Issue posted on the Mapbox SDK hinting
# that it's okay to ignore these warnings.
# https://github.com/mapbox/mapbox-gl-native/issues/12919
-dontwarn com.google.auto.value.AutoValue$Builder
-dontwarn com.google.auto.value.AutoValue
-dontwarn org.slf4j.impl.StaticLoggerBinder
