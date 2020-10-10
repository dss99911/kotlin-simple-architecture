//
// Created by hyun kim on 19/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

struct NoReactiveScreen: SampleScreen {

    var model = NoReactiveViewModel()

    func content(navigator: Navigator) -> some View {
        Column {
            Row {
                SampleTextField("Input new row", model.newWord)
                Button("Add") {
                    model.onClick()
                }
            }
            SampleTextField("Search", model.keyword)
            
            List(model.list.value as? [String] ?? [String](), id: \.self) { item in
                Text("\(item)")
            }
        }
        .navigationTitle("Reactive".localized())
    }

}
