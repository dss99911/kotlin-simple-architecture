//
//  MainView.swift
//  kotlinIOS
//
//  Created by hyun kim on 12/04/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import sample_base
import SwiftUI

struct HomeScreen: SampleNavigationScreen {
    let PARAM_TAB = "tab"
    
    @State var activeTab: TabIdentifier = .model
    
    var isRoot: Bool = true
    
    func content(navigator: Navigator) -> some View {
        TabView(selection: $activeTab) {
            ModelScreen()
                .tabItem {
                    Image(systemName: "doc.text.fill")
                    Text("Model")
                }
                .tag(TabIdentifier.model)
            
            ViewScreen()
                .tabItem {
                    Image(systemName: "rectangle.split.3x3.fill")
                    Text("View")
                }
                .tag(TabIdentifier.view)
        }
        .navigationTitle("Home".localized())
    }

    func onDeeplinkReceived(url: URL) {
        print("main deeplink received \(url)")
        guard let tabString = url.param(key: PARAM_TAB) else { return }
        guard let tab = TabIdentifier(rawValue: tabString) else { return }
        activeTab = tab
    }
}

enum TabIdentifier: String {
    case model, view
}

class MainScreen_Previews: PreviewProvider {
    static var previews: some View {
        //preview is not working https://hyun.myjetbrains.com/youtrack/issue/KSA-134
        HomeScreen()
    }
    
    //    #if DEBUG
    //    @objc class func injected() {
    //        UIApplication.shared.windows.first?.rootViewController =
    //                UIHostingController(rootView: MainView())
    //    }
    //    #endif
}
