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


// MARK: Navigator Instruction & LIMITATION
//  !!WARNING!! this is not simple to use. and may not be stable. there is instruction below. read carefully and decide to use this or not.
//  TODO: the complexity and instability is caused by the function to navigate to root view. consider if it's really required. or remove from here. it's added for the deeplink which navigate to first Screen.

//  The purpose of this protocol is to navigate easily and handle deeplink easily.
//  The root Screen(first Screen in navigation stack) should use NavigationScreen. and define Screen.isRoot = true
//  If you don't use this functions. you can ignore this instruction.
//  Working cases & Limitation
//    1. A Screen is root Screen and navigates to B Screen => WORKING
//    2. A Screen is root Screen and contains B Screen like TabView. and navigates to other Screen => set A as root Screen. B Screen doesn't receive deeplink. so, deliver deeplink manually to B Screen if required.
//    3. A View navigates to B Screen => Not supported for now. because first View is not disappear by navigation. as B Screen is not first View. it's disappear when B Screen navigates to other Screen. and it causes some state is not stable.
//    4. multiple root Screen => Not supported
// TODO: Support Sheet
protocol Navigator {
    func navigate<Content>(to screen: @autoclosure @escaping () -> Content) where Content: Screen
    
    func navigate<Content>(_ screen: () -> Content) where Content: Screen
    
    func navigate<Content>(to screen: @escaping () -> Content, onResult: @escaping (Kotlin_simple_architectureScreenResult) -> Void) where Content: Screen
    
    // if it's by deeplink, set deeplinkUrl. if not, just set nil
    func navigateToRootView(deeplinkUrl: URL)
    func navigateToRootView()
    func openUrl(url: URL)
  
    func dismiss()
    
    func isShown() -> Bool
    
    func isForeground() -> Bool
}

// MARK: Functions
//  1. connect with ViewModel
//  2. show error, progress ui
//  3. navigation related functions on `Navigator`
//  4. deeplink
struct SimpleLayout<Content, SCREEN> : View, Navigator where Content : View, SCREEN : Screen {
    @ObservedObject private var wrapper: ViewModelWrapper
    @Environment(\.presentationMode) var presentationMode
    @Environment(\.openURL) var _openURL
    
    // All Screen refer root Screen's ViewModelWrapper. in order to navigate to Root View
    @EnvironmentObject var rootWrapper: ViewModelWrapper
    
    let content: (_ navigator: Navigator) -> Content
    let isRootView: Bool
    let screen: SCREEN
    let createdTime: CFAbsoluteTime
    
    init(viewModel: Kotlin_simple_architectureBaseViewModel, isRootView: Bool = false, screen: SCREEN,
         @ViewBuilder content: @escaping (_ navigator: Navigator) -> Content) {
        self.content = content
        self.isRootView = isRootView
        self.screen = screen
        self.wrapper = ViewModelWrapper(viewModel: viewModel)
        self.createdTime = CFAbsoluteTimeGetCurrent()
    }
    
    func navigate<Content>(to screen: @escaping () -> Content) where Content: Screen {
        navigate(to: screen())
    }
    
    func navigate<Content>(to screen: @escaping () -> Content, onResult: @escaping (Kotlin_simple_architectureScreenResult) -> Void) where Content: Screen {
        navigate(to: screen(), onResult: onResult)
    }
    
    func navigate<Content>(_ screen: () -> Content) where Content: Screen {
        navigate(to: screen())
    }
    
    private func navigate<Content>(to screen: Content) where Content: Screen {
        let view = screen.environmentObject(getRootWrapper())
        
        //If root screen's navigation is not shown. it means that current screen is root screen or root screen's inner screen.
        //if it's root's screen's inner screen, navigate by root screen. because, root screen can't recognize if current screen is root screen or other screen if root screen contains inner screen and navigated from the inner screen.
        let baseWrapper = isRootShown() ? getRootWrapper() : wrapper
        baseWrapper.navigation = {AnyView(view)}
        baseWrapper.navigationedViewModel = screen.model
        
        //when redirect to other screen directly, screen goes back to previous screen.
        //so, should wait screen change finished.
        //if this is not good, consider remove animation and try if it's working.
        if ((CFAbsoluteTimeGetCurrent() - createdTime) < 1) {
            delayMain (delayTime: .milliseconds(600)) {
                baseWrapper.showNavigation = true
            }
        } else {
            baseWrapper.showNavigation = true
        }
        
        
    }
    
    private func navigate<Content>(to screen: Content, onResult: @escaping (Kotlin_simple_architectureScreenResult) -> Void) where Content: Screen {
        screen.model.screenResult.watch(scope: wrapper.viewModel.scope) { data in
            if (data != nil) {
                onResult(data!)
            }
        }
        navigate(to: screen)
    }
    
