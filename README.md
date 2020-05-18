# Simple Android Architecture

this library is focusing on how to develop simple.

# Usage

Todo : apply plugin, add classpath of othe plugins

todo : is there way to remove pluginManagement below?
settings.gradle.kts
```
pluginManagement {
    resolutionStrategy {
        eachPlugin {
            val plugin = requested.id.id
            when (plugin) {
                "kotlinx-serialization" -> useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
            }
        }
    }
}

enableFeaturePreview("GRADLE_METADATA")
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

# Prerequisite
Todo : add prerequisite for ios, js, backend
## Client
- MVVM
- Kotlin, Coroutine

### Android
- Koin
- Jetpack : Navigation, Data Binding, LiveData, Paging, Room, ViewModel
- [Resource](https://developer.android.com/jetpack/docs/guide#addendum)
- AndroidX testing

# Articles

Todo : Multiplatform architecture for android, ios, frontend, backend

## Android Articles
- [Remove boilerplate code of RecyclerView][recyclerview]
- [Coroutine Api call and error handling in Retrofit2][coroutine]
- [LiveData and event][livedata]
- [MVVM concept and reduce boilerplate code on Activity/Fragment/ViewModel][mvvm]
- [Testing efficiently with Android X][testing]
- [Refactoring to new architecture][refactoring]

# Samples
- [Sample][sample] : shows usage of Simple Android Architecture one by one
- [Sunflower Sample][sample-sunflower] : this is converted version of [Android Sunflower sample][android-sunflower]
- [Testing Sample][sample-testing] : this is converted version of [Android Testing Codelab][android-testing]

# Planning
Currently this is focusing on only android development.
but with Kotlin multiplatform, will support spring, ktor, ios, frontend.

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
```
[livedata]: https://medium.com/@dss99911/simple-android-architecture-livedata-and-event-92f5f4b04af7
[recyclerview]: https://medium.com/@dss99911/simple-android-architecture-recyclerview-ef5fdd7dac0a
[sample]: https://github.com/dss99911/simple-android-architecture/tree/master/sample
[sample-sunflower]: https://github.com/dss99911/simple-android-architecture/tree/master/sample-sunflower
[sample-testing]: https://github.com/dss99911/simple-android-architecture/tree/master/sample-testing-codelab
[android-sunflower]: https://github.com/android/sunflower
[android-testing]: https://github.com/googlecodelabs/android-testing
[coroutine]: https://medium.com/@dss99911/simple-android-architecture-coroutine-api-call-and-error-handling-in-retrofit2-1677d0f84f56
[mvvm]: https://medium.com/@dss99911/simple-android-architecture-mvvm-concept-and-reduce-boilerplate-code-on-8ff1912286a8
[testing]: https://medium.com/@dss99911/simple-android-architecture-testing-efficiently-with-android-x-c1b9c6c81a20
[refactoring]: https://medium.com/@dss99911/simple-android-architecture-refactoring-to-new-architecture-c0a786caddf7
