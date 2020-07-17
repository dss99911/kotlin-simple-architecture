//
//  CommonStateView.swift
//  kotlinIOS
//
//  Created by hyun kim on 11/07/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import KotlinApi

struct StatusView<Content> : View where Content : View {
    @ObservedObject private var wrapper: ViewModelWrapper
    @State var initialized = false
    
    /// Screen refers to CommonStatusView. if use generic View. then the viewModel also should know which View is root view on Screen. so decided to use AnyView
    let content: () -> Content
    let viewModel: Kotlin_simple_architectureBaseViewModelIos
    
    init(viewModel: Kotlin_simple_architectureBaseViewModelIos, @ViewBuilder content: @escaping () -> Content) {
        self.viewModel = viewModel
        self.content = content
        self.wrapper = ViewModelWrapper(viewModel: viewModel)
    }
    
    var body: some View {
        
        ZStack {
            //this is just for reloading view
            if (wrapper.empty != "") {
                Text(wrapper.empty)
            }
            
            if (wrapper.isInitLoading()) {
                Text("full Loading")
            } else if (wrapper.isInitError()) {
                Text("full Error")
            } else {
                self.content()
                
                if (wrapper.isLoading()) {
                    Text("Loading")
                } else if (wrapper.isError()) {
                    Text("Error")
                }
            }
        }.onAppear {
            if (!self.initialized) {
                self.initialized = true
                self.wrapper.onInitialized()
            }
        }
    }
}

class ViewModelWrapper: ObservableObject {
    @Published var empty = ""
    
    let viewModel: Kotlin_simple_architectureBaseViewModelIos
    
    init(viewModel: Kotlin_simple_architectureBaseViewModelIos) {
        self.viewModel = viewModel
    }
    
    func onInitialized() {
        viewModel.forEachFlow { (flow) in
            flow.watch { (data) in
                self.reloadView()
            }
        }
        viewModel.onInitialized()
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
    
    deinit {
        viewModel.onCleared()
    }
}
