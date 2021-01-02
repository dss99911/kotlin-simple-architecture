# Simple API

## Index
- Description
- Simple Usage
- Define Http Request
- Request Response Adapter
- Retrofit Migration
- Setup
- Sample
- Initializer

## Description
- use interface for api call similar with [Retrofit](https://square.github.io/retrofit/)
- Share api interface by client, server both
- You can call api like suspend function
    - No Http definition like GET, POST, Query, Body(if required, you can set it as well)
    - So, You just simply make function on interface. and client call the function and server implement the function. that's it.
- If you have to define Http request(like calling external api), it's also available
    - Check this [sample](https://github.com/dss99911/kotlin-simple-architecture/blob/master/sample/sample-base/src/commonMain/kotlin/kim/jeonghyeon/sample/api/GithubApi.kt)
- How to handle error?
    - Let's think about calling function, what function return when it's error?
    - It just throw exception instead of returning data which contains error status.
    - So, calling api also doesn't require to return response which contains error status. just throw exception

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
    api<SampleApi>().getGreeting("Hyun", "Programmer")
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
When we call external api. or server doesn't use Simple Api, we have to define http request

Supported Method
- Get, Post, Put, Patch, Delete, Options, Head

Supported Type
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
1. Transform response to return type : in case that server response and return type is different. If client, server both use Simple Api. this is not required.
2. Perform some action or modification on request/respond

This is similar with Interceptor and CallAdapter of Okhttp, Retrofit

The Adapter has 4 function to implement
- beforeBuildRequest : before build request
- buildRequest : after building request
- transformResponse : transform response to return type
- handleException : handle the exception

TODO check sample code

Transform response to return type
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

Perform some action or modification on request/respond
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
As this is similar with Retrofit.
this provide smooth migration

There are two steps
1. Change HttpClient
2. Change Annotations

Before migration, you won't be sure if the Simple Api will work properly with your project.
There are lots of api interface of retrofit in your project. and it's not easy to migrate all the api interface.
So, Simple Api support Retrofit annotation as well.
It means that you don't need to change Retrofit Annotations to Simple Api.
instead, Just change HttpClient.

If the responseType and returnType is different, check [Request Response Adapter](#)

### Limitation
- it doesn't support Retrofit Response or Call type like the below.

```kotlin
interface RetrofitApi {
    suspend fun get(): Response<SomeData>
    fun get2(): Call<SomeData>
}


## Setup

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
    implementation "kim.jeonghyeon:kotlin-simple-architecture-client:{latest-version}"
}

```

Todo check initializer/android-backend-api-only


## Sample
- Simple Architecture sample
- Retrofit & Simple Api sample
-

## Initializer
As it's complicated to configure build script.
Simple Api provide initialzer
- Android + Backend

Simple Architecture provide several initializer
- Android
- Android + Ios
- Android + Ios + Backend