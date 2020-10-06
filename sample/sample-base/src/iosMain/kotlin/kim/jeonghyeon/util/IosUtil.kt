package kim.jeonghyeon.util

import io.ktor.http.*
import platform.Foundation.NSURL

//todo if this is in library module. it's not recognized.
// if find the way, move there
object IosUtil {
    fun convertUrl(url: NSURL): Url = Url(url.absoluteString()?:"")
}