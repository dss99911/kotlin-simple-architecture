//
//  Architecture.swift
//  kotlinIOS
//
//  Created by hyun kim on 18/06/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import sample_base
struct ModelScreen: NavigationScreen {
    var title = "Model".localized()
    @State var model = BaseViewModel()
    
    var content: some View {
        List {
        NavigationLink(destination:NavigationLazyView(ApiSingleScreen())) {
                Text("Api Single Call").font(.headline)
            }
            NavigationLink(destination: NavigationLazyView(ApiSequentialScreen())) {
                Text("Api Sequential Call").font(.headline)
            }
            NavigationLink(destination: NavigationLazyView(ApiParallelScreen())) {
                Text("Api Parallel Call").font(.headline)
            }
            NavigationLink(destination: NavigationLazyView(ApiPollingScreen())) {
                Text("Api Polling").font(.headline)
            }
            NavigationLink(destination: NavigationLazyView(ApiDbScreen())) {
                Text("DB Api Together").font(.headline)
            }
            NavigationLink(destination: NavigationLazyView(DbSimpleScreen())) {
                Text("Simple DB Call").font(.headline)
            }
            NavigationLink(destination: NavigationLazyView(ApiAnnotationScreen())) {
                Text("Api Annotation Call").font(.headline)
            }
            NavigationLink(destination: NavigationLazyView(ApiExternalScreen())) {
                Text("Api External Call").font(.headline)
            }
        }
    }
    
}

struct ModelView_Previews: PreviewProvider {
    static var previews: some View {
        ModelScreen()
    }
}
