//
// Created by hyun kim on 08/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

protocol Screen : View {
    associatedtype Content : View
    associatedtype LoadingView : View
    associatedtype ErrorView : View
    associatedtype ViewModel : Kotlin_simple_architectureBaseViewModel
    
    // Set ViewModel
    var model: ViewModel { get }
    
    // Required for `Navigator` feature
    var isRoot: Bool { get }
    
    // Add View here instead of `body`
    func content(navigator: Navigator) -> Content
    
    // Handle deeplink by override this function. if it's not overridden then, it automatically deliver to ViewModel.
    func onDeeplinkReceived(url: URL)
    
    // override this and define deeplink logic
    var deeplinker: Deeplinker { get }
    
    // Customize loading view by override this function
    var loadingView: Self.LoadingView { get }
    
    // Customize error view view by override this function
    func errorView(error: Kotlin_simple_architectureResourceError, retry: @escaping () -> Void) -> Self.ErrorView
}

extension Screen {
    var isRoot: Bool {
        false
    }
    
    var body: some View {
        SimpleLayout(viewModel: model, isRootView: isRoot, screen: self) { navigator in
            self.content(navigator: navigator)
        }
    }
    
    var loadingView: some View {
        Text("full Loading")
    }
    
    func errorView(error: Kotlin_simple_architectureResourceError, retry: @escaping () -> Void) -> some View {
        Button(action: { retry() }, label: { Text("full Error") })
    }
    
    var deeplinker: Deeplinker {
        Deeplinker()
    }

    func onDeeplinkReceived(url: URL) {
        model.onDeeplinkReceived(url: IosUtil().convertUrl(url: url))
    }
    
    var model: Kotlin_simple_architectureBaseViewModel {
        BaseViewModel()
    }

}

// NavigationScreen include NavigationView on Screen.
// because Screen doesn't contains NavigationView, so, if want to add NavigationView. it should be inside of `SimpleLayout`
// but, if NavigationView is inside of `SimpleLayout`, `SimpleLayout`'s onAppear is not working(onAppear() should be inside of NavigationView), then Screen is not drawn and NavigationLink's View is not created again. so, sub view's initialization is not working properly
// so, If need NavigationView, use this
protocol NavigationScreen : Screen {
    
}

extension NavigationScreen {
    var body: some View {
        NavigationView {
            SimpleLayout(viewModel: model, isRootView: isRoot, screen: self) { navigator in
                self.content(navigator: navigator)
            }
        }
        
    }
}

struct NavigationLazyView<Content: View>: View {
    let build: () -> Content
    init(_ build: @autoclosure @escaping () -> Content) {
        self.build = build
    }
    init(_ build: @escaping () -> Content) {
        self.build = build
    }
    var body: Content {
        build()
    }
}
