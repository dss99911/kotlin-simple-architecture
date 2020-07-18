//
// Created by hyun kim on 04/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import KotlinApi

struct ApiSingleView: Screen {
    var title = "ApiSingleView"
    
    @State var model = ApiSingleViewModelIos()
    
    var content: some View {
        VStack {
            Text("key : \(title) : \(model.result.value ?? "")")
            TextField("Enter value", text: asStringBinding(model.input))
            Button(action: { self.model.onClick() }, label: { Text("update")})
        }
    }
}
