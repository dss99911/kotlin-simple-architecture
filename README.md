# Kotlin Simple Architecture

Kotlin Simple Architecture is an example of unified framework for general purpose for all platform

# Goal
- Simplest/Low-code development
- Reduce leanring curve of development of each platform

# Features

- [API Interface](https://github.com/dss99911/kotlin-simple-architecture#api-interface)
- [API Binding](https://github.com/dss99911/kotlin-simple-architecture#api-binding)
- [MVVM on Multiplatform](https://github.com/dss99911/kotlin-simple-architecture#mvvm-on-multiplatform)
- [Sign-in/Sign-up, OAuth(google, facebook, etc)](https://github.com/dss99911/kotlin-simple-architecture#sign-insign-up-oauthgoogle-facebook-etc)
- [Deeplink](https://github.com/dss99911/kotlin-simple-architecture#deeplink)

# Dependency
- [Ktor](https://github.com/ktorio/ktor)
- [Sqldelight](https://github.com/cashapp/sqldelight)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)(for Android)
- [SwiftUI 2.0](https://developer.apple.com/xcode/swiftui/)(for Ios)

# Introduction
- the sample code is [here](https://github.com/dss99911/kotlin-simple-architecture/tree/master/sample)
- sample android application to install [here](https://play.google.com/store/apps/details?id=kim.jeonghyeon.sample.compose)
## API Interface
- Share api interface by client, server both
- You can call api like suspend function
    - No Http definition like GET, POST, Query, Body(if required, you can set it as well)
    - so, simply make function. and client use the function and server implement the function. that's it.
- If you have to define Http request(like external api), it's also available
    - check this [sample](https://github.com/dss99911/kotlin-simple-architecture/blob/master/sample/sample-base/src/commonMain/kotlin/kim/jeonghyeon/sample/api/GithubApi.kt)
- How to handle error?
    - let's think about calling function, what function return when it's error?
    - it just throw exception instead of returning data which contains error data.
    - so, calling api also doesn't require to return response which contains error data. just throw exception

common

```kotlin
@Api
interface SampleApi {
    suspend fun getGreeting(name: String, job: String): String
}
```

client (for how to handle error, check here)

```kotlin
inline fun <reified API> api(baseUrl: String = serverUrl): API = client.create(baseUrl)

scope.launch {
    api<SampleApi>().getGreeting("Hyun", "Programmer")
}

```

backend

```kotlin

class SampleController : SampleApi {
    override suspend fun getGreeting(name: String, job: String): String = "Hello $name($job)"
}

install(SimpleFeature) {
    routing {
        +SampleController()
    }
}


```

## API Binding

This supports to call multiple api at once.
- no need to make new API for specific client requirements.
    - when a screen have to show some data from server. we can consider to add the data on existing api, or make new api.
    - to add data on existing api is not good in case of the data is not related to the existing api.
    - to make new api is not good as the screen have to call multiple api.
        - if one of the api is failed and one of other api is success, we have to decide to retry all again or just failed api.
        - it needs much of network communication
        - if those apis should be called sequentially like requesting order and get order history, in this case, as it's sequential, it takes a lot of time.
- support to use response of previous API as a request parameter.
    - for example, call order api, then call order detail api. order detail api requires orderId.
- sample code [here](https://github.com/dss99911/kotlin-simple-architecture/blob/master/sample/sample-base/src/mobileMain/kotlin/kim/jeonghyeon/sample/viewmodel/ApiBindingViewModel.kt)

common

```kotlin
@Api
interface SampleApi {
    suspend fun getGreeting(name: String, job: String): String
    suspend fun reply(answer: String): String
}
```

client
```kotlin
scope.launch {
    //this is group of response.
    val result: Pair<String, String> = bindApi {
        api.getGreeting("Hyun", "Programmer")
    }.bindApi {
        api.reply("Thanks")
    }.execute()
}

```

## MVVM on Multiplatform
- ios, android share ViewModel
- handle error on UI in simple way
    - load() : when call api or call function which takes time or can be error
    - data consist of data and status
        - data is just data
        - status is like loading, error, success. `state` may be proper name. but it's already used on JetCompose or StateFlow. so used status
        - [Resource](https://github.com/dss99911/kotlin-simple-architecture/blob/master/kotlin-simple-architecture/src/commonMain/kotlin/kim/jeonghyeon/type/Resource.kt) contains data and status both
    - initStatus, status
        - this is status without data.
        - this is to handle error or loading in common UI.
        - you can change common loading, error UI on Screen by overriding
        - you can use other Resource or Status to show different UI for status. but you have to define how to show the status on the screen
    - retry on Error
        - Resource contains retry().
        - Default error ui(snackbar) shows retry button and user can retry without additional code
- provides common functions on ViewModel and Screen
    - goBack() : you can return response, it's explained on [Navigate to the deeplink from Client / Server](https://github.com/dss99911/kotlin-simple-architecture#navigate-to-the-deeplink-from-client--server)
    - navigateToDeeplink()
    - loadInIdle() : for example, in case click button two times quickly. 2nd time click is ignored.
    - loadBounce() : for example, search with keyword. and if searching takes time(like api call is required), in that case, delay the api call and if next input comes, cancel previous call
- reactive way : you can compare the difference of reactive way and no reactive way below
    - [ReactiveViewModel](https://github.com/dss99911/kotlin-simple-architecture/blob/master/sample/sample-base/src/mobileMain/kotlin/kim/jeonghyeon/sample/viewmodel/ReactiveViewModel.kt)
    - [NoReactiveViewModel](https://github.com/dss99911/kotlin-simple-architecture/blob/master/sample/sample-base/src/mobileMain/kotlin/kim/jeonghyeon/sample/viewmodel/NoReactiveViewModel.kt)

common
```kotlin
class SampleViewModel(val api: SampleApi = serviceLocator.sampleApi) : BaseViewModel() {

    //[add] is for ios to watch each Flow
    //Todo DataFlow is similar with MutableSharedFlow or LiveData(android). after coroutine-1.4.0-M1-native-mt is released. DataFlow will be migrated to SharedFlow
    val greeting by add { DataFlow<String>() }
    val replyResult by add { DataFlow<String>() }

    override fun onInitialized() {
        //initStatus handle error and loading ui, also retry on error
        greeting.load(initStatus) {
            api.getGreeting("Hyun", "Programmer")
        }
    }

    fun onClick() {
        //initStatus hide ui on loading, error.
        //status doesn't hide ui but show loading, error ui
        replyResult.load(status) {
            api.reply("Thanks")
        }
    }
}
```

android
```kotlin
class SampleScreen(val model: SampleViewModel = SampleViewModel()) : Screen(model) {

    @Composable
    override fun view() {
        Column {
            Text("greeting : ${+model.greeting}")
            Text("reply result : ${+model.replyResult}")
            Button("Reply") {
                model.onClick()
            }
        }
    }

    //for composer to recognize SampleScreen as Composable
    @Composable
        override fun compose() {
            super.compose()
        }
}
```

ios
- you can see Swift UI's code is similar with Android Jetpack Compose
- so, this frameworks purpose is for developers not to study swift, IOS's architecture, IOS SDK deeply. just learn SwiftUI to draw UI
```kotlin
struct SampleScreen: Screen {

    var model: SampleViewModel = SampleViewModel()

    func content(navigator: Navigator) -> some View {
        Column {
            Text("current value : \(+model.greeting ?? "")")
            Text("reply result : \(+model.replyResult ?? "")")
            Button("Reply") {
                model.onClick()
            }
        }
    }
}
```

## Sign-in/Sign-up, OAuth(google, facebook, etc)
- Experimental, Security review is required.
- you can choose authentication method (basic, digest)
- you can choose session method (Session, JWT Token)
- This OAuth feature doesn't use android or ios library
    - each OAuth provider's library is better on user's navigation perspective. so, it may be supported in the future
    - but this approach also has merit that you can add any custom OAuth provider like [this](https://github.com/dss99911/kotlin-simple-architecture/blob/ff58afa111b1fbbd4cf67f572d26a46ce5449692/kotlin-simple-architecture/src/jvmMain/kotlin/kim/jeonghyeon/auth/SignInAuthConfiguration.kt#L126)
- we generally implement authentication, oauth for each product.
    - It's not easy to implement them as we have to consider security, and also implementation is not simple.
    - and it's used various product. so, it's better to support by framework side
    - but the implementation is various on different product.
    - so, I seperated it to common part and customization part. This library provides common part. so, developer just configure it, then customize it for their product requirement.

backend
```kotlin
install(SimpleFeature) {
    sign {

        //sign-in with basic authentication
        basic {
            //you can set controller to customize to add addtional user information.
        }

        //or sign-in with digest authentication
        //digest {
        //    //you can set controller to customize to add addtional user information.
        //}


        //use Session
        serviceAuthConfig = SessionServiceAuthConfiguration()

        //or use JWT token
        //serviceAuthConfig = JwtServiceAuthConfiguration(jwtAlgorithm)

        //OAuth (you can add custom OAuth provider as well)
        oauth {
            //you can set controller to customize to add addtional user information.

            google(
                googleClientId,
                googleClientSecret
            )
            facebook(
                facebookClientId,
                facebookClientSecret
            )
        }
    }
}
```

client

```
val signApi = client.createSignApi(serverUrl, SignInAuthType.DIGEST)
signApi.signUp(id, password, extra)
signApi.signIn(id, password)
signApi.signOut()

//for OAuth
val oAuthClient = SignOAuthClient(serverUrl)
oAuthClient.signUp(OAuthServerName.GOOGLE, DEEPLINK_PATH_SIGN_UP)

//when OAuth signUp, client move to web browser and web browser redirect to deeplink with token
oAuthClient.saveToken(deepUrl)

```


## Deeplink
- share deeplink on android, ios, backend
- configure deeplink on Android, Ios easily
- Server can respond with deeplink for client to navigate to the deeplink
- Client can navigate to the deeplink with ViewModel fuction

### Define Deeplink on common

```kotlin
object DeeplinkUrl {
    val DEEPLINK_PATH_HOME: String = "$prefix/home"
    val DEEPLINK_PATH_SIGN_UP: String = "$prefix/signUp"
    val DEEPLINK_PATH_SIGN_IN: String = "$prefix/signIn"
    val DEEPLINK_PATH_DEEPLINK_SUB: String = "$prefix/deeplink-sub"
}
```

### Configure Deeplink on Android
1. configure deeplink path on AndroidManifest.xml
2. add deeplink and screen matching

```kotlin
MainActivity : BaseActivity() {
    override val deeplinks = mapOf(
            DeeplinkUrl.DEEPLINK_PATH_HOME to (HomeScreen::class to { HomeScreen() }),
            DeeplinkUrl.DEEPLINK_PATH_SIGN_UP to (SignUpScreen::class to { SignUpScreen() }),
            DeeplinkUrl.DEEPLINK_PATH_SIGN_IN to (SignInScreen::class to { SignInScreen() }),
            DeeplinkUrl.DEEPLINK_PATH_DEEPLINK_SUB to (DeeplinkSubScreen::class to { DeeplinkSubScreen() }),
    )
}
```

### Configure Deeplink on IOS
- Universal Link will be supported soon.

```swift
class SampleDeeplinker : Deeplinker {

    override func navigateToDeeplink<SCREEN>(
        data: DeeplinkData<SCREEN>
    ) -> Bool where SCREEN : Screen {
        let deeplink = DeeplinkUrl()
        let url = data.url.absoluteString
        if (url.starts(with: deeplink.DEEPLINK_PATH_HOME)) {
            navigate(to: HomeScreen(), data: data)
        } else if (url.starts(with: deeplink.DEEPLINK_PATH_SIGN_IN)) {
            navigate(to: SigninScreen(), data: data)
        } else if (url.starts(with: deeplink.DEEPLINK_PATH_SIGN_UP)) {
            navigate(to: SignUpScreen(), data: data)
        } else if (url.starts(with: deeplink.DEEPLINK_PATH_DEEPLINK_SUB)) {
            navigate(to: DeeplinkSubScreen(), data: data)
        } else {
            return false
        }
        return true
    }
}
```

### Navigate to the deeplink from Client / Server
Just with configuration above, deeplink will navigate to the app. but, This provide further functions.

- navigate to the deeplink by BaseViewModel.navigateToDeeplink()
    - able to navigate to specific screen by viewModel. so no need set logic on android, ios both to navigate to the screen
    - able to set parameter and response also. check sample [here](https://github.com/dss99911/kotlin-simple-architecture/blob/master/sample/sample-base/src/mobileMain/kotlin/kim/jeonghyeon/sample/viewmodel/DeeplinkSubViewModel.kt)
    - for example, you can navigate to sign-in screen by deeplink and after complete signin, reinitialize automatically. refer to [UserViewModel](https://github.com/dss99911/kotlin-simple-architecture/blob/e60221a3886bfb7d10f641187b359614a6b2ccaa/sample/sample-base/src/mobileMain/kotlin/kim/jeonghyeon/sample/viewmodel/UserViewModel.kt)

```kotlin
class DeeplinkViewModel() : BaseViewModel() {

    fun onClick() {
        navigateToDeeplink(DeeplinkUrl.DEEPLINK_PATH_SIGN_UP)
    }
}

```

- navigate to the deeplink by server controller
    - when some error occurred, we may let user to navigate to some Screen.
    - in that case, we don't need for client to add logic to navigate there.
    - just configure deeplink and server set deeplink on resposne
    - If the deeplink shouldn't be publicly open. make two type of deeplink(public, private)
    - also available to retry the errored api automatically after completing deeplink screen
        - for example, If there are some features which should be completed before navigate to the screen
        - like Sign in -> KYC -> create mpin -> navigated screen
        - how will you cover this? will you check all of these every time on each screen?
        - you can do it. but also cover it from server, server response with the required feature's deeplink. and after complete it, retry again.
        - we can retry the api by `RedirectionType.retry` on server side.
```kotlin
class SampleController : SampleApi {

    override suspend fun doSomething() {
        errorDeeplink(DeeplinkInfo(DeeplinkUrl.DEEPLINK_PATH_SIGN_UP, "Please Sign up for testing deeplink"))
    }
}

```

# Setup

### Environment (tested on macOS Big Sur with the below)
- IOS
    - Xcode 12 (for SwiftUI 2.0, Big Sur is required)
- Android
    - Android Studio 4.2 Canary 12
### Template
- no need to configure kotlin multiplatform, libraries. just download template project to start with this framework
    - [android](https://github.com/dss99911/kotlin-simple-architecture-template/tree/android)
    - [android + ios](https://github.com/dss99911/kotlin-simple-architecture-template/tree/android-ios)
    - [android + ios + backend](https://github.com/dss99911/kotlin-simple-architecture-template/tree/android-ios-backend)

### Use on existing project
1. project's build.gradle.kts
```kotlin
buildscript {
    repositories {
        jcenter()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    dependencies {
        classpath("kim.jeonghyeon:kotlin-simple-architecture-gradle-plugin:1.0.1")

        //required as Kotlin Simple Architecture depends on these libraries.
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.0")
        classpath("com.squareup.sqldelight:gradle-plugin:1.4.2")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.4.2")

        //for android only
        classpath("com.android.tools.build:gradle:4.2.0-alpha12")

        //for backend only (creating jar of backend)
        classpath("com.github.jengelman.gradle.plugins:shadow:5.1.0")
    }
}
```

2. module's build.gradle.kts
```kotlin
apply(plugin = "kim.jeonghyeon.kotlin-simple-architecture-gradle-plugin")
```

3. copy Jetpack Compose, SwiftUi related files
- as Jetpack Compose on library is not yet supported
- not yet support library of SwiftUi code.
- so, copy the files from template project.
- To use this library without copying will be supported soon.

### Running Sample in Local
- run backend `sh sh/runBackendLocal.sh`
- run android `sh sh/installAndroid.sh` (you can run with android Studio configuration)
- run ios : open xcode with path `sample/sample-native` (I tried `sh sh/installIos.sh`, it was working. but now not working. need to fix)


# Test on local
- sometimes we need mock server or fake api for client testing
- but it's not required anymore.
- when develop new api
- just add fake code on the backend controller. and make test code of the controller
- then run the server in local
- implement test code in client.
- run the test code with local ip address
    - for runing real code with local ip address
    - you don't need to configure local ip address to connect local server
    - just use `SimpleConfig.buildTimeLocalIpAddress` it's automatically generateed.
    - but the device and server should be in same network like same wifi
- after server's fake code is changed to real code, run the test code again.
- when we test, client code integrity depends on server code
- if server code has no bug, we don't need to make mock code of the api, just use the api
- but sometimes, it's difficult to make situation to test some case.
- in that case, make api which configure server data on dev environment.
- code integrity dependencies like below
    - screen -> viewModel -> repository -> api -> backend controller -> backend service
    - if A depends on B, if B code has no bug. we can use B without mock data of B.
    - so, this approach doesn't need any mock data. but use real code
    - if the code is not yet implemented, add fake code on that module.
    - you can check the concept [here](https://medium.com/@dss99911/simple-android-architecture-testing-efficiently-with-android-x-c1b9c6c81a20), it's for android testing. but concept is same


# Planning & Contributions
All issues and plan is described [here](https://hyun.myjetbrains.com/youtrack/agiles/108-0/109-0)
- If there are anyone who like the idea of this project, feel free to contributes, It'll be so much appreciated.
- Currently as a single developer. It's not easy to maintain all of this.


# License

```
Copyright 2020 Jeonghyun Kim

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.