# Kotlin Simple Architecture

Kotlin Simple Architecture is a simple framework to develop easily reducing learning curve of various architecture and libraries
it provides latest architecture and common libraries with easy use.

# Features

- API Interface
- API Binding
- MVVM on Multiplatform

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

# Setup

### Environment (tested on macOS Big Sur with the below)
- IOS
    - Xcode 12 (for SwiftUI 2.0)
- Android
    - Android Studio 4.2 Canary 12
### Template
- no need to configure kotlin multiplatform. libraries. just download template project
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

# Goals
1. Simplest
2. Intuitive & Least learning curve
3. No extra knowledge required
4. Support all platform
5. Just add your business logic without technical code

# Merits by the Goals
1. Simple to read : even non-developer can understand
2. Simple to write : no boilerplate code, 1 line of code means 1 business logic. able to focus on business logic only
3. Simple learning curve : easy to remember usage. even it's easy, if you forget?, you can find where to see. and also it provides the sample as well.
4. Simple to use latest architecture : you don't need to spend a lot of time for what is latest architecture, how to make good code. just follow sample, then you'll write code at least not bad.
5. Simple to test : it provides base test classes and sample. just follow sample, then you can test.

# Articles

Todo : Multiplatform architecture for android, ios, frontend, backend

# Planning & Contributions
All issues and plan is described here.
Anyone can create ticket and contribute.
https://hyun.myjetbrains.com/youtrack/agiles/108-0/109-0

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