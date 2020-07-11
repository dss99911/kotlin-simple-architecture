//
//  Resource.swift
//  kotlinIOS
//
//  Created by hyun kim on 10/07/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import KotlinApi
import SwiftUI



typealias Resource = Kotlin_simple_architectureResource
typealias ResourceState<T : AnyObject> = Binding<Resource<T>>

extension Resource {
    @objc class func createStart() -> Resource<AnyObject> {
        return ResourceExKt.createStart()
    }
}

typealias Status = Binding<Resource<AnyObject>>

typealias CRFlow = Kotlin_simple_architectureCRFlow
