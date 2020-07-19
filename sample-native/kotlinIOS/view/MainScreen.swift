//
//  MainView.swift
//  kotlinIOS
//
//  Created by hyun kim on 12/04/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import KotlinApi
import SwiftUI

struct MainScreen: View {
    var body: some View {
        /// can't use NavigationView outside of TabView. crash occurs. reason: 'Tried to pop to a view controller that doesn't exist.'
        return TabView {
                ModelScreen()
                        .tabItem {
                        Image(systemName: "doc.text.fill")
                        Text("Model")
                    }

                ViewScreen().tabItem {
                    Image(systemName: "rectangle.split.3x3.fill")
                    Text("View")
                }
        }
    }
}

class ContentView_Previews: PreviewProvider {
    static var previews: some View {
        MainScreen()
    }

//    #if DEBUG
//    @objc class func injected() {
//        UIApplication.shared.windows.first?.rootViewController =
//                UIHostingController(rootView: MainView())
//    }
//    #endif
}
