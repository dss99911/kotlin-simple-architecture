//
// Created by hyun kim on 19/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import sample_base
import SwiftUI

func ApiParallelScreen(_ model: ApiParallelViewModel) -> some View {
    Screen(model) {
        Column {
            List(model.list.asValue(viewModel: model) as! [KotlinPair<NSString, NSString>], id: \.self.first) { item in
                Text("key : \(item.first ?? ""), value : \(item.second ?? "")")
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
