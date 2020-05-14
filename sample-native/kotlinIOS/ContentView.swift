//
//  ContentView.swift
//  kotlinIOS
//
//  Created by hyun kim on 12/04/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//
import KotlinApi
import SwiftUI

struct ContentView: View {
    var body: some View {
        
        Text(IosTest().getName())
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
