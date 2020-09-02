//
//  CommonStateView.swift
//  kotlinIOS
//
//  Created by hyun kim on 11/07/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

typealias BaseViewModel = Kotlin_simple_architectureBaseViewModel

struct StatusView<Content> : View where Content : View {
    @ObservedObject private var wrapper: ViewModelWrapper
    
    /// Screen refers to CommonStatusView. if use generic View. then the viewModel also should know which View is root view on Screen. so decided to use AnyView
    let content: () -> Content
    let viewModel: Kotlin_simple_architectureBaseViewModel
    
    init(viewModel: Kotlin_simple_architectureBaseViewModel, @ViewBuilder content: @escaping () -> Content) {
        self.viewModel = viewModel
        self.content = content
        self.wrapper = ViewModelWrapper(viewModel: viewModel)
    }
    
    var body: some View {

        ZStack {
            
            if (wrapper.appearCount < 0) {
                //this is just for redrawing view on appeared.
                //in case NavigationLink. if this is not redrawn, destination view is not initialized again.
            }
            
            if (wrapper.isInitLoading()) {
                Text("full Loading")
            } else if (wrapper.isInitError()) {
                Button(action: { self.wrapper.retryOnInitError() }, label: { Text("full Error") })
            } else {
                self.content()
                
                if (wrapper.isLoading()) {
                    Text("Loading")
                } else if (wrapper.isError()) {
                    Button(action: { self.wrapper.retryOnError() }, label: { Text("Error") })
                }
            }
        }
        .onAppear {
            self.wrapper.onAppear()
        }
    }
}

class ViewModelWrapper: ObservableObject {
    @Published var appearCount = 0
    
    let viewModel: Kotlin_simple_architectureBaseViewModel
    
    init(viewModel: Kotlin_simple_architectureBaseViewModel) {
        self.viewModel = viewModel
    }
    

    
    func onAppear() {
        if (!viewModel.initialized) {
            viewModel.watchChanges {
                self.reloadView()
            }
        } else {
            //when this is changed, view is redrawn.
            //on initialized, it's drawn, so, no need to draw again.
            appearCount += 1
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
