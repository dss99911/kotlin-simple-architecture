import SwiftUI
import sample_base
@main
struct SampleApp: App {
    var body: some Scene {
        ApplicationKt.initialize(app: UIApplication.shared)
        
        return WindowGroup {
            MainScreen()
        }
    }
}

protocol SampleScreen : Screen {

}

extension SampleScreen {
    var deeplinker: Deeplinker {
        SampleDeeplinker()
    }
}

protocol SampleNavigationScreen : NavigationScreen {

}

extension SampleNavigationScreen {
    var deeplinker: Deeplinker {
        SampleDeeplinker()
    }
}

class SampleDeeplinker : Deeplinker {    
    override func navigateToDeeplink<SCREEN>(navigator: Navigator, url: URL, currentScreen: SCREEN) where SCREEN : Screen {
        switch url.path {
        case "/main":
            navigator.navigateToRootView(deeplinkUrl: url)
        case "/signIn":
            navigate(to: SigninScreen(), url: url, navigator: navigator, currentScreen: currentScreen)
        case "/signUp":
            navigate(to: SignUpScreen(), url: url, navigator: navigator, currentScreen: currentScreen)
        default:
            navigator.navigateToRootView(deeplinkUrl: url)
        }
    }
}
