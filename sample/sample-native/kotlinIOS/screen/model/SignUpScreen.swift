//
//  SignUpScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 09/09/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

func SignUpScreen(_ model: SignUpViewModel) -> some View {
    Screen(model) {
        Column {
            SampleTextField("Id", model.inputId)
            SampleTextField("Name", model.inputName)
            SampleTextField("Password", model.inputPassword)
            Button("Sign up") { model.onClickSignUp() }
            Button("Google") { model.onClickGoogle() }
            Button("Facebook") { model.onClickFacebook() }
        }
    }
}
