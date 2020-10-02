//
//  SigninScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 04/09/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import sample_base

struct UserScreen: SampleScreen {
    
    var model: UserViewModel = UserViewModel()
    func content(navigator: Navigator) -> some View {
        ScrollView {
            VStack {
                if let userDetail = model.user.value {
                    Text("Id : \(userDetail.id!)")
                    Text("Name : \(userDetail.name)")
                    Button("Log Out") {
                        model.onClickLogOut()
                    }
                }
            }
        }
        .navigationTitle("User".localized())
    }
    
}


class UserScreen_Previews: PreviewProvider {
    static var previews: some View {
        UserScreen()
    }
    
}
