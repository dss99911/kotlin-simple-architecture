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

func ApiExternalScreen(_ model: ApiExternalViewModel) -> some View {
    Screen(model) {
        Column {
            List(model.repoList.asValue(viewModel: model) as? [String] ?? [String](), id: \.self) { item in
                Text(item)
            }
            Row {
                SampleTextField("Keyword", model.input)
                Button("Call") {
                    model.onClickCall()
                }
            }
        }
    }
}
