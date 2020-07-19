//
// Created by hyun kim on 04/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import Foundation
import SwiftUI
import KotlinApi

struct ApiSequentialScreen: Screen {
    var title = "Api Sequential Call".localized()

    @State var model = ApiSequentialViewModelIos()
    
    var content: some View {
        VStack {
            List(model.textList.value as! [String], id: \.self) { item in
                Text(item)
            }
            
            HStack {
                Text(model.viewModel.KEY1)
                TextField("Enter value", text: asStringBinding(model.input1))
            }
            
            HStack {
                Text(model.viewModel.KEY2)
                TextField("Enter value", text: asStringBinding(model.input2))
            }
            
            HStack {
                Text(model.viewModel.KEY3)
                TextField("Enter value", text: asStringBinding(model.input3))
            }
            
            Button(action: { self.model.onClick() }, label: { Text("Update") })
        }
    }
    

}

