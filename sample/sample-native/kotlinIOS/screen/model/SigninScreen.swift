//
//  SigninScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 04/09/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import sample_base

struct SigninScreen: SampleScreen {
    
    var model: SignInViewModel = SignInViewModel()
    func content(navigator: Navigator) -> some View {
        ScrollView {
            VStack {
                SampleTextField("Id", model.inputId)
                SampleTextField("Password", model.inputPassword)
                Button("Sign In") {
                    model.onClickSignIn()
                }
                Button("Sign Up") {
                    navigator.navigate(to: {
                        SignUpScreen()
                    }, onResult: { result in
                        model.onSignUpResult(result: result)
                    })
                }
            }
        }
        .navigationTitle("Sign in".localized())
    }
}


class SigninScreen_Previews: PreviewProvider {
    static var previews: some View {
        SigninScreen()
    }
    
}
