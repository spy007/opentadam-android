package com.opentadam.ui.registration.mvp.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface V2RegistrationView : MvpView {

    fun showAlertCheckNumbers()

    fun showCountryList(countryIsoList: Array<String?>)

    fun initUI(countryFlag: Int, countryPhonePrefix: String, mask: String)

    fun hideKeyboard()

    fun showProgress()

    fun showAlertInvalidNumber()

    fun showPrefixSmsCode(regPrefixPhone: String, phoneUser: String, id: Long, value: String)

    fun setV2KeyNumberVisibility(visible: Boolean)

//    fun onSelectUserAgreement()
//
//    fun onSelectPolicyPrivacy()
//
//    fun onSelectNext()
//
//    fun DeletePhone()
//
//    fun onBackPressed()
//
//    fun onDestroyView()
}