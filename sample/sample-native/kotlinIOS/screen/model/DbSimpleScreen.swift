//
// Created by hyun kim on 20/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation

import SwiftUI
import sample_base

func DbSimpleScreen(_ model: DbSimpleViewModel) -> some View {
    Screen(model) {
        Column {
            List(+model.wordList as? [Word] ?? [Word](), id: \.self.id) { item in
                Text("id : \(item.id), text : \(item.text)")
            }
            Row {
                SampleTextField("Enter value", model.newWord)
                Button("Add") {
                    model.onClickAdd()
                }
            }
        }
    }
}
