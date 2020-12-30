
import Foundation
import base
import SwiftUI

func MainActivity() -> some View {
    BaseActivity(rootViewModel: HomeViewModel(), deeplinks: []) { viewModel in
        //you can customize to add tab view or drawer. etc..
        Box {
            switch (viewModel) {
            case is HomeViewModel: HomeScreen(viewModel as! HomeViewModel)
            default: EmptyView()
            }
        }
    }
}
