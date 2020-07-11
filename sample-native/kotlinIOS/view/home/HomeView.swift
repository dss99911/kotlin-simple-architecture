//
// Created by hyun kim on 04/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI

struct HomeView: View {
    var body: some View {
        TabView {
            NavigationView {
                ModelView()
                        .tabItem {
                            Image(systemName: "doc.text.fill")
                            Text("Model")
            }

                ViewView()
                        .tabItem {
                            Image(systemName: "rectangle.split.3x3.fill")
                            Text("View")
                        }
            }
            
        }
                .navigationBarTitle("Home")
    }
}
