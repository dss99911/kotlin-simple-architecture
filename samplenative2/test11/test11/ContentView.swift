//
//  ContentView.swift
//  test11
//
//  Created by hyun kim on 05/07/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import SwiftUI
import KotlinApi
struct ContentView: View {
    
    @State private var value: String? = ""
    
    
    var body: some View {
        VStack {
            Button(action: {
                PreferenceApiIos().getString(key: "d").watch { (ss) in
                    if (ss!.isError()) {
                        self.value = "error"
                    } else {
                        if (ss!.isLoading()) {
                            self.value = "loading"
                        }
                        if (ss!.isSuccess()){
                            self.value = ss!.data() as String?
                        }
                        
                    }
                
                }
            }) {
                Text("Hello, World! \(value ?? "")")
            }
            
            Button(action: {
                PreferenceApiIos().setString(key: "d", value: "bena").watch { (resource) in
                    
                    
                }
            }) {
                Text("chnage")
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
