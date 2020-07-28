//
//  Architecture.swift
//  kotlinIOS
//
//  Created by hyun kim on 18/06/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import KotlinApi

struct ViewScreen: NavigationScreen {
    
    var title: String = "View".localized()
    @State var model = EmptyViewModelIos()
    
    var content: some View {
        Text("View")
    }
}

struct ViewView_Previews: PreviewProvider {
    static var previews: some View {
        ViewScreen()
    }
}