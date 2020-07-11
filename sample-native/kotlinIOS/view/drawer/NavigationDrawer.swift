//
// Created by hyun kim on 04/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI

struct NavigationDrawer: View {
    private let width = UIScreen.main.bounds.width - 100
    let isOpen: Bool

    var body: some View {
        HStack {
            DrawerView()
                    .frame(width: self.width)
                    .offset(x: self.isOpen ? 0 : -self.width)
                    .animation(.default)
            Spacer()
        }
    }
}