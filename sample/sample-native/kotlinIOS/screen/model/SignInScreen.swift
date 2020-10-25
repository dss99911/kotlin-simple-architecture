//
//  SigninScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 04/09/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import sample_base

func SignInScreen(_ model: SignInViewModel) -> some View {
    Screen(model) {
        ScrollableColumn {
            SampleTextField("Id", model.inputId)
            SampleTextField("Password", model.inputPassword)
            Button("Sign In") { model.onClickSignIn() }
            Button("Sign Up") { model.onClickSignUp() }
        }
    }
}
