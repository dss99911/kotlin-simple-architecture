//
//  ViewModelEx.swift
//  kotlinIOS
//
//  Created by hyun kim on 09/09/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

prefix func + (_ flow: Kotlin_simple_architectureDataFlow<NSString>) -> Binding<String> {
    return Binding<String>(get: { () -> String in flow.value as String? ?? "" }, set: { (v: String) in flow.value = NSString(utf8String: v)  })
}

typealias BaseViewModel = Kotlin_simple_architectureBaseViewModel
