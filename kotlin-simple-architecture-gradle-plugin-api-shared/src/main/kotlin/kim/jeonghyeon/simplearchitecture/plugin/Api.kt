package kim.jeonghyeon.annotation


//todo this Api class is used by both gradle plugin and source.
// so, I tried to make different gradle module with multiplatform
// but same error occurs with the below. I followed the suggestion there.
// but It was not working. so, I just hardcoded the Api class name
// https://youtrack.jetbrains.com/issue/KT-31641
// the reason seems that kapt can't figure out proper dependency between common and jvm
// How about adding this as well? `attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)`
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Api