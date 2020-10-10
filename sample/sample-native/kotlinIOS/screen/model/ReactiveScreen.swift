//
// Created by hyun kim on 19/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

struct ReactiveScreen: SampleScreen {

    var model = ReactiveViewModel()

    func content(navigator: Navigator) -> some View {
        Column {
            Button("No reactive example") {
                navigator.navigate {
                    NoReactiveScreen()
                }
            }
            Row {
                SampleTextField("Input new row", model.newWord)
                Button("Add") {
                    model.click.setValue(value: KotlinUnit())
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
