//
// Created by hyun kim on 04/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import Foundation
import SwiftUI
import sample_base

func ApiSequentialScreen(_ model: ApiSequentialViewModel) -> some View {
    Screen(model) {
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
    } 
}
