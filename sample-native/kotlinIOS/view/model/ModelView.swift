//
//  Architecture.swift
//  kotlinIOS
//
//  Created by hyun kim on 18/06/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import KotlinApi
struct ModelView: Screen {
    @State var model = EmptyViewModelIos()

    var content: some View {
        List {
            
            NavigationLink(destination: ApiSingleView()) {
                Text("Api Single Call").font(.headline)
            }
            NavigationLink(destination: ApiSequentialView(ab: 1)) {
                Text("Api Sequential Call").font(.headline)
            }
        }.navigationBarTitle("Sample")
    }
    
}

struct ModelView_Previews: PreviewProvider {
    static var previews: some View {
        ModelView()
    }
}
