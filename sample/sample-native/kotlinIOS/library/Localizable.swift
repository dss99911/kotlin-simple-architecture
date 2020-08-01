//
//  Localizable.swift
//  kotlinIOS
//
//  Created by hyun kim on 18/07/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation


extension String {
    func localized() -> String {
        return NSLocalizedString(self, comment: "")
    }
}
