package kim.jeonghyeon.sample.repository

import samplebase.generated.SimpleConfig

actual val DEEPLINK_PATH_SIGN_UP: String = "${SimpleConfig.serverUrl}${SimpleConfig.deeplinkPrePath}/signUp"