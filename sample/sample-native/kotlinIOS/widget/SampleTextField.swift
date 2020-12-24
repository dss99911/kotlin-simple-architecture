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


func SampleTextField(_ title: String, _ text: Kotlin_simple_architectureViewModelFlow<NSString>) -> some View {
    TextField(title, text: ++text)
}

