//
//  ApiTestScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 30/12/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

func ApiTestScreen(_ model: ApiTestViewModel) -> some View {
    Screen(model) {
        Column {
            Text("Success")
        }
    }
}
