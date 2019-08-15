package com.opentadam.ui.registration

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.OnClick
import com.arellomobile.mvp.presenter.InjectPresenter
import com.opentadam.Constants.IS_PROFIL
import com.opentadam.Constants.IS_RESTART
import com.opentadam.Injector
import com.opentadam.R
import com.opentadam.data.DialogClient
import com.opentadam.ui.BaseFr
import com.opentadam.ui.registration.mvp.presenter.V2RegistrationPresenter
import com.opentadam.ui.registration.mvp.view.V2RegistrationView
import com.opentadam.utils.CustomTypefaceSpan
import kotlinx.android.synthetic.main.f_registration.*

class V2RegistrationFragment : BaseFr(), V2RegistrationView {
    private var maskedWatcher: MaskedWatcher? = null

    private var v2KeyNumber: V2KeyNumber? = null

    private var isProfil = false
    private var isRestart = false

    @InjectPresenter
    lateinit var presenter: V2RegistrationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        aWork.window
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        arguments?.run {
            isProfil = getBoolean(IS_PROFIL, false)
            isRestart = getBoolean(IS_RESTART, false)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bodyUI()
    }

    private fun bodyUI() {
        (if (Injector.getCountryList() == null) View.INVISIBLE else View.VISIBLE).let {
            login_flag_icon.visibility = it
            login_flag_select.visibility = it
        }

        v2KeyNumber = childFragmentManager
                .findFragmentById(R.id.keyboard_number) as? V2KeyNumber
        v2KeyNumber?.initEdit(reg_edit_phone, true)

        presenter.setTitleDef(getString(if (isProfil)
            R.string.navigation_phone
        else
            R.string.registration_title))

        v2_title.text = presenter.getTitleDef()

        var textStart = getString(R.string.value_info_conf) + " "
        var textUrl = getString(R.string.sub_info_conf)
        val textFin = " " + getString(R.string.sub_info_conf_dop)
        var builder = initSpannableStringBuilder(textStart, textUrl, textFin)
        sub_info_conf.setText(builder, TextView.BufferType.SPANNABLE)

        textStart = getString(R.string.freg_sub_policy_info) + " "
        textUrl = getString(R.string.value_info_conf_two_sub)
        builder = initSpannableStringBuilder(textStart, textUrl, null)
        info_conf_two_sub.setText(builder, TextView.BufferType.SPANNABLE)

        presenter.initUI()
    }

