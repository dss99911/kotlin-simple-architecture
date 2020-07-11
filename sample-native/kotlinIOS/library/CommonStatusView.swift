//
//  CommonStateView.swift
//  kotlinIOS
//
//  Created by hyun kim on 11/07/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import SwiftUI
import KotlinApi

struct CommonStatusView : View {
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>

    @State private var _status: Resource<AnyObject> = Resource<AnyObject>.createStart()
    @State private var _initStatus: Resource<AnyObject> = Resource<AnyObject>.createStart()
    
    @State private var isInitialized = false
    
    let onAppeared: (CommonStatusView, Bool) -> Void
    
    private var closableList = [Ktor_ioCloseable]()
    
    mutating func watch<T> (_ flow: CRFlow<T>, _ onResource: @escaping (Resource<T>) -> Void) {
        print("CommonStatusView.watch")
        let closable = flow.watch(block: onResource)
        closableList.append(closable)
    }
    
    private var content: (CommonStatusView) -> AnyView
    
    var body: some View {
        ZStack {
            if (_initStatus.isLoading()) {
                Text("full Loading")
            } else if (_initStatus.isError()) {
                Text("full Error")
            } else {
                self.content(self)
                
                if (_status.isLoading()) {
                    Text("Loading")
                } else if (_status.isError()) {
                    Text("Error")
                }
            }
        }.onAppear {
            print("onAppear")
            
            self.onAppeared(self, self.isInitialized)
            self.isInitialized.toggle()
        }.onDisappear {
            print("disappear")
            if (!self.presentationMode.wrappedValue.isPresented) {
                print("not presented")
                for closable in self.closableList {
                    closable.close()
                }
            }
        }
    }
    
    @inlinable public init<V>(onAppeared: @escaping (CommonStatusView, Bool) -> Void, @ViewBuilder content: @escaping (CommonStatusView) -> V) where V : View {
//        self._status2 = State<Resource<AnyObject>>.init(initialValue: Resource<AnyObject>.createStart())
        self.content = { view in
            AnyView(content(view))
        }
        self.status = __status.projectedValue
        self.initStatus = __initStatus.projectedValue
        self.onAppeared = onAppeared
    }

    var status: Status
    var initStatus: Status

    func setState(_ state: Resource<AnyObject>) {
        _status = state
    }
    func setInitState(_ state: Resource<AnyObject>) {
        _initStatus = state
    }

//    @inlinable func setOnInitialized(a: @escaping (CommonStatusView) -> Void) -> CommonStatusView {
//        self.onInitialized = a
//        return self
//    }
    
}
