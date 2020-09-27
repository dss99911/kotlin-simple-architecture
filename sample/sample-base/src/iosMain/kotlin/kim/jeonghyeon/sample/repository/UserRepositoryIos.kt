package kim.jeonghyeon.sample.repository

import samplebase.generated.SimpleConfig

/**
 * Universal Link requires Apple developer account and device to test.
 * I have emulator only. so, I can't test it.
 * so, Used custom scheme deeplink
 * TODO: Use Universal Link
 */
actual val DEEPLINK_PATH_SIGN_UP: String = "kim.jeonghyeon.kotlinIOS:/${SimpleConfig.deeplinkPrePath}/signUp"