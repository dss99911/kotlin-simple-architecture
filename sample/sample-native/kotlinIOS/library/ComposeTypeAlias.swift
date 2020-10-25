//
//  ComposeTypeAlias.swift
//  kotlinIOS
//
//  Created by hyun kim on 02/10/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI

typealias Column = VStack
typealias Row = HStack
typealias Box = ZStack

func ScrollableColumn<Content>(alignment: HorizontalAlignment = .center, spacing: CGFloat? = nil, @ViewBuilder content: () -> Content) -> some View  where Content : View {
    ScrollView {
        VStack(alignment: alignment, spacing: spacing, content: content)
    }
}
