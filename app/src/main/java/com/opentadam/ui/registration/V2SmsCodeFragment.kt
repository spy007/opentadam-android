package com.opentadam.ui.registration

import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.util.TypedValue
import android.view.WindowManager
import butterknife.OnClick
import butterknife.OnTextChanged
import com.arellomobile.mvp.presenter.InjectPresenter
import com.badoo.mobile.util.WeakHandler
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.opentadam.App
import com.opentadam.Constants
import com.opentadam.Constants.IS_PROFIL
import com.opentadam.Constants.IS_RESTART
import com.opentadam.Injector
import com.opentadam.R
import com.opentadam.bus.BusPreloadMenu
import com.opentadam.data.DialogClient
import com.opentadam.ui.BaseFr
import com.opentadam.ui.registration.mvp.presenter.ID
import com.opentadam.ui.registration.mvp.presenter.PHONE
import com.opentadam.ui.registration.mvp.presenter.PHONE_INFO
import com.opentadam.ui.registration.mvp.presenter.V2SmsCodePresenter
import com.opentadam.ui.registration.mvp.view.V2SmsCodeView
import kotlinx.android.synthetic.main.f_sms_code.*
import java.io.IOException

class V2SmsCodeFragment : BaseFr(), V2SmsCodeView {
    private var mMaskedWatcher: MaskedWatcher? = null

    @InjectPresenter
    lateinit var presenter: V2SmsCodePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        aWork.window
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        presenter.onCreate(arguments)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val v2KeyNumber = childFragmentManager
                .findFragmentById(R.id.keyboard_number) as V2KeyNumber
        v2KeyNumber.initEdit(reg_edit_code, true)

        presenter.run {
            onActivityCreated()

            reg_get_code.setText(R.string.title_set_code)

            setMaskedWatcher(mask)

            sms_value_phone.text = getPhone()
        }
    }

    override fun refreshOnSendingToServers() {
        reg_get_call.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
        presenter.getPeriodicBlockSms().let {
            reg_get_call.setText(String.format("00:%s", if (it < 10) "0$it" else "" + it))
            v2_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
            v2_title.setText(String.format("00:%s", if (it < 10) "0$it" else "" + it))
        }
    }

    override fun refreshOnNoBlockSms() {
        reg_get_call.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11f)
        reg_get_call.setText(getString(R.string.request_code))
        v2_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11f)
        v2_title.setText(getString(R.string.button_get_call))
    }

    private fun setMaskedWatcher(mask: String?) {
        mMaskedWatcher?.let {
            reg_edit_code.removeTextChangedListener(it)
        }
        mMaskedWatcher = MaskedWatcher(mask)
        reg_edit_code.addTextChangedListener(mMaskedWatcher)
    }

    override fun onConfirmed() {
        aWork.setObjectRestoryV3Route(null)

        App.bus.post(BusPreloadMenu())

        aWork.setObjectRestoryV3Route(null)
        aWork.restartAll()
    }

    override fun refreshedToken() {
        WeakHandler(Looper.getMainLooper()).post {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(aWork, OnSuccessListener { instanceIdResult ->
                val readRegKey = Injector.getSettingsStore().readString(Constants.REG_KEY_CLIENT, null)
                if (readRegKey == null)
                    return@OnSuccessListener

                presenter.onRefreshedToken(instanceIdResult.token)
            })
        }
    }

    @OnClick(R.id.reg_get_code)
    fun onRegGetCode() {
        presenter.onRegGetCode()
    }

    @OnClick(R.id.reg_get_call)
    fun onRegGetCall() {
        presenter.onRegGetCall()
    }

    @OnClick(R.id.reg_get_in)
    fun onRegGetIn() {
        presenter.validateCode(reg_edit_code.text.toString())
    }

    @OnClick(R.id.reg_del_code)
    fun regDelCode() {
        reg_edit_code.setText("")
    }

    @OnClick(R.id.v2_bask)
    fun onv2Bask() {
        presenter.run { aWork.showV2FRegistration(isProfil, isRestart) }
    }

    override fun onBackPressed(): Boolean {
        onv2Bask()
        return true
    }

    override fun showMessageWrongSmsCode() {
        showMessage(R.string.error_code)
    }

    override fun showMessageWrongConfirmationCode() {
        showMessage(R.string.error_val_code)
    }

    override fun showMessageCallRequestSent() {
        showMessage(R.string.send_ok)
    }

    override fun showMessageSmsRequestSent() {
        showMessage(R.string.send_ok_code)
    }

    private fun showMessage(resTextId: Int) {
        DialogClient.alertInfo(resources.getString(resTextId), aWork)
    }

    @OnTextChanged(R.id.reg_edit_code)
    fun afterTextChangedEditCode(editable: Editable) {
        presenter.onSmsCodeChanged(editable.toString())
    }


    override fun hideKeyboard() {
        hideKeyboard(reg_edit_code)
    }

    override fun onDestroy() {
        presenter.onDestroyFragment()

        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(phone: String, id: Long, value: String, isProfil: Boolean, isRestart: Boolean) =
                V2SmsCodeFragment().withViewId(R.layout.f_sms_code)
                        .withArgument(PHONE, phone)
                        .withArgument(ID, id)
                        .withArgument(PHONE_INFO, value)
                        .withArgument(IS_PROFIL, isProfil)
                        .withArgument(IS_RESTART, isRestart)
    }
}
