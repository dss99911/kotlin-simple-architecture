//
// Created by hyun kim on 04/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base


func ApiSingleScreen(_ model: ApiSingleViewModel) -> some View {
    Screen(model) {
        Column(alignment: .center) {
            Text("current value : \(model.result.asValue(viewModel: model) ?? "")")
            SampleTextField("Enter value", model.input).frame(width: 100, alignment: .center)
            Button("Update") { model.onClick() }
        }
    }
}
