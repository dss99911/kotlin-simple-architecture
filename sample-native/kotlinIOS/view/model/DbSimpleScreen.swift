//
// Created by hyun kim on 20/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation

import SwiftUI
import KotlinApi

/// TODO this is not working https://github.com/cashapp/sqldelight/issues/1845
struct DbSimpleScreen: Screen {
    var title = "Simple DB Call".localized()
    @State var model = DbSimpleViewModelIos()

    var content: some View {
        VStack {
            List(model.wordList.value as! [Word], id: \.self.id) { item in
                Text("id : \(item.id), text : \(item.text)")
            }
            HStack {
                TextField("Enter value", text: asStringBinding(model.newWord))
                Button(action: { self.model.onClickAdd() }, label: { Text("Add")})
            }
        }
    }

}
