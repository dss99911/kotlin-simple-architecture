//
// Created by hyun kim on 04/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import KotlinApi
/*
* close() when View is destroyed
* watch() wrapper to Binding<>
* load(someBinding) {
*   api.doSomething() //return CFlow<T> and get T and set T to someBinding
* }
* when load is called. keep Closable and when destroy, close()
* destroy
    - !presentationMode.wrappedValue.isPresented
    - https://stackoverflow.com/questions/56513568/ios-swiftui-pop-or-dismiss-view-programmatically
* struct can't be inherited
* api call on init : use onAppearance or presentationMode.wrappedValue.isPresented
* consider weak static with get property, EnvironmentValues or EnvironmentObject and how to get manually without annotation
*/
//struct ApiSingleView: Screen {
//
//    @State var state: Resource<AnyObject> = Resource<AnyObject>.createStart()
//
//    @State var initState: Resource<AnyObject> = Resource<AnyObject>.createStart()
//
//    @State var result: String? = nil
//
//    let preferenceApi: PreferenceApiIos
//
//    @State var isInitialized = false
//
//    func onInitialized() {
//        print("onInitialized")
//
//        preferenceApi.getString(key: "aa").watch { resource in
//            print("getString \(resource)")
//
//            self.initState = resource.asStatus()
//            if (resource.isSuccess()) {
//                self.result = resource.successData() as String?
//            }
//        }
//    }
//
//    init(_ preferenceApi: PreferenceApiIos = PreferenceApiIos()) {
//        self.preferenceApi = preferenceApi
//
//
//    }
//
//    var children: some View {
//        VStack {
//            Text(result ?? "none")
//                .onAppear {
//                    if (!self.isInitialized) {
//                        self.isInitialized = true
//                        self.onInitialized()
//                    }
//
//            }
//            Button(action: {
//                self.preferenceApi.setString(key: "aa", value: "test").watch { (resource) in
//                    self.state = resource.asStatus()
//                    if (resource.isSuccess()) {
//                    self.result = "changed"
//                    }
//                }
//            }, label: { Text("change")})
//        }
//
//    }
//}


//struct ApiSingleView: View {
//
//    @State var result: String? = nil
//    @State var commonView: CommonStatusView? = nil
//
//    let preferenceApi: PreferenceApiIos
//
//    init(_ preferenceApi: PreferenceApiIos = PreferenceApiIos()) {
//        self.preferenceApi = preferenceApi
//    }
//
//    func initialize() {
//        print("onInitialized")
//
//        commonView?.watch(preferenceApi.getString(key: "aa")) { resource in
//            print("getString \(resource)")
//
//            self.commonView?.initStatus.set(resource.asStatus())
//            if (resource.isSuccess()) {
//                self.result = resource.successData() as String?
//            }
//        }
//    }
//
//    func onButtonClick() {
//        print("onButtonClick")
//        commonView?.watch(preferenceApi.setString(key: "aa", value: "test")) { resource in
//            self.commonView?.status.set(resource.asStatus())
//            if (resource.isSuccess()) {
//                self.result = "changed"
//            }
//        }
//    }
//
//    var body: some View {
//        print("body")
//        let view = CommonStatusView(onAppeared: { view in
//            self.commonView = view
//            self.initialize()
//        }) { parent in
//            VStack {
//                Text(self.result ?? "none")
//                Button(action: { self.onButtonClick() }, label: { Text("change") })
//            }
//        }.onAppear {
//
//        }
//
//        return view
//    }
//}


struct ApiSingleView: Screen2 {

    var commonView: Binding<CommonStatusView?> = createBinding(nil)

    let preferenceApi: PreferenceApiIos
    @State var result: String? = nil

    init(_ preferenceApi: PreferenceApiIos = PreferenceApiIos()) {
        self.preferenceApi = preferenceApi
    }

    func onInitialized() {
        print("onInitialized")
         
        loadDataAndStatus(data: $result, status: initStatus, flow: preferenceApi.getString(key: "aa")) { $0 as String? }
    }

    func onButtonClick() {
        print("onButtonClick")
        
        loadDataAndStatus(data: $result, status: status, flow: preferenceApi.setString(key: "aa", value: "test")) { _ in "changed" }
    
    }

    var children: some View {
        print("body")
        return VStack {
            Text(self.result ?? "none")
            Button(action: { self.onButtonClick() }, label: { Text("change") })
            NavigationLink(destination: ApiSequentialView()) {
                Text("Api Sequential Call").font(.headline)
            }
        }
    }
}

protocol Screen2 : View {
    associatedtype Children : View
    var commonView: Binding<CommonStatusView?> { get }
    var children: Self.Children { get }
    var initStatus: Status { get }
    var status: Status { get }

    func onInitialized()
    func watch<T> (_ flow: CRFlow<T>, _ onResource: @escaping (Resource<T>) -> Void)
}

extension Screen2 {

    //todo check if this save the value or call whenever refer it.


    /// Declares the content and behavior of this view.
    func onInitialized() {
        
    }
    
    var body: some View {
        print("body")
        return CommonStatusView(onAppeared: { view, initialized in
            self.commonView.wrappedValue = view
            if (!initialized) {
                self.onInitialized()
            }
        }) { parent in
            self.children
        }
    }
    
    func watch<T> (_ flow: CRFlow<T>, _ onResource: @escaping (Resource<T>) -> Void) {
        commonView.wrappedValue?.watch(flow, onResource)
    }
    
    /// you can set only non-nullable on CRFlow
    /// and resource.successData() is nullable even if it's actually non-nullable
    func loadDataAndStatus<T>(data: Binding<T?>, status: Status? = nil, flow: CRFlow<T>) {
        loadDataAndStatus(data: data, flow: flow) { value in
            value
        }
    }
    
    func loadDataAndStatus<T>(data: Binding<T>, status: Status? = nil, flow: CRFlow<T>) {
        loadDataAndStatus(data: data, flow: flow) { value in
            value!
        }
    }
    
    /// map is invoked only on success case
    func loadDataAndStatus<T, U>(data: Binding<U>, status: Status? = nil, flow: CRFlow<T>, map: @escaping (T?) -> U) {
        watch(flow) { resource in
            status?.set(resource.asStatus())
            if (resource.isSuccess()) {
                data.set(map(resource.successData()))
            }
        }
    }
    
    /// we don't know if U is nullable or not.
    /// if it's nullable, 'map' will be invoked. if it's non-nullable, 'map' won't be invoked
    /// T is not possible nullable. but the data can be nullable
    func load<T, U>(state: ResourceState<T>, flow: CRFlow<U>, _ map: @escaping (U?) -> T?) {
        watch(flow) { resource in
            state.set(resource.map(change: map) as! Resource<T>)
        }
    }
    
    func load<T>(state: ResourceState<T>, flow: CRFlow<T>) {
        load(state: state, flow: flow) { value in
            value
        }
    }

    var initStatus: Status {
        commonView.wrappedValue!.initStatus
    }
    var status: Status {
        commonView.wrappedValue!.status
    }
    
    static func createBinding<T>(_ initialValue: T) -> Binding<T> {
        var value: T = initialValue
        return Binding<T>(get: { () -> T in value }, set: { (v: T) in value = v;  })
    }
}

