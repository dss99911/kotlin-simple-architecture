//
//  ViewModelWrapper.swift
//  kotlinIOS
//
//  Created by hyun kim on 12/09/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

public class ViewModelWrapper: ObservableObject {
    @Published var appearCount = 0
    
    var navigation: (() -> AnyView)? = nil
    var navigationedViewModel: BaseViewModel? = nil
    @Published var showNavigation = false
    
    // this is used for sending deeplink to root view
    @Published var deeplinkUrl: URL? = nil
    
    @Published var dismiss = false
    
    let viewModel: Kotlin_simple_architectureBaseViewModel
    
    init(viewModel: Kotlin_simple_architectureBaseViewModel) {
        self.viewModel = viewModel
    }
    
    func isInitialized() -> Bool {
        return viewModel.initialized
    }
    
    func onAppear() {
        if (!viewModel.initialized) {
            viewModel.watchChanges {
                self.reloadView()
            }
            viewModel.eventGoBack.watch(scope: viewModel.scope) { data in
                if (data != nil) {
                    self.dismiss = true
                }
            }
        } else {
            //when this is changed, view is redrawn.
            //on initialized, it's drawn, so, no need to draw again.
            reloadView()
        }
        viewModel.onCompose()
    }
    
    func reloadView() {
        objectWillChange.send()
    }
    
    func isInitLoading() -> Bool {
        return (viewModel.initStatus.value)?.isLoading() ?? false
    }
    
    func isLoading() -> Bool {
        return (viewModel.status.value)?.isLoading() ?? false
    }
    
    func isInitError() -> Bool {
        return (viewModel.initStatus.value)?.isError() ?? false
    }
    
    func isError() -> Bool {
        
        return (viewModel.status.value)?.isError() ?? false
    }
    
    func retryOnError() {
        (viewModel.status.value)?.onError(onResult: { (error, last, retry) in
            retry()
        })
    }
    
    func retryOnInitError() {
        (viewModel.initStatus.value)?.onError(onResult: { (error, last, retry) in
            retry()
        })
    }
    
    deinit {
        viewModel.onCleared()
    }
}
