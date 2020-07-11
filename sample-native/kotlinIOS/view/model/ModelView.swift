//
//  Architecture.swift
//  kotlinIOS
//
//  Created by hyun kim on 18/06/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import KotlinApi
struct ModelView: View {
    var body: some View {
        List {
            NavigationLink(destination: ApiSingleView()) {
                Text("Api Single Call").font(.headline)
            }
            NavigationLink(destination: ApiSequentialView()) {
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
