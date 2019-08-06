package com.opentadam.ui.registration.mvp.presenter

import android.content.Intent
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.opentadam.App
import com.opentadam.Constants
import com.opentadam.Injector
import com.opentadam.R
import com.opentadam.data.Countries
import com.opentadam.network.IGetApiResponse
import com.opentadam.network.rest.Country
import com.opentadam.network.rest.SubmitRequest
import com.opentadam.ui.registration.mvp.view.V2RegistrationView
import java.util.*

@InjectViewState
class V2RegistrationPresenter : MvpPresenter<V2RegistrationView>() {

    private lateinit var titleDef: String
    private var mask: String? = null

    // TODO: pass application context via Dagger
    fun onSelectUserAgreement() {
        openWebpage(App.app.hashBC.URL_USER_AGREEMENT)
    }

    fun onSelectPolicyPrivacy() {
        openWebpage(App.app.hashBC.URL_POLICY_PRIVACY)
    }

    fun onSendPhone(regPhone: String?, regPrefixPhone: String?, isFragmentVisible: Boolean) {
        viewState.hideKeyboard()
        sendPhone(regPhone, regPrefixPhone, isFragmentVisible)

    }

    fun initUI() {

        Countries.getRegCountries(getCountriesIso()).let {iso ->
            mask = Countries.getCountryPhoneMask(iso)
            mask?.let {
                val hintMask = it.substring(0, it.length - Countries.ADD_MASK.length)
                viewState.initUI(getCountrlyFlag(iso), getCountryPhonePrefix(iso), hintMask)
            }
        }
    }

    private fun openWebpage(url: String?) {

        url?.let {
            val address = Uri.parse(it)
            val openlinkIntent = Intent(Intent.ACTION_VIEW, address)

            App.app?.run {
                startActivity(Intent
                        .createChooser(openlinkIntent, getString(R.string.select_app)))
            }
        }
    }

    private fun getCountriesIso(): Array<String?> {
        val countryList: List<Country> = Injector.getCountryList() ?: ArrayList()

        val size = countryList.size

        val lst = arrayOfNulls<String>(size)
            for (i in 0 until size) {
                val country = countryList[i]
                lst[i] = country.isoCode
            }

        return lst

    }

    fun getCountrlyFlag(iso: String?) = Countries.getCountryFlag(iso)

    fun getCountryName(iso: String?) = Countries.getCountryName(iso)

    fun getCountryPhonePrefix(iso: String?) = Countries.getCountryPhonePrefix(iso)
    fun onCountryItemSelected(iso: String?) {
        Injector.getSettingsStore().writeString("countries", iso)
    }

    fun isPhoneNotCorrect(regPhone: String?, mask: String?) =
            (regPhone == null
                    || mask == null
                    || (Countries.MASK_DEFAULT != mask && regPhone.length < mask.length - Countries.ADD_MASK.length))

    private fun sendPhone(regPhone: String?, regPrefixPhone: String?, isFragmentVisible: Boolean) {
        if (isPhoneNotCorrect(regPhone, mask) || regPrefixPhone == null) {
            viewState.showAlertCheckNumbers()
            return
        }
            viewState.showProgress()

            val phoneUser = regPhone?.trim { it <= ' ' }

            val phone = regPrefixPhone + phoneUser?.replace("\\D".toRegex(), "")
            val value = phone.replace(" ", "")
            val restConnect = Injector.getRC()

            val submitRequest = SubmitRequest(value)
            val rfererrClient = Injector.getSettingsStore().refererrClient
            rfererrClient.let {
                submitRequest.referralCode = it
            }

            restConnect.sendPhoneToServers(submitRequest, IGetApiResponse { apiResponse ->
                if (!isFragmentVisible)
                    return@IGetApiResponse

                apiResponse.error?.let {
                    viewState.showAlertInvalidNumber()
                    return@IGetApiResponse
                }

                if (Constants.PATH_REG_PHONE == apiResponse.path) {
                    val ph = regPrefixPhone + phoneUser?.replace("\\D".toRegex(), "")
                    val vl = ph.replace(" ", "")
                    val submitted = apiResponse.submitted
                    val id = submitted.id
                    viewState.showPrefixSmsCode(regPrefixPhone, phoneUser!!, id, vl)
                }
            })
    }

    fun onSelectCountry() {

        if (Injector.getCountryList() == null)
            return

        viewState.setV2KeyNumberVisibility(true)

        viewState.showCountryList(getCountriesIso())
    }

    fun replaceMask(): String? {
        mask = mask?.replace("X", "#")
        return mask
    }

    fun setTitleDef(tDef: String) {
        this.titleDef = tDef
    }

    fun getTitleDef() = titleDef
}
