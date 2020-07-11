//
// Created by hyun kim on 11/07/20.
// Copyright (c) 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI

extension Binding {
    func set(_ value: Value) {
        wrappedValue = value
    }

    func get() -> Value {
        wrappedValue
    }
}