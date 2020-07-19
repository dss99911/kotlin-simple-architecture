//
// Created by hyun kim on 19/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import KotlinApi

struct ApiDbScreen: Screen {
    var title = "DB Api Together".localized()
    @State var model = ApiDbViewModelIos()

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
