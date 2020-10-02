//
// Created by hyun kim on 04/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base


struct ApiSingleScreen: SampleScreen {
    
    var model: ApiSingleViewModel = ApiSingleViewModel()
    
    func content(navigator: Navigator) -> some View {
        Column(alignment: .center) {
            Text("current value : \(+model.result)")
            SampleTextField("Enter value", model.input).frame(width: 100, alignment: .center)
            Button("Update") {
                model.onClick()
            }
        }
        .navigationTitle("Api Single Call".localized())
    }
}

class ApiSingleScreen_Previews: PreviewProvider {
    static var previews: some View {
        ApiSingleScreen()
    }

}
