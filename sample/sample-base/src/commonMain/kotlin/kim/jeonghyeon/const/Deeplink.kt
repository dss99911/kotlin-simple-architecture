package kim.jeonghyeon.const

import samplebase.generated.SimpleConfig

/**
 * Universal Link requires Apple developer account and device to test.
 * I have emulator only. so, I can't test it.
 * so, Used custom scheme deeplink. and also use same custom scheme on android to unify deeplink on both ios and android
 * TODO: Use Universal Link and use server url
 */
object Deeplink {
    private val prefix = "${SimpleConfig.deeplinkScheme}://${SimpleConfig.deeplinkHost}${SimpleConfig.deeplinkPrePath}"

    val DEEPLINK_PATH_SIGN_UP: String = "$prefix/signUp"
    val DEEPLINK_PATH_SIGN_IN: String = "$prefix/signIn"
}

