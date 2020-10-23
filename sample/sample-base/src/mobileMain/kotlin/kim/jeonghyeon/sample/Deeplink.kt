package kim.jeonghyeon.sample

import kim.jeonghyeon.client.Deeplink
import kim.jeonghyeon.const.DeeplinkUrl
import kim.jeonghyeon.sample.viewmodel.HomeViewModel
import kim.jeonghyeon.sample.viewmodel.SignInViewModel
import kim.jeonghyeon.sample.viewmodel.SignUpViewModel

val deeplinkList: List<Deeplink> = listOf(
    Deeplink(DeeplinkUrl.DEEPLINK_PATH_HOME, HomeViewModel::class) { HomeViewModel() },
    Deeplink(DeeplinkUrl.DEEPLINK_PATH_SIGN_UP, SignUpViewModel::class) { SignUpViewModel() },
    Deeplink(DeeplinkUrl.DEEPLINK_PATH_SIGN_IN, SignInViewModel::class) { SignInViewModel() },
)