//
// Created by hyun kim on 04/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI

struct DrawerView: View {
    var body: some View {

        VStack {
            NavigationLink(destination: ModelView()) {
                Text("Model").font(.headline)
            }

        }
    }
}