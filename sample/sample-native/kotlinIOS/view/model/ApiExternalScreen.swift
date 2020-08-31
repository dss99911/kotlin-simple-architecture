//
//  ApiExternalScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 08/08/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation

import SwiftUI
import sample_base

struct ApiExternalScreen: Screen {
    var title = "Api External Call".localized()
    @State var model = ApiExternalViewModelIos()

    var content: some View {
        VStack {
            List(model.repoList.value as! [Repo], id: \.self.id) { item in
                Text("id : \(item.id), text : \(item.name)")
            }
            HStack {
                TextField("Keyword", text: asStringBinding(model.input))
                Button(action: { self.model.onClickCall() }, label: { Text("Call")})
            }
        }
    }

}
