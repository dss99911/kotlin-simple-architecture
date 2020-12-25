//
//  ContentView.swift
//  ios
//
//  Created by hyun kim on 06/10/20.
//

import SwiftUI
import base

func HomeScreen(_ model: HomeViewModel) -> some View {
    Screen(model) {
        Text("\(HelloIosKt.helloText) \(+model.world ?? "")")
    }
}
