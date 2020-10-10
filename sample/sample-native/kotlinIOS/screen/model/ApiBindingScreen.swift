//
// Created by hyun kim on 19/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

struct ApiBindingScreen: SampleScreen {

    var model = ApiBindingViewModel()

    func content(navigator: Navigator) -> some View {
        ScrollableColumn {
            Text("Result : \(+model.result)")
            Button("Bind 2 Api") {
                model.onClickBind2Api()
            }
            
            Button("Bind 3 Api") {
                model.onClickBind3Api()
            }
            
            Button("Bind Response to Parameter") {
                model.onClickBindResposneToParameter()
            }
            
            Button("Bind Response's Field to Parameter") {
                model.onClickBindResposneFieldToParameter()
            }
            
            Button("Handle Error") {
                model.onClickHandleError()
            }
            
            Button("Bind Api with Auth") {
                model.onClickBindApiAuthRequired()
            }
        }
        .navigationTitle("Api Binding".localized())
    }

}