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

//  Base approach
//    1. If the screen to be navigated is currently on top. then just deliver deeplink without navigation.
//    2. Otherwise, just add new Screen on the navigation stack.
//    3. If need to go to root Screen, use navigator.navigateToRootView(URL)
//  Usage
//    Inherit this class. and override `navigateToDeeplink` function
//    and overridden function uses `navigate` function for the approach 1,2 or `navigator.navigateToRootView(URL)` for the approach 3
public class Deeplinker {
    func navigateToDeeplink<SCREEN>(navigator: Navigator, url: URL, currentScreen: SCREEN) where SCREEN : Screen {
        
    }
    
    // If the Screen to be navigated is the current Screen. then just deliver deeplink without navigation.
    final func navigate<T, U>(to screen: @autoclosure @escaping () -> T, url: URL, navigator: Navigator, currentScreen: U) where T : Screen, U : Screen {
        if (type(of: currentScreen) == T.self) {
            currentScreen.onDeeplinkReceived(url: url)
            return
        }
        
        navigator.navigate(deeplinkUrl: url) {
            screen()
        }
    }
}
