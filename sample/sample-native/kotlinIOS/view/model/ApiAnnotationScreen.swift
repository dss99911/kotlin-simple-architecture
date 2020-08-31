//
//  ApiAnnotationScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 08/08/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

struct ApiAnnotationScreen: Screen {
    var title = "Api Annotation Call".localized()
    
    @State var model = ApiAnnotationViewModelIos()
    
    var content: some View {
        VStack(alignment: .center) {
            Text("current value : \(model.result.value!)")
            TextField("update", text: asStringBinding(model.input)).frame(width: 100, alignment: .center)
            Button(action: { self.model.onClick() }, label: { Text("Update")})
        }
    }
}

class ApiAnnotationScreen_Previews: PreviewProvider {
    static var previews: some View {
        ApiSingleScreen()
    }

}
