//
//  SigninScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 04/09/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import sample_base

func DeeplinkSubScreen(_ model: DeeplinkSubViewModel) -> some View {
    Screen(model) {
        Column {
            SampleTextField("Input value", model.result)
            Button("OK") { model.onClickOk() }
        }
    }
}
