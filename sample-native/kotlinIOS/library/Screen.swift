//
// Created by hyun kim on 08/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import KotlinApi

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
    }
    
    var title: String {
        ""
    }
    
    func asStringBinding(_ flow: Kotlin_simple_architectureCFlow<NSString>) -> Binding<String> {
        return Binding<String>(get: { () -> String in flow.value as String? ?? "" }, set: { (v: String) in flow.value = NSString(utf8String: v)  })
    }
}
