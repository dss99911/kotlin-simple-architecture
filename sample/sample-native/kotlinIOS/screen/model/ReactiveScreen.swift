//
// Created by hyun kim on 19/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

func ReactiveScreen(_ model: ReactiveViewModel) -> some View {
    Screen(model) {
        Column {
            Button("No reactive example") { model.onClickNoReactiveSample() }

            Row {
                SampleTextField("Input new row",model.newWord)
                Button("Add") {
                    model.click.setValue(value: KotlinUnit())
                }
            }

            SampleTextField("Search", model.keyword)

            List(model.list.value as? [String] ?? [String](), id: \.self) { item in
                Text("\(item)")
            }
        }
    }
}
