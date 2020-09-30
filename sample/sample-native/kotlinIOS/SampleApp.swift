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
    //TODO: how to change the type of model as SampleViewModel ?
}

extension SampleScreen {
    
    var model: Kotlin_simple_architectureBaseViewModel {
        SampleViewModel()
    }
    
    var deeplinker: Deeplinker {
        SampleDeeplinker()
    }
    
    func onInitialized(navigator: Navigator) {
        guard let sampleViewModel = model as? SampleViewModel else {
            return
        }
        
        sampleViewModel.goSignIn.watch(scope: model.scope) { data in
            if (navigator.isShown()) {
                if (data != nil) {
                    navigator.navigate(to: {
                        SigninScreen()
                    }, onResult: { result in
                        data?.onSignInResult(result: result)
                    })
                }
            }
        }
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
