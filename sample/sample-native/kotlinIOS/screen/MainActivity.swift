//
//  MainActivity.swift
//  kotlinIOS
//
//  Created by hyun kim on 25/10/20.
//  Copyright © 2020 hyun kim. All rights reserved.
//

import Foundation
import sample_base
import SwiftUI

func MainActivity() -> some View {
    BaseActivity(rootViewModel: HomeViewModel(), deeplinks: DeeplinkKt.deeplinkList) { viewModel in
        //you can customize to add tab view or drawer. etc..
        Box {
            switch (viewModel) {
            case is HomeViewModel: HomeScreen(viewModel as! HomeViewModel)
            case is ApiSingleViewModel: ApiSingleScreen(viewModel as! ApiSingleViewModel)
            case is ApiAnnotationViewModel: ApiAnnotationScreen(viewModel as! ApiAnnotationViewModel)
            case is ApiBindingViewModel: ApiBindingScreen(viewModel as! ApiBindingViewModel)
            case is ApiDbViewModel: ApiDbScreen(viewModel as! ApiDbViewModel)
            case is ApiExternalViewModel: ApiExternalScreen(viewModel as! ApiExternalViewModel)
            case is ApiHeaderViewModel: ApiHeaderScreen(viewModel as! ApiHeaderViewModel)
            case is ApiParallelViewModel: ApiParallelScreen(viewModel as! ApiParallelViewModel)
            case is ApiPollingViewModel: ApiPollingScreen(viewModel as! ApiPollingViewModel)
            case is ApiSequentialViewModel: ApiSequentialScreen(viewModel as! ApiSequentialViewModel)
            case is DbSimpleViewModel: DbSimpleScreen(viewModel as! DbSimpleViewModel)
            case is DeeplinkViewModel: DeeplinkScreen(viewModel as! DeeplinkViewModel)
            case is DeeplinkSubViewModel: DeeplinkSubScreen(viewModel as! DeeplinkSubViewModel)
            case is SearchViewModel: SearchScreen(viewModel as! SearchViewModel)
            case is SignInViewModel: SignInScreen(viewModel as! SignInViewModel)
            case is SignUpViewModel: SignUpScreen(viewModel as! SignUpViewModel)
            case is UserViewModel: UserScreen(viewModel as! UserViewModel)
            default: EmptyView()
            }
        }
    }
}
