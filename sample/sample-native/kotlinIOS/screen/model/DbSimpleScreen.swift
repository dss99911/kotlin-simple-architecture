//
// Created by hyun kim on 20/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation

import SwiftUI
import sample_base

/// TODO this is not working https://github.com/cashapp/sqldelight/issues/1845
struct DbSimpleScreen: SampleScreen {

    var model = DbSimpleViewModel()

    func content(navigator: Navigator) -> some View {
        Column {
            List(model.wordList.value as! [Word], id: \.self.id) { item in
                Text("id : \(item.id), text : \(item.text)")
            }
            Row {
                SampleTextField("Enter value", model.newWord)
                Button("Add") {
                    model.onClickAdd()
                }
            }
        }
        .navigationTitle("Simple DB Call".localized())
    }

}
