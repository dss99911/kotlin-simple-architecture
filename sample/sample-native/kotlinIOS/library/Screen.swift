//
// Created by hyun kim on 08/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import KotlinApi

/// todo: do we need Screen? how about just using StatusView?
protocol Screen : View {
    associatedtype Content : View
    associatedtype ViewModel : Kotlin_simple_architectureBaseViewModelIos
    var model: ViewModel { get }
    var content: Self.Content { get }
    var title: String { get }
    
}

extension Screen {
    var body: some View {
        StatusView(viewModel: model) {
            self.content
        }
        .navigationBarTitle(title)
    }
    
    var title: String {
        ""
    }
    
    func asStringBinding(_ flow: Kotlin_simple_architectureCFlow<NSString>) -> Binding<String> {
        return Binding<String>(get: { () -> String in flow.value as String? ?? "" }, set: { (v: String) in flow.value = NSString(utf8String: v)  })
    }
}

/// NavigationScreen include NavigationView on Screen.
/// because Screen doesn't contains NavigationView, so, if want to add NavigationView. it should be inside of StatusView
/// but, if NavigationView is inside of StatusView, StatusView's onAppear is not working(onAppear() should be inside of NavigationView), then Screen is not drawn and NavigationLink's View is not created again. so, sub view's initialization is not working properly
/// so, If need NavigationView, use this
protocol NavigationScreen : Screen {
    
}

extension NavigationScreen {
    var body: some View {
        NavigationView {
            StatusView(viewModel: model) {
                self.content
            }
            .navigationBarTitle(title)
        }
        
    }
}
