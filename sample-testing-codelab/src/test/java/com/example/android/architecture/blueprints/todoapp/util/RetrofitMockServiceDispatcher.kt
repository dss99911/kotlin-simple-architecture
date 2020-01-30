//package com.example.android.architecture.blueprints.todoapp.util
//
//import kim.jeonghyeon.androidlibrary.architecture.net.model.ResponseBody
//import kim.jeonghyeon.kotlinlibrary.extension.toJsonString
//import okhttp3.mockwebserver.Dispatcher
//import okhttp3.mockwebserver.MockResponse
//import okhttp3.mockwebserver.RecordedRequest
//import java.lang.reflect.Method
//import java.net.HttpURLConnection
//
//class RetrofitMockServiceDispatcher(vararg val mockServices: Any) : Dispatcher() {
//    override fun dispatch(request: RecordedRequest): MockResponse {
//        mockServices.forEach {
//            val method = findApiMethod(it, request)?:return@forEach
//
//            return makeResponse(callApi(it, method))
//        }
//
//        return makeNotFoundResponse()
//    }
//
//    private fun findApiMethod(obj: Any, request: RecordedRequest): Method? {
//        val apiInterface = obj.javaClass.interfaces[0]
//
//        obj.javaClass.methods.forEach {
//            //find interface's method
//            val interfaceMethod = apiInterface.findSameMethod(it)?:return@forEach
//
//            //check if matches request
//            if (interfaceMethod.matchRequest(request)) {
//                //return mock class's method
//                return it
//            }
//        }
//
//        //can't find. so return null
//        return null
//    }
//
//    private fun Class<*>.findSameMethod(method: Method): Method? {
//        javaClass.methods.forEach {
//            if (it.name == method.name
//                && it.parameterTypes.contentEquals(method.parameterTypes)
//                && it.returnType == method.returnType) {
//                return it
//            }
//        }
//
//        //if mock class has additional method. the method will be null
//        return null
//    }
//
//    private fun Method.matchRequest(request: RecordedRequest): Boolean {
//        TODO("HYUN [baselivedata] : mock webserver seems not required.")
//        //check request url
//
//        //if contains path, convert request url
//
//    }
//
//    private fun callApi(obj: Any, method: Method): ResponseBody<*> {
//        TODO("HYUN [baselivedata] : mock webserver seems not required.")
//        //consider path,
//    }
//
//    private fun makeResponse(responseBody: ResponseBody<*>): MockResponse =
//        MockResponse().setBody(responseBody.toJsonString())
//
//    private fun makeNotFoundResponse(): MockResponse =
//        MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
//}