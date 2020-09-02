//
// Created by hyun kim on 04/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import Foundation
import SwiftUI
import sample_base

struct ApiSequentialScreen: Screen {
    var title = "Api Sequential Call".localized()

    @State var model = ApiSequentialViewModel()
    
    var content: some View {
        VStack {
            List(model.textList.value as! [String], id: \.self) { item in
                Text(item)
            }
            
            HStack {
                Text(model.KEY1)
                TextField("Enter value", text: asStringBinding(model.input1))
            }
            
            HStack {
                Text(model.KEY2)
                TextField("Enter value", text: asStringBinding(model.input2))
            }
            
            HStack {
                Text(model.KEY3)
                TextField("Enter value", text: asStringBinding(model.input3))
            }
            
            Button(action: { self.model.onClick() }, label: { Text("Update") })
        }
    }
    

}

