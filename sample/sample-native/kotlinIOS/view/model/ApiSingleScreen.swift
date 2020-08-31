//
// Created by hyun kim on 04/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base


struct ApiSingleScreen: Screen {
    var title = "Api Single Call".localized()
    
    @State var model = ApiSingleViewModelIos()
    
    var content: some View {
        VStack(alignment: .center) {
            Text("current value : \(model.result.value!)")
            TextField("Enter value", text: asStringBinding(model.input)).frame(width: 100, alignment: .center)
            Button(action: { self.model.onClick() }, label: { Text("Update")})
        }
    }
}

class ApiSingleScreen_Previews: PreviewProvider {
    static var previews: some View {
        ApiSingleScreen()
    }

}
