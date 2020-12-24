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


func ApiAnnotationScreen(_ model: ApiAnnotationViewModel) -> some View {
    Screen(model) {
        Column(alignment: .center) {
            Text("current value : \(+model.result)")
            SampleTextField("update", model.input).frame(width: 100, alignment: .center)
            Button("Update") {
                model.onClick()
            }
        }
    }
}
