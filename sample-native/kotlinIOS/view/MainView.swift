//
//  MainView.swift
//  kotlinIOS
//
//  Created by hyun kim on 12/04/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import KotlinApi
import SwiftUI

struct MainView: View {
    @State var isDrawerOpen: Bool = false
    var body: some View {
        
        HomeView()
    }
//    var body: some View {
//        NavigationView {
//            /// Navigation Bar Title part
//            ZStack {
//                HomeView()
//                        .navigationBarTitle(Text("Navigation Drawer"))
//                        .navigationBarItems(leading: Button(action: {
//                            DispatchQueue.main.asyncAfter(deadline: .now()) {
//                                self.isDrawerOpen.toggle()
//                            }
//                        }) {
//                            Image(systemName: "sidebar.left")
//                        })
//
//                /// Navigation Drawer part
//                NavigationDrawer(isOpen: self.isDrawerOpen)
//                /// Other behaviors
//            }
//
//        }
//    }
}

class ContentView_Previews: PreviewProvider {
    static var previews: some View {
        MainView()
    }

//    #if DEBUG
//    @objc class func injected() {
//        UIApplication.shared.windows.first?.rootViewController =
//                UIHostingController(rootView: MainView())
//    }
//    #endif
}
