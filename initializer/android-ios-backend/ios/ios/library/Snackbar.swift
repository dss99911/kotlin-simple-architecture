//
//  Snackbar.swift
//  kotlinIOS
//
//  Created by hyun kim on 11/10/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI

struct Snackbar : View {
    
    let message: String
    let buttonText: String
    let buttonAction: () -> Void
    
    var body: some View {
        VStack {
            Spacer()
            HStack(alignment: .bottom, spacing: 20) {
                
                Text(message)
                    .font(Font.system(size: 15, weight: Font.Weight.light, design: Font.Design.default))
                    .foregroundColor(.white)
                Spacer()
                Button(buttonText) {
                    buttonAction()
                }.foregroundColor(.orange)
                .font(Font.system(size: 15, weight: Font.Weight.medium, design: Font.Design.default))
            }
            .padding(EdgeInsets(top: 10, leading: 20, bottom: 10, trailing: 20))
            
            .background(Color.black)
            .cornerRadius(8)
            .shadow(radius: 20)
        }.padding(20)
        
    }
}
