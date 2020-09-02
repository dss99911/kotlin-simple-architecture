//
//  ApiPollingScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 19/07/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

struct ApiPollingScreen: Screen {
    var title = "Api Polling".localized()
    @State var model = ApiPollingViewModel()

    var content: some View {
        VStack {
            Text("fail count \(model.count.value as! Int)")
            if (model.status.value!.isSuccess()) {
                Text("result \(model.result.value!)")
            }
        }
    }

}
