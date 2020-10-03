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
        Column {
            List(model.textList.value as! [String], id: \.self) { item in
                Text(item)
            }
            
            Row {
                Text(model.KEY1)
                SampleTextField("Enter value", model.input1)
            }
            
            Row {
                Text(model.KEY2)
                SampleTextField("Enter value", model.input2)
            }
            
            Row {
                Text(model.KEY3)
                SampleTextField("Enter value", model.input3)
            }
            
            Button("Update") {
                model.onClick()
            }
        }
        .navigationTitle("Api Sequential Call".localized())
    }
    

}

