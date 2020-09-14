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

struct ApiExternalScreen: SampleScreen {
   
    var model = ApiExternalViewModel()

    func content(navigator: Navigator) -> some View {
        VStack {
            List(model.repoList.value as! [Repo], id: \.self.id) { item in
                Text("id : \(item.id), text : \(item.name)")
            }
            HStack {
                TextField("Keyword", text: +model.input)
                Button(action: { self.model.onClickCall() }, label: { Text("Call")})
            }
        }
        .navigationTitle("Api External Call".localized())
    }

}
