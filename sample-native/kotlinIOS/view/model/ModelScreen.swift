//
//  Architecture.swift
//  kotlinIOS
//
//  Created by hyun kim on 18/06/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import KotlinApi
struct ModelScreen: NavigationScreen {
    var title = "Model".localized()
    @State var model = EmptyViewModelIos()
    
    var content: some View {
        List {
            NavigationLink(destination: ApiSingleScreen()) {
                Text("Api Single Call").font(.headline)
            }
            NavigationLink(destination: ApiSequentialScreen()) {
                Text("Api Sequential Call").font(.headline)
            }
            NavigationLink(destination: ApiParallelScreen()) {
                Text("Api Parallel Call").font(.headline)
            }
            NavigationLink(destination: ApiPollingScreen()) {
                Text("Api Polling").font(.headline)
            }
            NavigationLink(destination: ApiDbScreen()) {
                Text("DB Api Together").font(.headline)
            }
        }
    }
    
}

struct ModelView_Previews: PreviewProvider {
    static var previews: some View {
        ModelScreen()
    }
}