    private fun initSpannableStringBuilder(textStart: String?, textUrl: String?, textFin: String?): SpannableStringBuilder {
        if (textStart == null) {
            return SpannableStringBuilder("")
        }

        val fontBold = Typeface.createFromAsset(Injector.getAppContext().assets, "fonts/Roboto/Roboto-Bold.ttf")
        val fontLight = Typeface.createFromAsset(Injector.getAppContext().assets, "fonts/Roboto/Roboto-Light.ttf")

        val builder = SpannableStringBuilder()

        SpannableString(textStart).apply {
            // red
            setSpan(CustomTypefaceSpan(fontLight), 0, textStart.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(ForegroundColorSpan(initColor(R.color.text_default_color)), 0, textStart.length, 0)
        }.let {
            builder.append(it)
        }

        if (textUrl == null) {
            return builder
        }

        SpannableString(textUrl).apply {
            // white
            setSpan(ForegroundColorSpan(initColor(R.color.colorPrimary)), 0, textUrl.length, 0)
            setSpan(CustomTypefaceSpan(fontLight), 0, textUrl.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        }.let {
            builder.append(it)
        }

        if (textFin == null) {
            return builder
        }

        SpannableString(textFin).apply {
            // blue
            setSpan(CustomTypefaceSpan(fontBold), 0, textFin.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(ForegroundColorSpan(initColor(R.color.text_default_color)), 0, textFin.length, 0)
        }.let {
            builder.append(it)
        }

        return builder
    }

    override fun initUI(countryFlag: Int, countryPhonePrefix: String, hintMask: String) {

        login_flag_icon.setImageResource(countryFlag)

        reg_prefix_phone.setText(countryPhonePrefix)

        // коррекция отступа от префикса:

        reg_edit_phone.let {
            val padding = when (countryPhonePrefix.length) {
                1 -> getPX(76)
                2 -> getPX(86)
                3 -> getPX(96)
                4 -> getPX(110)
                else -> getPX(76)
            }

            it.setPadding(padding, 0, 0, 0)

            it.hint = hintMask
        }

        presenter.replaceMask()?.let {
            setMaskedWatcher(it)
        }
    }

    private fun setMaskedWatcher(mask: String) {
        reg_edit_phone.let {
            if (maskedWatcher != null)
                it.removeTextChangedListener(maskedWatcher)
            maskedWatcher = MaskedWatcher(mask)
            it.addTextChangedListener(maskedWatcher)
        }
    }

    override fun setV2KeyNumberVisibility(visible: Boolean) {
        v2KeyNumber?.setVisibilityKeyboord((if (visible) View.VISIBLE else View.GONE))
    }

    override fun onDestroyView() {
        aWork.hideWorkProgress()
        aWork.window
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        super.onDestroyView()
    }

    @OnClick(R.id.select_prefix)
    fun onSelectCountry() {
        presenter.onSelectCountry()
    }

    override fun showCountryList(countryIsoList: Array<String?>) {
        v2KeyNumber?.setVisibilityKeyboord(View.GONE)
        login_countries_list.let {
            it.visibility = View.VISIBLE
            it.removeAllViews()
            val layoutInflater = LayoutInflater.from(it.context)

            for (iso in countryIsoList) {
                val countryItem = layoutInflater
                        .inflate(R.layout.i_login_country, it, false) as LinearLayout
                val holder = CountryViewHolder(countryItem)

                presenter.getCountrlyFlag(iso).let {
                    holder.iLoginCountryFlag.setImageResource(it)
                }

                holder.run {
                    iLoginCountryName.text = presenter.getCountryName(iso)
                    iLoginCountryPrefix.text = presenter.getCountryPhonePrefix(iso)
                }

                it.addView(countryItem)

                countryItem.setOnClickListener {
                    presenter.onCountryItemSelected(iso)
                    login_countries_list.visibility =View.GONE
                    login_countries_list.removeAllViews()
                    v2KeyNumber?.setVisibilityKeyboord(View.VISIBLE)
                    presenter.initUI()
                }
            }
        }
    }

    override fun hideKeyboard() {
        hideKeyboard(reg_edit_phone)
    }

    @OnClick(R.id.sub_info_conf)
    fun subInfoConf() {
        presenter.onSelectUserAgreement()
    }

    @OnClick(R.id.info_conf_two_sub)
    fun infoConfTwoSub() {
        presenter.onSelectPolicyPrivacy()
    }

    @OnClick(R.id.v2_bask)
    fun onv2Bask() {
        cont_reg.run {
            if (visibility == View.GONE) {
                visibility = View.VISIBLE
                v2_title.setText(presenter.getTitleDef())
            } else if (isProfil)
                aWork.showFProfil()
            else
                aWork.closeApp()
        }
    }

    @OnClick(R.id.reg_send_phone)
    fun regSendPhone() {
        presenter.onSendPhone(reg_edit_phone.text.toString(), reg_prefix_phone.text.toString(), isVisible)
    }

    @OnClick(R.id.reg_del_phone)
    fun onDel() {
        reg_edit_phone.setText("")
    }

    override fun showAlertCheckNumbers() {
        showAlert(R.string.error_phone)
    }

    override fun showAlertInvalidNumber() {
        showAlert(R.string.freg_error_phone_servers)
    }

    override fun showProgress() {
        aWork.showWorkProgress()
    }

    private fun showAlert(resMsgId: Int) {
        DialogClient.alertInfo(resources
                .getString(resMsgId), aWork)
    }

    override fun showPrefixSmsCode(regPrefixPhone: String, phoneUser: String, id: Long, value: String) {
        aWork.showV2FSmsCode(regPrefixPhone + phoneUser, id, value, isProfil, isRestart)
    }

    override fun onBackPressed(): Boolean {
        onv2Bask()
        return true
    }

    override fun onDestroy() {
        presenter.onDestroyFragment()

        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(isProfil: Boolean, isRestart: Boolean) =
                V2RegistrationFragment()
                        .withViewId(R.layout.f_registration)
                        .withArgument(IS_PROFIL, isProfil)
                        .withArgument(IS_RESTART, isRestart)
    }
}