    func openUrl(url: URL) {
        _openURL(url)
    }
    
    func getRootWrapper() -> ViewModelWrapper {
        isRootView ? wrapper : rootWrapper
    }
    
    // Root Screen can contains inner Screen
    // this check if the root is shown.
    func isRootShown() -> Bool {
        !getRootWrapper().showNavigation
    }
    
    func isShown() -> Bool {
        // root view's isPresented is always false
        if (isRootView) {
            return isRootShown()
        } else if(isRootShown()) {
            //even if this screen is not root screen, the meaninng root is shown that this is inner screen of root screen
            //inner screen of root screen always return false.
            //so, ignore deeplink on inner root screen
            return false
        } else {
            return presentationMode.wrappedValue.isPresented
        }
    }
    
    func isForeground() -> Bool {
        UIApplication.shared.applicationState == .active
    }
    
    func navigateToRootView(deeplinkUrl: URL) {
        navigateToRootView()
        getRootWrapper().deeplinkUrl = deeplinkUrl
    }
    
    func navigateToRootView() {
        getRootWrapper().showNavigation = false
    }
    
    func dismiss() {
        presentationMode.wrappedValue.dismiss()
    }
    
    var body: some View {
        //TODO: if this is called on the same time before initialized is set true, there will be malfunction
        if (!self.wrapper.isInitialized()) {
            watchDeeplink()
            screen.onInitialized(navigator: self)
        }
        self.wrapper.onViewDrawn()
        
        return ZStack {
            if (wrapper.isInitLoading()) {
                screen.initLoadingView
            } else if (wrapper.isInitError()) {
                //TODO: errorData can throw error if initStatus is changed to not error
                screen.initErrorView(error: self.wrapper.viewModel.initStatus.value!.error()) {
                    self.wrapper.viewModel.initStatus.value!.retryOnError()
                }
            } else {
                self.content(self)
                
                if (wrapper.isLoading()) {
                    screen.loadingView
                } else if (wrapper.isError()) {
                    //TODO: errorData can throw error if initStatus is changed to not error
                    screen.errorView(error: self.wrapper.viewModel.status.value!.error()) {
                        self.wrapper.viewModel.status.value!.retryOnError()
                    }
                }
            }
            
            // as far as I know, only one navigation link  can be shown at the same time. so used just one NavigationLink
            NavigationLink(destination:NavigationLazyView(wrapper.navigation!()), isActive: $wrapper.showNavigation) {
                EmptyView()
            }
            .isDetailLink(false)
        }
        .onAppear {
            if (isRootView && !isForeground()) {
                //when move to other app from not root view
                //root view's `onAppear` is triggered but foreground is false
                //as root view is actually not shown, so ignore it
                return
            }
            
            wrapper.onAppear()
            
            //TODO: check if onAppear always called after next Screen is closed.
            let baseWrapper = isRootShown() ? getRootWrapper() : wrapper
            if (baseWrapper.navigationedViewModel != nil) {
                baseWrapper.navigationedViewModel?.onBackPressed()
                baseWrapper.navigationedViewModel = nil
            }
        }
        .onOpenURL { url in
            if (!isShown()) {
                return
            }
            
            // call after delay, because if there is several screen, onOpenURL is called one by one. and first onOpenURL navigate to some screen. then isShown() status is changed on other screen. so need delay
            delayMain (delayTime: .milliseconds(200)) {
                let data = DeeplinkData(navigator: self, url: url, resultListener: nil, currentScreen: screen)
                
                //as it's from external. doesn't open web browser
                screen.deeplinker.navigateToDeeplink(data: data)
            }
        }
        .onChange(of: wrapper.deeplinkUrl) { url in
            guard let url = url else { return }
            wrapper.deeplinkUrl = nil
            screen.onDeeplinkReceived(url: url)
        }
        .onChange(of: wrapper.dismiss) { dismissed in
            if (dismissed) {
                dismiss()
            }
            
        }
        .environmentObject(isRootView ? wrapper : rootWrapper)
    }
    
    func watchDeeplink() {
        wrapper.viewModel.eventDeeplink.watch(scope: wrapper.viewModel.scope) { data in
            guard let data = data else { return }
            guard let url = URL(string: data.url) else { return }
            
            let deeplinkData = DeeplinkData(navigator: self, url: url, resultListener: data.resultListener, currentScreen: screen)
            screen.deeplinker.navigateToDeeplinkOrLink(data: deeplinkData)
        }
    }
}
