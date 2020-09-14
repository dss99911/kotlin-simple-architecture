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
                if (model.user.value == nil) {
                    LogInView(navigator)
                } else {
                    LogOutView(navigator)
                }
            }
        }
        .navigationTitle("Sign in".localized())
    }
    
    func LogInView(_ navigator: Navigator) -> some View {
        VStack {
            TextField("Id", text: +model.inputId)
            TextField("Password", text: +model.inputPassword)
            Button("Sign In") {
                model.onClickSignIn()
            }
            Button("Sign Up") {
                navigator.navigate {
                    SignUpScreen()
                }
            }
        }
    }
    
    func LogOutView(_ navigator: Navigator) -> some View {
        VStack {
            let userDetail = model.user.value!
            Text("Id : \(userDetail.id!)")
            Text("Name : \(userDetail.name)")
            Button("Log Out") {
                model.onClickLogOut()
            }
        }
    }
    
}


class SigninScreen_Previews: PreviewProvider {
    static var previews: some View {
        SigninScreen()
    }
    
}
