//
//  SigninScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 04/09/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import sample_base

struct DeeplinkSubScreen: SampleScreen {
    
    var model: DeeplinkSubViewModel = DeeplinkSubViewModel()
    func content(navigator: Navigator) -> some View {
        Column {
            SampleTextField("Input value", model.result)
            Button("OK") {
                model.onClickOk()
            }
        }
        .navigationTitle("Deeplink Sub")
    }
}


class DeeplinkSubScreen_Previews: PreviewProvider {
    static var previews: some View {
        DeeplinkSubScreen()
    }
}
