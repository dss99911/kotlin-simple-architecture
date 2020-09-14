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
        ScrollView {
            VStack {
                Button("Api Single Call") {
                    navigator.navigate { ApiSingleScreen() }
                }
                Button("Api Sequential Call") {
                    navigator.navigate { ApiSequentialScreen() }
                }
                Button("Api Parallel Call") {
                    navigator.navigate { ApiParallelScreen() }
                }
                Button("Api Polling") {
                    navigator.navigate { ApiPollingScreen() }
                }
                Button("DB Api Together") {
                    navigator.navigate { ApiDbScreen() }
                }
                Button("Simple DB Call") {
                    navigator.navigate { DbSimpleScreen() }
                }
                Button("Api Annotation Call") {
                    navigator.navigate { ApiAnnotationScreen() }
                }
                Button("Api External Call") {
                    navigator.navigate { ApiExternalScreen() }
                }
                Button("Sign in") {
                    navigator.navigate { SigninScreen() }
                }
            }
        }
    }
}
