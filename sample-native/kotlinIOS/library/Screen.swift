//
// Created by hyun kim on 08/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import KotlinApi
//struct Screen<Content> : View where Content : View {
//    init(initState: Binding<Bool>, state: Binding<Bool>, content: () -> Content) {
//
//    }
//    var body: some View {
//        VStack {
//            Text("")
//        }
//    }
//
//    init() {
//    }
//}
//
protocol Screen : View {
    associatedtype Children : View

    var children: Self.Children { get }
    
    var state: Resource<AnyObject> { get }
    var initState: Resource<AnyObject> { get }

    func onInitialized()
    
    var isInitialized: Bool { get }
}

extension Screen {

    //todo check if this save the value or call whenever refer it.


    /// Declares the content and behavior of this view.


    var body: some View {
        ZStack {
            if (initState.isLoading()) {
                Text("full Loading")
            } else if (initState.isError()) {
                Text("full Error")
            } else {
                children
                
                if (state.isLoading()) {
                    Text("Loading")
                } else if (state.isError()) {
                    Text("Error")
                }
            }
        }
    }
    
    var isInitialized: Bool {
        return true
    }

}
//func a() {
//    var state: Kotlin_simple_architectureResource<NSString>
//}
//class ApiScreen : View {
//
//
//    }
//    var body: some View {
//        Screen {
//            Text("")
//        }
//    }
//
//    init() {
//    }
//}
