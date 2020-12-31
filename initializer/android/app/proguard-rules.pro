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
-keepattributes SourceFile,LineNumberTable

# START : Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Change here kim.jeonghyeon.template
-keep,includedescriptorclasses class kim.jeonghyeon.template.**$$serializer { *; } # <-- change package name to your app's
-keepclassmembers class kim.jeonghyeon.template.** { # <-- change package name to your app's
    *** Companion;
}
-keepclasseswithmembers class kim.jeonghyeon.template.** { # <-- change package name to your app's
    kotlinx.serialization.KSerializer serializer(...);
}
# TODO Added in addition to kotlin serialization requirements.
#  It was not working by uncertain reason.
-keep @kotlinx.serialization.Serializable class ** {
    *;
}
# END : Kotlin Serialization


# keep data classes
-keepclasseswithmembers class kim.jeonghyeon.template.** {
    public ** component1();
    <fields>;
}

# keep classes which with Keep annotation.
-keep @kim.jeonghyeon.annotation.Keep class ** {
    *;
}