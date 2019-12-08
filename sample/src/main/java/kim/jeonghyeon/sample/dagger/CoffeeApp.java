package kim.jeonghyeon.sample.dagger;

import javax.inject.Singleton;

import dagger.Component;

public class CoffeeApp {
    public static void main(String[] args) {
//    CoffeeShop coffeeShop = DaggerCoffeeApp_CoffeeShop.builder().build();
//    coffeeShop.maker().brew();
    }

    @Singleton
    @Component(modules = {DripCoffeeModule.class})
    public interface CoffeeShop {
        CoffeeMaker maker();
    }
}
