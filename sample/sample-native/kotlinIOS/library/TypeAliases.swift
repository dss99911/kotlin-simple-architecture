//
//  TypeAliases.swift
//  kotlinIOS
//
//  Created by hyun kim on 26/12/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import sample_base

let uiManager = Kotlin_simple_architecture_clientUiManager()

let navigator = uiManager.navigator
let deeplinkNavigator = uiManager.deeplinkNavigator

typealias BaseViewModel = Kotlin_simple_architecture_clientBaseViewModel
typealias ResourceError = Kotlin_simple_api_clientResourceError
typealias ViewModelScope = Kotlin_simple_architecture_clientViewModelScope
typealias ViewModelFlow = Kotlin_simple_architecture_clientViewModelFlow
typealias Deeplink = Kotlin_simple_architecture_clientDeeplink
