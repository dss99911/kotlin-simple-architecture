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


struct ApiAnnotationScreen: SampleScreen {
    
    var model = ApiAnnotationViewModel()
    
    func content(navigator: Navigator) -> some View {
        
        Column(alignment: .center) {
            Text("current value : \(+model.result ?? "")")
            SampleTextField("update", model.input).frame(width: 100, alignment: .center)
            Button("Update") {
                model.onClick()
            }
        }
        .navigationTitle("Api Annotation Call".localized())
    }
}

class ApiAnnotationScreen_Previews: PreviewProvider {
    static var previews: some View {
        ApiSingleScreen()
    }

}
