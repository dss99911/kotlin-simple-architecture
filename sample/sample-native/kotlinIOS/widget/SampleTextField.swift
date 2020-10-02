//
//  SampleTextField.swift
//  kotlinIOS
//
//  Created by hyun kim on 02/10/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base


func SampleTextField<S>(_ title: S, _ text: Kotlin_simple_architectureDataFlow<NSString>) -> some View where S : StringProtocol {
    TextField(title, text: ++text)
}
