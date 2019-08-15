package com.opentadam.ui.registration.mvp.view

import com.arellomobile.mvp.MvpView

interface V2SmsCodeView: MvpView {
    fun refreshOnSendingToServers()

    fun refreshOnNoBlockSms()

    fun showMessageWrongSmsCode()

    fun showMessageWrongConfirmationCode()

    fun showMessageCallRequestSent()

    fun showMessageSmsRequestSent()

    fun refreshedToken()

    fun onConfirmed()

    fun hideKeyboard()
}