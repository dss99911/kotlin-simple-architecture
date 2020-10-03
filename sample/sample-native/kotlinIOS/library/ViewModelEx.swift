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

//TODO: this only support Any type Any? type is not supported..
prefix func + (_ flow: Kotlin_simple_architectureDataFlow<NSString>) -> String {
    return flow.value! as String
}

prefix func + (_ flow: Kotlin_simple_architectureDataFlow<KotlinInt>) -> Int {
    return Int(truncating: flow.value!)
}

prefix func + <VALUE> (_ flow: Kotlin_simple_architectureDataFlow<VALUE>) -> VALUE {
    return flow.value!
}

prefix func ++ (_ flow: Kotlin_simple_architectureDataFlow<NSString>) -> Binding<String> {
    return Binding<String>(get: { () -> String in flow.value as String? ?? "" }, set: { (v: String) in flow.value = NSString(utf8String: v)  })
}

typealias BaseViewModel = Kotlin_simple_architectureBaseViewModel
