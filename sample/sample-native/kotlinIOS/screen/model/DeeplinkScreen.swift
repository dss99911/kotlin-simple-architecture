//
//  SigninScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 04/09/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import sample_base

func DeeplinkScreen(_ model: DeeplinkViewModel) -> some View {
    Screen(model) {
        ScrollableColumn {
            Button("Deeplink on client") { model.onClickClientDeeplink() }
            Button("Deeplink from server") { model.onClickServerDeeplink() }
            Button("Deeplink to home") { model.onClickGoToHome() }
            Button("Deeplink to signIn then home") { model.onClickGoToSignInThenGoHome() }
            Button("link to google") { model.onClickGoogleUrl() }
            SampleTextField("Input parameter", model.deeplinkSubRequest)
            Text("result value : \(+model.deeplinkSubResult)")
            Button("navigate to screen by deeplink only") { model.onClickNavigateByDeeplinkOnly() }
        }
    }
}
