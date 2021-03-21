# Kotlin Simple Architecture
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/hyun/kotlin-simple-architecture/kotlin-simple-architecture-client/images/download.svg) ](https://bintray.com/hyun/kotlin-simple-architecture/kotlin-simple-architecture-client/_latestVersion)
![kotlin-version](https://img.shields.io/badge/kotlin-1.4.31-orange)<br/>
Kotlin Simple Architecture is a library for simple and easy development in Kotlin Multiplatform

# Table of Contents
- [Features](https://github.com/dss99911/kotlin-simple-architecture#features)
- [Dependency](https://github.com/dss99911/kotlin-simple-architecture#dependency)
- [Introduction](https://github.com/dss99911/kotlin-simple-architecture#introduction)
    - [Simple Api](https://github.com/dss99911/kotlin-simple-architecture#simple-api)
    - [Api Binding](https://github.com/dss99911/kotlin-simple-architecture#api-binding)
    - [MVVM on Multiplatform](https://github.com/dss99911/kotlin-simple-architecture#mvvm-on-multiplatform)
    - [Sign-in/Sign-up, OAuth(google, facebook, etc)](https://github.com/dss99911/kotlin-simple-architecture#sign-insign-up-oauthgoogle-facebook-etc)
    - [Deeplink](https://github.com/dss99911/kotlin-simple-architecture#deeplink)
- [Sample](https://github.com/dss99911/kotlin-simple-architecture#sample)
- [Setup](https://github.com/dss99911/kotlin-simple-architecture#setup)


# Features

- [Simple Api](https://github.com/dss99911/kotlin-simple-architecture#simple-api)
- [API Binding](https://github.com/dss99911/kotlin-simple-architecture#api-binding)
- [MVVM on Multiplatform](https://github.com/dss99911/kotlin-simple-architecture#mvvm-on-multiplatform)
- [Sign-in/Sign-up, OAuth(google, facebook, etc)](https://github.com/dss99911/kotlin-simple-architecture#sign-insign-up-oauthgoogle-facebook-etc)
- [Deeplink](https://github.com/dss99911/kotlin-simple-architecture#deeplink)

# Dependency
If you don't use specific feature, you don't need to add the dependency
- [Ktor](https://github.com/ktorio/ktor) : for [Simple Api](https://github.com/dss99911/kotlin-simple-architecture#simple-api)
- [Sqldelight](https://github.com/cashapp/sqldelight) : for [Sign-in/Sign-up, OAuth(google, facebook, etc)](https://github.com/dss99911/kotlin-simple-architecture#sign-insign-up-oauthgoogle-facebook-etc)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)([MVVM on Multiplatform](https://github.com/dss99911/kotlin-simple-architecture#mvvm-on-multiplatform) for Android)
- [SwiftUI 2.0](https://developer.apple.com/xcode/swiftui/)([MVVM on Multiplatform](https://github.com/dss99911/kotlin-simple-architecture#mvvm-on-multiplatform) for Ios)

# Introduction

## Simple Api
- Simple Api uses interface for api call similar with [Retrofit](https://square.github.io/retrofit/)
- It shares api interface between client, server both
- You can call api just like function
    - No Http definition like GET, POST, Query, Body(if required, you can set it as well)
    - So, You just simply make function on interface. and client call the function and server implement the function. that's it.
- If you have to define Http request(like calling external api), it's also available
    - Check [Define Http Request](https://github.com/dss99911/kotlin-simple-architecture/tree/master/api#define-http-request)
- How to handle error?
    - Let's think about calling function, what function return when it's error?
    - It just throw exception instead of returning data which contains error status.
    - So, calling api also doesn't require to return response which contains error status. just throw exception
    - For how to handle on viewModel, check [MVVM on Multiplatform](https://github.com/dss99911/kotlin-simple-architecture#mvvm-on-multiplatform)

Refer to [Simple Api Detail](https://github.com/dss99911/kotlin-simple-architecture/tree/master/api)

common
```kotlin
@Api
interface SampleApi {
    suspend fun getGreeting(name: String, job: String): String
}
```

client

```kotlin
val client: HttpClient = HttpClient {
    install(JsonFeature) {
        //set your serializer
        serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
        })
    }
}

inline fun <reified API> api(baseUrl: String = serverUrl): API = client.create(baseUrl)

scope.launch {
    val greeting = api<SampleApi>().getGreeting("Hyun", "Programmer")
}

```

backend

```kotlin

class SampleController : SampleApi {
    override suspend fun getGreeting(name: String, job: String): String = "Hello $name($job)"
}

install(SimpleRouting) {
    +SampleController()
}

```

## API Binding

API Binding provides the function to call multiple api at once.

- This feature supports to use the response of previous API as a request parameter.
    - For example, call order api, then call order detail api. order detail api requires orderId.

- Merit
    - reduce latency of multiple API call
    - easy to organize well designed API
        - If there is a UI that needs multiple data.
        - We consider to add new api or add data to existing API.
        - Normally, It's good to add new API, but it's required to call multiple API

- refer to [Sample code](https://github.com/dss99911/kotlin-simple-architecture/blob/master/sample/sample-base/src/mobileMain/kotlin/kim/jeonghyeon/sample/viewmodel/ApiBindingViewModel.kt)

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
### Android, IOS share ViewModel
- It uses MutableSharedFlow for communicating UI and ViewModel
- This shows loading, error UI on each state in simple way
    - `load()` is used when calling api or calling function which can takes long time or can throw error
    - [Resource](https://github.com/dss99911/kotlin-simple-architecture/blob/master/kotlin-simple-architecture/src/commonMain/kotlin/kim/jeonghyeon/type/Resource.kt) consists of data and status
        - Data is the data which doesn't contain state
        - `Status` is like loading, error, success. `state` may be proper name. but it's already used on Jetpack Compose or StateFlow. so `status` is used
    - `initStatus`, `status` field on `BaseViewModel`
        - This is `Status` without data.
        - This is to handle error or loading in common UI.
        - You can customize UI of common loading, error
        - You can use other `Resource` or `Status` field to show different UI for status. but you have to define how to show the status on the screen
        - `initStatus` is for initial time. as initial time has no data, it just show loading or error UI without actual UI.
    - Retry on Error
        - `Resource` contains `retry()`
        - Default error ui(snackbar) shows retry button and user can retry by the button without additional implementation
- This provides common functions on ViewModel
    - `loadInIdle()` : For example, in case of clicking button two times quickly. 2nd time click is ignored.
    - `loadBounce()` : For example, searching with keyword. and if searching takes time(like api call is required), in that case, delay the api call and if next input comes, cancel previous call

common
```kotlin
class SampleViewModel(val api: SampleApi = serviceLocator.sampleApi) : BaseViewModel() {

    //ViewModelFlow is MutableSharedFlow. as Swift doesn't recognize generic of interface. just wrapped the flow with ViewModelFlow class.
    val greeting = viewModelFlow<String>()
    val replyResult = viewModelFlow<String>()

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
fun SampleScreen(val model: SampleViewModel) {
    Screen(model) {
        Column {
            Text("greeting : ${+model.greeting}")
            Text("reply result : ${+model.replyResult}")
            Button("Reply") { model.onClick() }
        }
    }
}
```

ios
- you can see Swift UI's code is similar with Android Jetpack Compose
- so, this framework's purpose is for developers not to study deeply of swift, IOS's architecture, IOS SDK. just learn SwiftUI to draw UI
- SwiftUi's View is normally with struct. but also use function base. this shows functions base. but you can use struct as well.
```swift
func SampleScreen(_ model: SampleViewModel) -> some View {
    Screen(model) {
        Column {
            Text("current value : \(+model.greeting)")
            Text("reply result : \(+model.replyResult)")
            Button("Reply") { model.onClick() }
        }
    }
}
```

### Navigation
- Navigation is processed on ViewModel side instead of UI
- 1 Screen is matched with 1 ViewModel. so navigate by creating ViewModel.
```kotlin
class SampleViewModel() : BaseViewModel() {
    fun onClickSIgnUp() {
        navigate(SignUpViewModel())
    }
}
```


- Navigate for result
```kotlin
class SampleViewModel() : BaseViewModel() {
    fun onClickSIgnUp() {
        status.loadInIdle {
            val result = navigateForResult(SignUpViewModel())
            if (result.isOk) {
                //success
            }
        }
    }
}
```

- Return result
```kotlin
class SampleViewModel() : BaseViewModel() {
    fun onClickOk() {
        goBackWithOk()//return success
        goBackWithOk(someData)// return success with data
    }
}

```


## Sign-in/Sign-up, OAuth(google, facebook, etc)
- Experimental, Security review is required.
- You can choose authentication method (basic, digest)
- You can choose session method (Session, JWT Token)
- This OAuth feature doesn't use Oauth provider's library, but use web browser.
    - Each OAuth provider's library is better on user's navigation perspective. so, it may be supported in the future
    - But this approach also has merit that you can add any custom OAuth provider like [this](https://github.com/dss99911/kotlin-simple-architecture/blob/ff58afa111b1fbbd4cf67f572d26a46ce5449692/kotlin-simple-architecture/src/jvmMain/kotlin/kim/jeonghyeon/auth/SignInAuthConfiguration.kt#L126)
- We generally implement authentication, oauth for each product.
    - It's not easy to implement them as we have to consider security, and also implementation is not simple.
    - And it's used on various product. so, it's better to support by framework side
    - But the implementation is various on different product.
    - So, I seperated it to common part and customization part. This library provides common part. so, developer just configure it, then customize it for their product requirement.

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
- Share deeplink on android, ios, backend
- Configure deeplink on Android, Ios easily
- Server can respond with deeplink for client to navigate to the deeplink
- Client can navigate to the deeplink with ViewModel fuction

### Define Deeplink on common

```kotlin
object DeeplinkUrl {
    val DEEPLINK_PATH_HOME: String = "$prefix/home"
    val DEEPLINK_PATH_SIGN_UP: String = "$prefix/signUp"
    val DEEPLINK_PATH_SIGN_IN: String = "$prefix/signIn"
}
```
### Configure Deeplink on Client
```kotlin
val deeplinkList: List<Deeplink> = listOf(
    Deeplink(DeeplinkUrl.DEEPLINK_PATH_HOME, HomeViewModel::class) { HomeViewModel() },
    Deeplink(DeeplinkUrl.DEEPLINK_PATH_SIGN_UP, SignUpViewModel::class) { SignUpViewModel() },
    Deeplink(DeeplinkUrl.DEEPLINK_PATH_SIGN_IN, SignInViewModel::class) { SignInViewModel() },
)

```

### Configure Deeplink on Android
1. Configure deeplink path on AndroidManifest.xml
2. Configure deeplinkList on BaseActivity

```kotlin
MainActivity : BaseActivity() {
    override val deeplinks: List<Deeplink> = deeplinkList
}
```

### Configure Deeplink on IOS
- Universal Link will be supported soon.

```swift
func MainActivity() -> some View {
    //just set `DeeplinkKt.deeplinkList` on deeplinks parameter
    BaseActivity(rootViewModel: HomeViewModel(), deeplinks: DeeplinkKt.deeplinkList) { viewModel in
        .
        .
        .
    }
}
```

### Navigate to the deeplink from Server
Just with configuration above, deeplink will navigate to the app. but, This provide further functions.

- When some error occurred, we may let user to navigate to some Screen.
- In that case, we don't need for client to add logic to navigate there.
- Just configure deeplink and server set deeplink on response
- If the deeplink shouldn't be publicly open. you can make two type of deeplink(public, private)
- Also available to retry the errored api automatically after completing deeplink screen
    - For example, If there are some features which should be completed before navigate to the screen
    - Like Sign in -> KYC -> create mpin -> navigated screen
    - How will you handle this? will you check all of these every time on each screen?
    - You can do it. but also it can be handled by server side, server responds with the deeplink of the required feature. and after it completed, retry again.
    - we can retry the api by `RedirectionType.retry` on server side.

```kotlin
class SampleController : SampleApi {

    override suspend fun doSomething() {
        errorDeeplink(DeeplinkInfo(DeeplinkUrl.DEEPLINK_PATH_SIGN_UP, "Please Sign up for testing deeplink"))
    }
}

```

# Sample
- [Common Sample](https://github.com/dss99911/kotlin-simple-architecture/tree/master/sample)

# Setup

### Environment (tested on macOS Big Sur with the below)
If you don't use [MVVM on Multiplatform](https://github.com/dss99911/kotlin-simple-architecture#mvvm-on-multiplatform), lower version also available
- IOS
    - Xcode 12 (for SwiftUI 2.0, Big Sur is required)
- Android
    - Android Studio Arctic Fox | 2020.3.1 Canary 2
    - Java 11 (install java 11, then add `org.gradle.java.home={java-11-path}` to gradle.properties)

### Initializer
- No need to configure kotlin multiplatform, libraries. just download initializer project to start with Simple Architecture
    - [android](https://github.com/dss99911/kotlin-simple-architecture/blob/master/initializer/android)
    - [android + ios](https://github.com/dss99911/kotlin-simple-architecture/blob/master/initializer/android-ios)
    - [android + ios + backend](https://github.com/dss99911/kotlin-simple-architecture/blob/master/initializer/android-ios-backend)

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
        classpath("kim.jeonghyeon:kotlin-simple-architecture-gradle:{latest-version}")

        //required as Kotlin Simple Architecture depends on these libraries.
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:{kotlin-version}")
        classpath("com.squareup.sqldelight:gradle-plugin:{sqldelight-version}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:{serialization-version}")

        //for android, if not use Jetpack compose
        classpath("com.android.tools.build:gradle:4.1.1")
        //for android, if use Jetpack compose
        classpath("com.android.tools.build:gradle:7.0.0-alpha09")

        //for backend (creating jar of backend)
        classpath("com.github.jengelman.gradle.plugins:shadow:5.1.0")
    }
}
```

2. module's build.gradle.kts
```kotlin
apply(plugin = "kim.jeonghyeon.kotlin-simple-architecture-gradle")

dependencies {
    implementation "kim.jeonghyeon:kotlin-simple-architecture-client:{latest-version}"
    implementation "kim.jeonghyeon:kotlin-simple-architecture-backend:{latest-version}"
}
```

3. Copy Jetpack Compose, SwiftUi related files
- Jetpack Compose/SwiftUi on library is not yet supported
- So, copy the files from initializer project.
- To use this library without copying these files will be supported soon.



# Thinking of Test on local
- Sometimes we need mock server or fake api for client testing
- But it's not required anymore.
- When develop new api
- Just add fake code on the backend controller. and make test code of the controller
- Then run the server in local
- Implement test code in client.
- Run the test code with local ip address
    - for runing real code with local ip address
    - you don't need to configure local ip address to connect local server
    - just use `SimpleConfig.buildTimeLocalIpAddress` it's automatically generateed.
    - but the device and server should be in same network like same wifi
- After server's fake code is changed to real code, run the test code again.
- when we test, client code integrity depends on server code
- If server code has no bug, we don't need to make mock code of the api, just use the api
- But sometimes, it's difficult to make situation to test some cases.
- In that case, make api which configure server data on dev environment.
- Code integrity dependencies like below
    - screen -> viewModel -> repository -> api -> backend controller -> backend service
    - If A depends on B, if B code has no bug. we can use B without mock data of B.
    - So, this approach doesn't need any mock data. but use real code
    - If the code is not yet implemented, add fake code on that module.
    - You can check the concept [here](https://medium.com/@dss99911/simple-android-architecture-testing-efficiently-with-android-x-c1b9c6c81a20), it's for android testing. but concept is same


# Planning & Contributions
All issues and plan is described [here](https://hyun.myjetbrains.com/youtrack/agiles/108-0/109-0)
- If there are anyone who like the approach of this project, feel free to contribute, It's always welcome.
- Currently as a single developer. It's not easy to maintain all of this.

# Future Plan

- Microservice
- Support JS (maybe with Web assembly)
- Testing
- Web Socket
- Scheduler
- Cache
- Paging


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