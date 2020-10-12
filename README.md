# Kotlin Simple Architecture

Kotlin Simple Architecture is a simple framework to pursue the below
- To develop easily reducing learning curve of various architecture and libraries
- Low code development. so, Library provide all common logic like sign-in, oauth, etc

# Features

- API Interface
- API Binding
- MVVM on Multiplatform
- Sign-in/Sign-up, OAuth(google, facebook, etc)
- Deeplink

# Dependency
- Ktor
- Sqldelight
- Jetpack Compose(for Android)
- SwiftUI 2.0(for Ios)

# Intro

## API Interface
- You can call api like suspend function
- No REST client definition like GET, POST, Query, Body(if required, you can set it as well)
- Use same api interface by client, server both

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
- support to use response of previous API as a request parameter. (check here)

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
- ios, android use common ViewModel
- provides common functions on ViewModel and Screen

common
```kotlin
class SampleViewModel(val api: SampleApi = serviceLocator.sampleApi) : BaseViewModel() {

    //[add] is for ios to watch each Flow
    //DataFlow is similar with MutableStateFlow or LiveData(android)
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
- you can see Swift UI is similar with Android Jetpack Compose
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

### Sign-in/Sign-up, OAuth(google, facebook, etc)
- Experimental, Security check is required.
- you can choose authentication method (basic, digest)
- you can choose session method (Session, JWT Token)
- OAuth doesn't use android or ios library. but use web browser. so, you can add any custom OAuth provider.
- we implement sign-in, oauth for each product. but, I think we can seperate common part and customization part. This library provides common part. so, developer just configure it, then customize it for their product requirement.

backend
```kotlin
install(SimpleFeature) {
    sign {

        //sign-in with basic authentication
        basic {
            //you can set controller to customize to add addtional user information.
        }

        //or sign-in with digest authentication
        digest {
            //you can set controller to customize to add addtional user information.
        }


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


### Deeplink
- configure Deeplink on Android, Ios easily
- Server can respond with deeplink for client to navigate to the deeplink
- Client can navigate to the deeplink with ViewModel fuction

#### Define Deeplink on common

```kotlin
object DeeplinkUrl {
    val DEEPLINK_PATH_HOME: String = "$prefix/home"
    val DEEPLINK_PATH_SIGN_UP: String = "$prefix/signUp"
    val DEEPLINK_PATH_SIGN_IN: String = "$prefix/signIn"
    val DEEPLINK_PATH_DEEPLINK_SUB: String = "$prefix/deeplink-sub"
}
```

#### Configure Deeplink on Android
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

#### Configure Deeplink on IOS
- Universal Link is not yet supported.

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

#### Navigate to the deeplink from Client / Server
Just with configuration above, deeplink will navigate to the app
but, This provide further functions.

navigate to the deeplink by BaseViewModel.navigateToDeeplink()
```kotlin
class DeeplinkViewModel() : BaseViewModel() {

    fun onClick() {
        navigateToDeeplink(DeeplinkUrl.DEEPLINK_PATH_SIGN_UP)
    }
}

```

navigate to the deeplink by server controller
```kotlin
class SampleController : SampleApi {

    override suspend fun doSomething() {
        errorDeeplink(DeeplinkInfo(DeeplinkUrl.DEEPLINK_PATH_SIGN_UP, "Please Sign up for testing deeplink"))
    }
}

```

TODO : I'll explain the purpose of this functions on article


# Setup

### Environment (tested on macOS Big Sur with the below)
- IOS
    - Xcode 12 (for SwiftUI 2.0)
- Android
    - Android Studio 4.2 Canary 12
### Template
- no need to configure kotlin multiplatform, libraries. just download template project
    - [android](https://github.com/dss99911/kotlin-simple-architecture-template/tree/android)
    - [android + ios](https://github.com/dss99911/kotlin-simple-architecture-template/tree/android-ios)
    - [android + ios + backend](https://github.com/dss99911/kotlin-simple-architecture-template/tree/android-ios-backend)

### Use on existing project
project's build.gradle.kts
```kotlin
buildscript {
    repositories {
        jcenter()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    dependencies {
        classpath("kim.jeonghyeon:kotlin-simple-architecture-gradle-plugin:1.0.0")

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

module's build.gradle.kts
```kotlin
apply(plugin = "kim.jeonghyeon.kotlin-simple-architecture-gradle-plugin")
```

# Articles
TODO, 1. What we can do with Kotlin Multiplatform


# Planning & Contributions
All issues and plan is described [here](https://hyun.myjetbrains.com/youtrack/agiles/108-0/109-0)
Anyone can create ticket and contribute.

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