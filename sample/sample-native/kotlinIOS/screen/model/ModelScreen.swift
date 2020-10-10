//
//  Architecture.swift
//  kotlinIOS
//
//  Created by hyun kim on 18/06/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import sample_base
struct ModelScreen: SampleScreen {
    func content(navigator: Navigator) -> some View {
        ScrollableColumn {
            //in a VStack, limited children is available.
            //so, split childrent with 2 column
            Column {
                Button("Api Single Call".localized()) {
                    navigator.navigate { ApiSingleScreen() }
                }
                Button("Api Sequential Call".localized()) {
                    navigator.navigate { ApiSequentialScreen() }
                }
                Button("Api Parallel Call".localized()) {
                    navigator.navigate { ApiParallelScreen() }
                }
                Button("Api Polling".localized()) {
                    navigator.navigate { ApiPollingScreen() }
                }
            }
            Column {
                Button("DB Api Together".localized()) {
                    navigator.navigate { ApiDbScreen() }
                }
                Button("Simple DB Call".localized()) {
                    navigator.navigate { DbSimpleScreen() }
                }
                Button("Api Annotation Call".localized()) {
                    navigator.navigate { ApiAnnotationScreen() }
                }
                Button("Api External Call".localized()) {
                    navigator.navigate { ApiExternalScreen() }
                }
                Button("User".localized()) {
                    navigator.navigate { UserScreen() }
                }
                Button("Api Binding".localized()) {
                    navigator.navigate { ApiBindingScreen() }
                }
                Button("Deeplink".localized()) {
                    navigator.navigate { DeeplinkScreen() }
                }
                Button("Reactive".localized()) {
                    navigator.navigate { ReactiveScreen() }
                }
            }
            
        }
    }
}

struct ModelScreen_Previews: PreviewProvider {
    static var previews: some View {
        /*@START_MENU_TOKEN@*/Text("Hello, World!")/*@END_MENU_TOKEN@*/
    }
}
