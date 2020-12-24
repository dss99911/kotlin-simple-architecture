//
//  SigninScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 04/09/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import sample_base

func UserScreen(_ model: UserViewModel) -> some View {
    Screen(model) {
        ScrollableColumn {
            if let userDetail = model.user.asValue(viewModel: model) {
                Text("Id : \(userDetail.id!)")
                Text("Name : \(userDetail.name)")
                Button("Log Out") { model.onClickLogOut() }
            }
        }
    }
}
