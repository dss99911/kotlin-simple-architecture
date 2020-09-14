package kim.jeonghyeon.util

import io.ktor.http.*
import platform.Foundation.NSURL

object IosUtil {
    fun convertUrl(url: NSURL): Url = Url(url.absoluteString()?:"")
}