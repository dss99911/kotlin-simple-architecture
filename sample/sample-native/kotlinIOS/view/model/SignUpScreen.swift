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

struct SignUpScreen : SampleScreen {
    
    var model: SignUpViewModel = SignUpViewModel()

    func content(navigator: Navigator) -> some View {
        VStack {
            TextField("Id", text: +model.inputId)
            TextField("Name", text: +model.inputName)
            TextField("Password", text: +model.inputPassword)
            Button("Sign up") {
                model.onClickSignUp()
            }
            Button("Google") {
                model.onClickGoogle()
            }
            Button("Facebook") {
                model.onClickFacebook()
            }
        }
        .navigationTitle("Sign up".localized())
    }
}
