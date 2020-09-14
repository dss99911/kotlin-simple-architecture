//
// Created by hyun kim on 19/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import sample_base
import SwiftUI

struct ApiParallelScreen: SampleScreen {

    var model = ApiParallelViewModel()

    func content(navigator: Navigator) -> some View {
        VStack {
            List(model.list.value as! [KotlinPair<NSString, NSString>], id: \.self.first) { item in
                Text("key : \(item.first!), value : \(item.second!)")
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
        .navigationTitle("Api Parallel Call".localized())
    }

}
