//
//  File.swift
//  kotlinIOS
//
//  Created by hyun kim on 06/09/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import Combine
import sample_base

//  Base approach
//    1. If the screen to be navigated is currently on top. then just deliver deeplink without navigation.
//    2. Otherwise, just add new Screen on the navigation stack.
//    3. If need to go to root Screen, use navigator.navigateToRootView(URL)
//  Usage
//    Inherit this class. and override `navigateToDeeplink` function
//    and overridden function uses `navigate` function for the approach 1,2 or `navigator.navigateToRootView(URL)` for the approach 3
public class Deeplinker {
    func navigateToDeeplink<SCREEN>(
        data: DeeplinkData<SCREEN>
    ) -> Bool where SCREEN : Screen {
        //override and let deeplink to navigate to screen with [navigate] function
        true
    }
    
    // use this function
    // when screen not exists, open web browser
    func navigateToDeeplinkOrLink<SCREEN>(
        data: DeeplinkData<SCREEN>
    ) where SCREEN : Screen {
        if (!navigateToDeeplink(data: data)) {
            //open webbrowser
            data.navigator.openUrl(url: data.url)
        }
    }
    
    // If the Screen to be navigated is the current Screen. then just deliver deeplink without navigation.
    final func navigate<T, U>(
        to screen: @autoclosure @escaping () -> T,
        data: DeeplinkData<U>
    ) where T : Screen, U : Screen {
        if (type(of: data.currentScreen) == T.self) {
            data.currentScreen.onDeeplinkReceived(url: data.url)
            return
        }
        
        let screenView = screen()//create here as `NavigationLink` calls destination several times.
        screenView.onDeeplinkReceived(url: data.url)
        
        if (screenView.isRoot) {
            data.navigator.navigateToRootView(deeplinkUrl: data.url)
        } else {
            if (data.resultListener == nil) {
                data.navigator.navigate(to: screenView)
            } else {
                data.navigator.navigate(
                    to: { screenView },
                    onResult: { result in
                        data.resultListener!.onDeeplinkResult(result: result)
                    }
                )
            }
        }
    }
}

struct DeeplinkData<SCREEN : Screen> {
    let navigator: Navigator
    let url: URL
    let resultListener: Kotlin_simple_architectureDeeplinkResultListener?
    let currentScreen: SCREEN
}
