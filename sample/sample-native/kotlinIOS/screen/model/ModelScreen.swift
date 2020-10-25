//
//  Architecture.swift
//  kotlinIOS
//
//  Created by hyun kim on 18/06/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import sample_base

func ModelScreen() -> some View {
    List(ModelViewModel.Companion.init().items, id: \.self) { item in
        Button(item.generate().title) {
            navigator.navigate(viewModel: item.generate())
        }
    }
}
