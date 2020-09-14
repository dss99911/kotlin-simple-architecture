//
//  ThreadEx.swift
//  kotlinIOS
//
//  Created by hyun kim on 11/09/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation


func delayMain(delayTime: DispatchTimeInterval, perform: @escaping () -> Void) {
    DispatchQueue.main.asyncAfter(deadline: .now() + delayTime) {
        perform()
    }
}

func asyncMain(perform: @escaping () -> Void) {
    DispatchQueue.main.async {
        perform()
    }
}
