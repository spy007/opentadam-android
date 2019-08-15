package com.opentadam.ui.registration.mvp.presenter

import android.os.Bundle
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.opentadam.App
import com.opentadam.Constants
import com.opentadam.Constants.IS_PROFIL
import com.opentadam.Constants.IS_RESTART
import com.opentadam.Injector
import com.opentadam.network.HiveHmacSigner
import com.opentadam.network.rest.Confirmed
import com.opentadam.network.rest.FsmInfo
import com.opentadam.ui.registration.api.RemoteService
import com.opentadam.ui.registration.mvp.view.V2SmsCodeView
import com.opentadam.ui.registration.room.RoomDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

const val PHONE = "phone"
const val ID = "id"
const val PHONE_INFO = "phone_info"

@InjectViewState
class V2SmsCodePresenter : MvpPresenter<V2SmsCodeView> {

    private var eql = false

    @Inject
    lateinit var apiService: RemoteService

    @Inject
    lateinit var db: RoomDataSource

    val mask = "####"

    private var isSendToServersSMS = false

    private var isSendToServersCALL = false

    private var timerRefresher: Timer? = null

    private var isSendCode = false

    var isRestart = false

    var isProfil = false

    private var mPhone: String? = null

    private var mPhoneInfo: String? = null

    private var mId: Long = 0

    private var periodBlockSMS = 59

    private var periodBlockCALL = 59

    private lateinit var disposable: Disposable

    private var smsCodeSize = 0

    constructor() {
        App.appComponent.inject(this)
    }

    fun onCreate(arguments: Bundle?) {
        arguments?.run {
            mPhone = getString(PHONE)
            mId = getLong(ID)
            mPhoneInfo = getString(PHONE_INFO)
            isProfil = getBoolean(IS_PROFIL, false)
            isRestart = getBoolean(IS_RESTART, false)
        }
    }

    fun onActivityCreated() {
        refresh()
    }

    private fun refresh() {
        if (timerRefresher == null)
            timerRefresher = Timer()

        timerRefresher?.schedule(object : TimerTask() {
            override fun run() {
                if (isSendToServers()) {
                    viewState.refreshOnSendingToServers()
                    periodBlockSMS--
                    periodBlockCALL--
                }
                if (periodBlockSMS == 0) {
                    viewState.refreshOnNoBlockSms()
                    periodBlockSMS = 59
                    isSendToServersSMS = false
                    isSendToServersCALL = false
                }
            }
        }, 1000, 1000)
    }

    private fun isSendToServers() = isSendToServersSMS || isSendToServersCALL

    fun onRegGetCall() {
        if (isSendToServers())
            return

        viewState.showMessageCallRequestSent()
        isSendToServersCALL = true

        disposable = apiService.reSubmit(mId, "voice")
                .doOnError(this::onResubmitError)
                .subscribe()
    }

    private fun onResubmitError(error: Throwable) {
        error.message?.let {
            Timber.e("kiv007 ERROR on resubmit $it")
        }
    }

    fun onRegGetCode() {
        if (isSendToServers())
            return

        viewState.showMessageSmsRequestSent()
        isSendToServersSMS = true

        apiService.reSubmit(mId, "sms")
                .doOnError(this::onResubmitError)
                .subscribe()
    }

    fun validateCode(regEditCode: String?) {
        if (regEditCode == null)
            return

        if (regEditCode.length != mask.length) {
            viewState.showMessageWrongSmsCode()
            return
        }

        viewState.hideKeyboard()

        if (!isSendCode) {
            isSendCode = true

            disposable = apiService.findConfirm(mId, regEditCode)
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess { submitted -> onComfirmedSuccess(submitted) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ confirmed -> onConfirmed() },
                            { error -> onConfirmError(error) })
        }
    }

    private fun onComfirmedSuccess(confirmed: Confirmed) =
            confirmed?.apply {

                Injector.getSettingsStore()?.run {
                    writeString(Constants.REG_PHONE_CLIENT, mPhoneInfo)
                    writeLong(Constants.REG_ID_CLIENT, id)
                    writeString(Constants.REG_KEY_CLIENT, key)
                }

//                db.confirmDao().run {
//                    insertConfirm(ConfirmEntity.create(id = id, key = key))
//                    // just for test
//                    getConfirm().subscribe{confirm -> Timber.i("kiv007 confirms" + confirm.get(0).key)}
//                }

            }

    private fun onConfirmed() {
        viewState.refreshedToken()

        viewState.onConfirmed()
    }

    private fun onConfirmError(error: Throwable) {
        error.message?.let {
            viewState.showMessageWrongConfirmationCode()
            Timber.e("kiv007 ERROR  after sending phone $it")
        }
    }

    fun onRefreshedToken(token: String) {
        token.let {
            Log.e("MyFirebaseIIDService", "Refreshed token: $it")

            val head = HiveHmacSigner
                    .addRegAutor("POST", Constants.PATH_FCM)
                    ?: // String error = "Пользователь не зарегистрирован";
                    return

            apiService.findToken(head.get("Date")!!, head.get("Authentication")!!,
                    FsmInfo(token))
                    .subscribe({}, this::onFindTokenError)
        }
    }

    private fun onFindTokenError(error: Throwable) {
        error.message?.let {
            Timber.e("kiv007 ERROR on finding token $it")
        }
    }

    fun getPhone() = mPhone

    fun getPeriodicBlockSms() = periodBlockSMS

    fun onDestroyFragment() {
        disposable.dispose()
    }

    fun onSmsCodeChanged(smsCode: String) {
        eql = smsCodeSize == smsCode.length // this check because @OnTextChanged method in fragment calls twice for every changing
        smsCodeSize = smsCode.length
        if (!eql && smsCode.length == 4) {
            validateCode(smsCode)
        }
    }
}