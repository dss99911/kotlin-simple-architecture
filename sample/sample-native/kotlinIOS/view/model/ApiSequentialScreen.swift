//
// Created by hyun kim on 04/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import Foundation
import SwiftUI
import sample_base

struct ApiSequentialScreen: SampleScreen {
    
    var model = ApiSequentialViewModel()
    
    func content(navigator: Navigator) -> some View {
        VStack {
            List(model.textList.value as! [String], id: \.self) { item in
                Text(item)
            }
            
            HStack {
                Text(model.KEY1)
                TextField("Enter value", text: +model.input1)
            }
            
            HStack {
                Text(model.KEY2)
                TextField("Enter value", text: +model.input2)
            }
            
            HStack {
                Text(model.KEY3)
                TextField("Enter value", text: +model.input3)
            }
            
            Button(action: { self.model.onClick() }, label: { Text("Update") })
        }
        .navigationTitle("Api Sequential Call".localized())
    }
    

}

