package kim.jeonghyeon.const

import samplebase.generated.SimpleConfig

/**
 * Universal Link requires Apple developer account and device to test.
 * I have emulator only. so, I can't test it.
 * so, Used custom scheme deeplink. and also use same custom scheme on android to unify deeplink on both ios and android
 * TODO: Use Universal Link and use server url
 */
object DeeplinkUrl {
    private val prefix = "${SimpleConfig.deeplinkScheme}://${SimpleConfig.deeplinkHost}${SimpleConfig.deeplinkPrePath}"

    val DEEPLINK_PATH_HOME: String = "$prefix/home"
    val DEEPLINK_PATH_SIGN_UP: String = "$prefix/signUp"
    val DEEPLINK_PATH_SIGN_IN: String = "$prefix/signIn"
    val DEEPLINK_PATH_DEEPLINK_SUB: String = "$prefix/deeplink-sub"

    //oauth link
    //facebook w/android https://sample.jeonghyeon.kim/kim/jeonghyeon/auth/SignOAuthApi/signUp?serverUrl=https%3A%2F%2Fsample.jeonghyeon.kim&oAuthServer=%7B%22name%22%3A%22FACEBOOK%22%7D&redirectUrl=kim.jeonghyeon.kotlinios%3A%2F%2Fsample.jeonghyeon.kim%2Fdeeplink%2FsignUp&platform=ANDROID&packageName=kim.jeonghyeon.sample.compose
    //google w/android https://sample.jeonghyeon.kim/kim/jeonghyeon/auth/SignOAuthApi/signUp?serverUrl=https%3A%2F%2Fsample.jeonghyeon.kim&oAuthServer=%7B%22name%22%3A%22GOOGLE%22%7D&redirectUrl=kim.jeonghyeon.kotlinios%3A%2F%2Fsample.jeonghyeon.kim%2Fdeeplink%2FsignUp&platform=ANDROID&packageName=kim.jeonghyeon.sample.compose
    //facebook w/ios https://sample.jeonghyeon.kim/kim/jeonghyeon/auth/SignOAuthApi/signUp?serverUrl=https%3A%2F%2Fsample.jeonghyeon.kim&oAuthServer=%7B%22name%22%3A%22FACEBOOK%22%7D&redirectUrl=kim.jeonghyeon.kotlinios%3A%2F%2Fsample.jeonghyeon.kim%2Fdeeplink%2FsignUp&platform=IOS&packageName=
    //google w/ios https://sample.jeonghyeon.kim/kim/jeonghyeon/auth/SignOAuthApi/signUp?serverUrl=https%3A%2F%2Fsample.jeonghyeon.kim&oAuthServer=%7B%22name%22%3A%22GOOGLE%22%7D&redirectUrl=kim.jeonghyeon.kotlinios%3A%2F%2Fsample.jeonghyeon.kim%2Fdeeplink%2FsignUp&platform=IOS&packageName=
}

