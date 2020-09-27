//
// Created by hyun kim on 19/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

struct ApiBindingScreen: SampleScreen {

    var model = ApiBindingViewModel()

    func content(navigator: Navigator) -> some View {
        VStack {
            Text("Success")
        }
        .navigationTitle("Api Binding".localized())
    }

}
