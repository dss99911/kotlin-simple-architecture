This is sample of Kotlin Simple Architecture

Download [Sample android application](https://play.google.com/store/apps/details?id=kim.jeonghyeon.sample.compose)

# Used Framework & Library
- Android - Jetpack Compose
- IOS - SwiftUi 2.0
- Backend - Ktor

# This covers some common use cases below

- Single Api Call
- Sequential Api Call
- Parallel Api Call
- Polling
- Use DB
- Use DB and Api together in Repository
- Configure common header, and Http request
- External Api Call
- Sign-in, OAuth
- Api Binding
- Deeplink
- Search
- Retrofit


# Setup & Run

Common
1. Set JDK 11 path on gradle.properties

Android
1. run android by `sh shell/installAndroid.sh` (you can run with android Studio configuration)
2. connect same wifi with desktop(for running on local)

Backend
1. run backend by `sh shell/runBackendLocal.sh`
2. connect same wifi with Android(for running local)

Ios
1. open xcode with path `sample-native`
2. run by Xcode (I tried `sh shell/installIos.sh`, it was working. but now not working. need to fix it. consider Intellij plugin to run ios)
