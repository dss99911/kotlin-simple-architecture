//
//  ApiPollingScreen.swift
//  kotlinIOS
//
//  Created by hyun kim on 19/07/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import sample_base

func ApiPollingScreen(_ model: ApiPollingViewModel) -> some View {
    Screen(model) {
        Column {
            Text("fail count \(+model.count ?? 0)")
            if ((+model.status)?.isSuccess() == true) {
                Text("result \(+model.result ?? "")")
            }
        }
    }
}
