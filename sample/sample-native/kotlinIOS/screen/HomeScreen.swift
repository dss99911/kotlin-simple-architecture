//
//  MainView.swift
//  kotlinIOS
//
//  Created by hyun kim on 12/04/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import sample_base
import SwiftUI

func HomeScreen(_ model: HomeViewModel) -> some View {
    TabView {
        ModelScreen()
            .tabItem {
                Image(systemName: "doc.text.fill")
                Text("Model")
            }
            .tag(0)
        
        ViewScreen()
            .tabItem {
                Image(systemName: "rectangle.split.3x3.fill")
                Text("View")
            }
            .tag(1)
    }
}
