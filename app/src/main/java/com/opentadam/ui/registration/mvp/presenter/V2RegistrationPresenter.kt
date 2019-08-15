package com.opentadam.ui.registration.mvp.presenter

import android.content.Intent
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.opentadam.App
import com.opentadam.Injector
import com.opentadam.R
import com.opentadam.data.Countries
import com.opentadam.network.rest.Country
import com.opentadam.network.rest.SubmitRequest
import com.opentadam.network.rest.Submitted
import com.opentadam.ui.registration.api.RemoteService
import com.opentadam.ui.registration.mvp.view.V2RegistrationView
import com.opentadam.ui.registration.room.ClientEntity
import com.opentadam.ui.registration.room.RoomDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@InjectViewState
class V2RegistrationPresenter : MvpPresenter<V2RegistrationView> {

    @Inject
    lateinit var apiService: RemoteService

    @Inject
    lateinit var db: RoomDataSource

    private lateinit var titleDef: String

    private var mask: String? = null

    private lateinit var disposable: Disposable

    constructor() {
        App.appComponent.inject(this)
    }

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
        Countries.getRegCountries(getCountriesIso()).let { iso ->
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

        val phoneUser: String = regPhone?.trim { it <= ' ' }!!
        var phone = regPrefixPhone + phoneUser?.replace("\\D".toRegex(), "")
        phone = phone.replace(" ", "")

        val submitRequest = SubmitRequest(phone)

        Injector.getSettingsStore().refererrClient?.let {
            submitRequest.referralCode = it
        }

        disposable = apiService.findPhone(submitRequest)
                .subscribeOn(Schedulers.io())
                .doOnSuccess { submitted -> onSubmittedSuccess(submitted) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ submitted -> onSubmitted(submitted, regPrefixPhone, phoneUser, phone) },
                        { error -> onError(error) })
    }

    private fun onSubmittedSuccess(submitted: Submitted) =
            submitted?.apply {
                db.clientDao().run {
                    insertClient(ClientEntity.create(clientId = id))
                    // just for test
                    getClients().subscribe { clients -> Timber.i("kiv007 clients" + clients.get(0).clientId) }
                }
            }

    private fun onSubmitted(submitted: Submitted, regPrefixPhone: String, phoneUser: String, phone: String) {
        submitted.id?.let {
            viewState.showPrefixSmsCode(regPrefixPhone, phoneUser, it, phone)

            Timber.i("kiv007 submitted id after sending phone: $it")
        }
    }

    private fun onError(error: Throwable) {
        error.message?.let {
            viewState.showAlertInvalidNumber()
            Timber.i("kiv007 ERROR  after sending phone $it")
        }
    }

    fun onDestroyFragment() {
        disposable?.run {
            if (!isDisposed) {
                dispose()
            }
        }
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
