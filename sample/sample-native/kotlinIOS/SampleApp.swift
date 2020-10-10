import SwiftUI
import sample_base
@main
struct SampleApp: App {
    var body: some Scene { 
        return WindowGroup {
            HomeScreen()
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
}

protocol SampleNavigationScreen : NavigationScreen {

}

extension SampleNavigationScreen {
    var deeplinker: Deeplinker {
        SampleDeeplinker()
    }
}

class SampleDeeplinker : Deeplinker {
    
    // return false if screen not exists.
    //
    // I want to make function getDeeplinkScreen.
    //  but, Screen required generic type for each screen. and have to return Screen type
    //  so, I couldn't find the way. and used this way
    override func navigateToDeeplink<SCREEN>(
        data: DeeplinkData<SCREEN>
    ) -> Bool where SCREEN : Screen {
        let deeplink = DeeplinkUrl()
        let url = data.url.absoluteString
        if (url.starts(with: deeplink.DEEPLINK_PATH_HOME)) {
            navigate(to: HomeScreen(), data: data)
        } else if (url.starts(with: deeplink.DEEPLINK_PATH_SIGN_IN)) {
            navigate(to: SigninScreen(), data: data)
        } else if (url.starts(with: deeplink.DEEPLINK_PATH_SIGN_UP)) {
            navigate(to: SignUpScreen(), data: data)
        } else if (url.starts(with: deeplink.DEEPLINK_PATH_DEEPLINK_SUB)) {
            navigate(to: DeeplinkSubScreen(), data: data)
        } else {
            return false
        }
        return true
    }
}
