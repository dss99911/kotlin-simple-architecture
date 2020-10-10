//
// Created by hyun kim on 19/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

struct ApiDbScreen: SampleScreen {

    var model = ApiDbViewModel()

    func content(navigator: Navigator) -> some View {
        Column {
            List(model.wordList.value as? [Word] ?? [Word](), id: \.self.id) { item in
                Text("id : \(item.id), text : \(item.text)")
            }
            Row {
                SampleTextField("Enter value", model.newWord)
                Button("Add") {
                    model.onClickAdd()
                }
            }
        }
        .navigationTitle("DB Api Together".localized())
    }

}
