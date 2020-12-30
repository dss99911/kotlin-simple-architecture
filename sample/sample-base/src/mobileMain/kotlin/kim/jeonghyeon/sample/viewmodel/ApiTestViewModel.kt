package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.sample.api.EnumValue
import kim.jeonghyeon.sample.api.GenericSubType
import kim.jeonghyeon.sample.api.GenericType
import kim.jeonghyeon.sample.api.TestApi
import kim.jeonghyeon.sample.di.serviceLocator


class ApiTestViewModel(private val api: TestApi = serviceLocator.testApi) : ModelViewModel() {
    override val title: String = "Api Test"

    override fun onInit() {
        initStatus.load {
            val text = api.checkNull("aa")
            check(text == "aa")

            val textNull = api.checkNull(null)
            check(textNull == null)

            val checkEnum = api.checkEnum(EnumValue.A)
            check(checkEnum == EnumValue.A)

            val enumNull = api.checkEnum(null)
            check(enumNull == null)

            val genericInput = GenericType(EnumValue.A, GenericSubType(EnumValue.A))
            val genericResult = api.checkGeneric(
                genericInput
            )
            check(genericResult == genericInput)

            val genericNull = api.checkGeneric(null)
            check(genericNull == null)

            api.checkAnnotation("a", "q", genericInput, "head")

            api.checkAnnotation(null, null, null, null)

            api.checkEmptyFunction()
        }
    }
}