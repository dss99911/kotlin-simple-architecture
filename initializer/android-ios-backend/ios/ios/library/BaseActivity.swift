//
//  MainScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 24/10/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import base


struct BaseActivity<Content>: View where Content : View {

    let makeScreen: (BaseViewModel) -> Content
    
    @ObservedObject private var baseActivityViewModel: BaseActivityViewModel
    
    init(rootViewModel: BaseViewModel, deeplinks: [Deeplink], makeScreen: @escaping (BaseViewModel) -> Content) {
        self.baseActivityViewModel = BaseActivityViewModel(_rootViewModel: rootViewModel, deepLinks: deeplinks)
        self.makeScreen = makeScreen
    }
    
    var body: some View {
        NavigationView {
            if let viewModel = baseActivityViewModel.currentViewModel {
                if (navigator.root == viewModel) {
                    makeScreen(viewModel)
                        .navigationTitle(viewModel.title)
                    
                } else {
                    makeScreen(viewModel)
                        .navigationTitle(viewModel.title)
                        .navigationBarItems(leading: Button(action : { navigator.backUpTo(viewModel: viewModel, inclusive: true) }){
                            //TODO: A->B->C. after C, when go back to B, B's back arrow's left margin increased.
                            // I don't know the exact reason. and looks minor and also can fix with custom navigationBarTitle and back button.
                            // as this library doesn't use navigation. NavigationView itself seems not much required.
                            Row {
                                Image(systemName: "arrow.left")
                                
                                Text(navigator.previous?.title ?? "")
                            }
                            
                        })
                }
            }
                
        }
        .onOpenURL { url in
            deeplinkNavigator.navigateToDeeplinkFromExternal(url: url.absoluteString)
        }
    }
}

class BaseActivityViewModel: ObservableObject {
    @Published var currentViewModel : BaseViewModel? = nil
    
    init(_rootViewModel: BaseViewModel, deepLinks: [Deeplink]) {
        deeplinkNavigator.deeplinks = deepLinks
        uiManager.initialize(app: UIApplication.shared)
        navigator.navigate(viewModel: _rootViewModel)
        navigator.watchCurrent(scope: uiManager.globalScope) { viewModel in
            self.currentViewModel = viewModel
        }
    }
}




