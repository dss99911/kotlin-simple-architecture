package kim.jeonghyeon.net

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import kim.jeonghyeon.auth.ServiceAuthType
import kim.jeonghyeon.auth.SignInAuthType
import kotlinsimplearchitecture.generated.net.createSimple
import samplebase.generated.SimpleConfig
import samplebase.generated.net.create

val AUTH_TYPE_SIGN_IN = SignInAuthType.DIGEST
val AUTH_TYPE_SERVICE = ServiceAuthType.JWT

val client: HttpClient by lazy {
    httpClientSimple {
        defaultRequest {
            //this is called whenever api is called
            header(HEADER_KEY, headerKeyValue)
        }

        /* Todo after ktor support coroutine-mt stable. try this.
        Caused by: kotlin.native.concurrent.InvalidMutabilityException: mutation attempt of frozen kotlinx.coroutines.AwaitAll.AwaitAllNode@4cc72a28
    at 0   kotlinIOS                           0x000000010806d77d kfun:kotlin.Throwable#<init>(kotlin.String?){} + 93
    at 1   kotlinIOS                           0x000000010806632b kfun:kotlin.Exception#<init>(kotlin.String?){} + 91
    at 2   kotlinIOS                           0x000000010806657b kfun:kotlin.RuntimeException#<init>(kotlin.String?){} + 91
    at 3   kotlinIOS                           0x000000010809c46b kfun:kotlin.native.concurrent.InvalidMutabilityException#<init>(kotlin.String){} + 91
    at 4   kotlinIOS                           0x000000010809dd22 ThrowInvalidMutabilityException + 690
    at 5   kotlinIOS                           0x00000001081986ec MutationCheck + 108
    at 6   kotlinIOS                           0x00000001081c45b0 kfun:kotlinx.coroutines.AwaitAll.AwaitAllNode.<set-handle>#internal + 96
    at 7   kotlinIOS                           0x00000001081c37ba kfun:kotlinx.coroutines.AwaitAll.await#internal + 1210
    at 8   kotlinIOS                           0x00000001081c2d5d kfun:kotlinx.coroutines.$awaitAllCOROUTINE$1#invokeSuspend(kotlin.Result<kotlin.Any?>){}kotlin.Any? + 1389
    at 9   kotlinIOS                           0x00000001081c302b kfun:kotlinx.coroutines#awaitAll@kotlin.collections.Collection<kotlinx.coroutines.Deferred<0:0>>(){0§<kotlin.Any?>}kotlin.collections.List<0:0> + 235
    at 10  kotlinIOS                           0x000000010834015f kfun:io.ktor.util.$split$lambda-0COROUTINE$3.invokeSuspend#internal + 2319
    at 11  kotlinIOS                           0x000000010808f2a6 kfun:kotlin.coroutines.native.internal.BaseContinuationImpl#resumeWith(kotlin.Result<kotlin.Any?>){} + 758
    at 12  kotlinIOS                           0x000000010822a68f kfun:kotlinx.coroutines.DispatchedTask#run(){} + 2767
    at 13  kotlinIOS                           0x000000010825df35 kfun:kotlinx.coroutines.DarwinMainDispatcher.dispatch$lambda-0#internal + 85
    at 14  kotlinIOS                           0x000000010825e2eb kfun:kotlinx.coroutines.DarwinMainDispatcher.$dispatch$lambda-0$FUNCTION_REFERENCE$41.invoke#internal + 59
    at 15  kotlinIOS                           0x000000010825e34b kfun:kotlinx.coroutines.DarwinMainDispatcher.$dispatch$lambda-0$FUNCTION_REFERENCE$41.$<bridge-UNN>invoke(){}#internal + 59
    at 16  kotlinIOS                           0x000000010825f404 _6f72672e6a6574627261696e732e6b6f746c696e783a6b6f746c696e782d636f726f7574696e65732d636f7265_knbridge8 + 180
    at 17  libdispatch.dylib                   0x00000001093948ac _dispatch_call_block_and_release + 12
    at 18  libdispatch.dylib                   0x0000000109395a88 _dispatch_client_callout + 8
    at 19  libdispatch.dylib                   0x00000001093a3f23 _dispatch_main_queue_callback_4CF + 1152
    at 20  CoreFoundation                      0x00007fff203a8276 __CFRUNLOOP_IS_SERVICING_THE_MAIN_DISPATCH_QUEUE__ + 9
    at 21  CoreFoundation                      0x00007fff203a2b06 __CFRunLoopRun + 2685
    at 22  CoreFoundation                      0x00007fff203a1b9e CFRunLoopRunSpecific + 567
    at 23  GraphicsServices                    0x00007fff2b76edb3 GSEventRunModal + 139
    at 24  UIKitCore                           0x00007fff24660c73 -[UIApplication _run] + 912
    at 25  UIKitCore                           0x00007fff24665b84 UIApplicationMain + 101
    at 26  SwiftUI                             0x00007fff55fc2e27 $s7SwiftUI17KitRendererCommon33_ACC2C5639A7D76F611E170E831FCA491LLys5NeverOyXlXpFAESpySpys4Int8VGSgGXEfU_ + 119
    at 27  SwiftUI                             0x00007fff55fc2d9f $s7SwiftUI6runAppys5NeverOxAA0D0RzlF + 143
    at 28  SwiftUI                             0x00007fff55b11e7d $s7SwiftUI3AppPAAE4mainyyFZ + 61
    at 29  kotlinIOS                           0x0000000107edd4e1 $s9kotlinIOS9SampleAppV5$mainyyFZ + 33
         */

        //        install(Logging) {
//            logger = Logger.DEFAULT
//            level = LogLevel.ALL
//        }
    }
}

inline fun <reified API> api(baseUrl: String = SimpleConfig.serverUrl): API = client.create(baseUrl)
inline fun <reified API> apiSimple(baseUrl: String = SimpleConfig.serverUrl): API = client.createSimple(baseUrl)

//just for sample showing how to set common header
const val HEADER_KEY = "KEY"
var headerKeyValue = "Header test"