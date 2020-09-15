//
//  URLEx.swift
//  kotlinIOS
//
//  Created by hyun kim on 09/09/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation

extension URL {
    
    func param(key: String) -> String? {
        guard let query = query else { return nil }
        
        let components = query.split(separator: ",").flatMap { $0.split(separator: "=") }
        guard let keyIndex = components.firstIndex(of: Substring(key)) else { return nil }
        guard keyIndex + 1 < components.count else { return nil }
        return String(components[keyIndex + 1])
    }
}
