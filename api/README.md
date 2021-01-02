# Simple API

## Index
- [Description](https://github.com/dss99911/kotlin-simple-architecture/tree/master/api#description)
- [Simple Usage](https://github.com/dss99911/kotlin-simple-architecture/tree/master/api#simple-usage)
- [Define Http Request](https://github.com/dss99911/kotlin-simple-architecture/tree/master/api#define-http-request)
- [Request Response Adapter](https://github.com/dss99911/kotlin-simple-architecture/tree/master/api#request-response-adapter)
- [Retrofit Migration](https://github.com/dss99911/kotlin-simple-architecture/tree/master/api#retrofit-migration)
- [Setup](https://github.com/dss99911/kotlin-simple-architecture/tree/master/api#setup)
- [Sample](https://github.com/dss99911/kotlin-simple-architecture/tree/master/api#sample)
- [Initializer](https://github.com/dss99911/kotlin-simple-architecture/tree/master/api#initializer)

## Description
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
    - for how to handle on viewModel, check [MVVM on Multiplatform](https://github.com/dss99911/kotlin-simple-architecture#mvvm-on-multiplatform)

## Simple Usage

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

## Define Http Request
When we call external api or server doesn't use Simple Api, we have to define http request

#### Supported Method
- Get, Post, Put, Patch, Delete, Options, Head

#### Supported Type
- Body, Header, Path, Query

```kotlin
@Api("https://api.github.com")
interface GithubApi {
    @Get("search/repositories?sort=stars")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): RepoSearchResponse

    @Get("history/{id})
    suspend fun getHistory(@Path("id") id: String): String
}
```

## Request Response Adapter
This is used for the cases below
1. Transform response to return type : in case that server response and return type is different (If client, server both use Simple Api. this is not required)
2. Perform some action or modification on request/respond

This is similar with [Interceptor](https://square.github.io/okhttp/interceptors/) and [CallAdapter](https://square.github.io/retrofit/2.x/retrofit/retrofit2/CallAdapter.html) of Okhttp, Retrofit

The Adapter has 4 function to implement
- beforeBuildRequest : before build request
- buildRequest : after building request, you can retrive request data, and also can add/modify request data
- transformResponse : transform response to return type (when response type and return type is different. or for additional logic)
- handleException : handle the exception of api call

check [sample code](https://github.com/dss99911/kotlin-simple-architecture/blob/a1b7c1deffcdbccb74afeeb4fcd2160cc78e870f/sample/sample-base/src/main/java/kim/jeonghyeon/sample/api/RequestResponseAdapter.kt#L14)

Example : transform ResponseBody to just data
```kotlin

data class ResponseBody<T>(val data: T)

data class SomeData(val name: String, val id: String)

inline fun getCustomApiAdapter(): RequestResponseAdapter = object : RequestResponseAdapter() {
    override suspend fun <OUT> transformResponse(
        response: HttpResponse,
        callInfo: ApiCallInfo,
        returnTypeInfo: TypeInfo
    ): OUT {
        return if (returnTypeInfo.type == ResponseBody::class) {
            response.call.receive(returnTypeInfo) as OUT
        } else {
            (response.call.receive(typeInfo<ResponseBody<OUT>>()) as ResponseBody<OUT>).data
        }
    }
}

HttpClient {
    install(SimpleApiCustom) {
        adapter = getCustomApiAdapter()
    }
}

```

Example : add token on header while building request
```kotlin

inline fun getCustomApiAdapter(): RequestResponseAdapter = object : RequestResponseAdapter() {
    override suspend fun buildRequest(builder: HttpRequestBuilder, callInfo: ApiCallInfo) {
        //example to add token. but you can do this by defaultRequest feature of ktor
        builder.header("token", "some-token")
    }
}

HttpClient {
    install(SimpleApiCustom) {
        adapter = getCustomApiAdapter()
    }
}

```

## Retrofit Migration
As Simple Api is similar with Retrofit, it provides smooth migration from Retrofit to Simple Api

refer to [Retrofit & Simple Api sample](https://github.com/dss99911/kotlin-simple-architecture/blob/master/sample/sample-base/src/main/java/kim/jeonghyeon/sample/api/RetrofitApi.kt)

There are two steps to migrate

#### 1. Change HttpClient(Mandatory)
Before migration, you won't be sure if the Simple Api will work properly with your project.<br/>
There are lots of api interface of retrofit in your project. and it's not easy to migrate all the api interface.<br/>

So, Simple Api support Retrofit annotation.<br/>
It means that you don't need to change Retrofit Annotations to Simple Api.<br/>
instead, Just change HttpClient.

What you have to do is only the below
```kotlin
//this is the interface with Retrofit annotations
interface YourApi {
    @GET("your")
    suspend fun get(): SomeResponse
}

fun getYourApi(): YourApi {
    return HttpClient(OkHttp) {
        engine {
            //use your Okhttp interceptor here
            addInterceptor(interceptor)
        }
        install(JsonFeature) {
            //set your serializer
            serializer = GsonSerializer()
        }
    }.create(serverUrl)
}

suspend fun callApi() {
    val response = getYourApi().get()
}

```

If the responseType and returnType is different, check [Request Response Adapter](https://github.com/dss99911/kotlin-simple-architecture/tree/master/api#request-response-adapter)

#### 2. Change Annotations(Optional)
This is not mandatory. but Retrofit is only for JVM. Retrofit Annotation can't be used for other environment.<br/>
on that time, you may migrate to Simple Api annotations.

#### Limitation
- it doesn't support Retrofit [Response](https://square.github.io/retrofit/2.x/retrofit/retrofit2/Response.html) or [Call](https://square.github.io/retrofit/2.x/retrofit/retrofit2/Call.html) type like the below.

```kotlin
interface RetrofitApi {
    suspend fun get(): Response<SomeData>
    fun get2(): Call<SomeData>
}
```

## Setup
This Setup is only for Simple Api.<br/>
If you want to use Simple Architecture. check [Simple Architecture Setup](https://github.com/dss99911/kotlin-simple-architecture#setup)

#### Dependency
- [Ktor](https://github.com/ktorio/ktor)

project build.gradle.kts
```
buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    dependencies {
        classpath("kim.jeonghyeon:kotlin-simple-api-gradle:{latet-version}")
    }
}

System.setProperty(// Enabling kotlin compiler plugin
        "kotlin.compiler.execution.strategy",
        "in-process"
)
```

module build.gradle.kts

```
apply plugin: 'kim.jeonghyeon.kotlin-simple-api-gradle'

dependencies {
    implementation "kim.jeonghyeon:kotlin-simple-api-client:{latest-version}"
    implementation "kim.jeonghyeon:kotlin-simple-api-backend:{latest-version}"
}

```

refer to initializer of [Android + Backend](https://github.com/dss99911/kotlin-simple-architecture/tree/master/initializer/android-backend-api-only)


## Sample
- [Simple Architecture sample](https://github.com/dss99911/kotlin-simple-architecture/tree/master/sample)
- [Retrofit & Simple Api sample](https://github.com/dss99911/kotlin-simple-architecture/blob/master/sample/sample-base/src/main/java/kim/jeonghyeon/sample/api/RetrofitApi.kt)

## Initializer
As it's complicated to configure build script.
Simple Api provide initialzer
- [Android + Backend](https://github.com/dss99911/kotlin-simple-architecture/tree/master/initializer/android-backend-api-only)

Simple Architecture provide several initializer
- [Android](https://github.com/dss99911/kotlin-simple-architecture/tree/master/initializer/android)
- [Android + Ios](https://github.com/dss99911/kotlin-simple-architecture/tree/master/initializer/android-ios)
- [Android + Ios + Backend](https://github.com/dss99911/kotlin-simple-architecture/tree/master/initializer/android-ios-backend)