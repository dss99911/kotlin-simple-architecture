//
//  SigninScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 04/09/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import sample_base

struct DeeplinkScreen: SampleScreen {
    
    var model: DeeplinkViewModel = DeeplinkViewModel()
    func content(navigator: Navigator) -> some View {
        ScrollableColumn {
            Button("Deeplink on client") {
                model.onClickClientDeeplink()
            }
            
            Button("Deeplink from server") {
                model.onClickServerDeeplink()
            }
            
            Button("Deeplink to home") {
                model.onClickGoToHome()
            }
            
            Button("Deeplink to signIn then home") {
                model.onClickGoToSignInThenGoHome()
            }
            
            Button("link to google") {
                model.onClickGoogleUrl()
            }
            
            SampleTextField("Input parameter", model.deeplinkSubRequest)
            Text("result value : \(+model.deeplinkSubResult)")
            
            Button("navigate to screen by deeplink only") {
                model.onClickNavigateByDeeplinkOnly()
            }
        }
        .navigationTitle("Deeplink".localized())
    }
}


class DeeplinkScreen_Previews: PreviewProvider {
    static var previews: some View {
        DeeplinkScreen()
    }
    
}
