//
//  ApiHeaderScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 25/10/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

func ApiHeaderScreen(_ model: ApiHeaderViewModel) -> some View {
    Screen(model) {
        Column {
            Text("current header : \(+model.result ?? "")")
            SampleTextField("Input custom header", model.input)
            Button("change header") { model.onClick() }
        }
    }
}
