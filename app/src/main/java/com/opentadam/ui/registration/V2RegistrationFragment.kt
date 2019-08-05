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
import android.widget.*
import butterknife.InjectView
import butterknife.OnClick
import com.opentadam.Injector
import com.opentadam.R
import com.opentadam.data.DialogClient
import com.opentadam.ui.BaseFr
import com.opentadam.ui.registration.mvp.presenter.V2RegistrationPresenter
import com.opentadam.ui.registration.mvp.view.V2RegistrationView
import com.opentadam.utils.CustomTypefaceSpan

private const val IS_PROFIL = "isProfil"
private const val IS_RESTART = "isRestart"

class V2RegistrationFragment : BaseFr(), V2RegistrationView {
    @InjectView(R.id.login_country_select)
    @JvmField
    internal var loginCountrySelect: FrameLayout? = null
    @InjectView(R.id.reg_prefix_phone)
    @JvmField
    internal var regPrefixPhone: TextView? = null
    @InjectView(R.id.reg_edit_phone)
    @JvmField
    internal var regEditPhone: EditText? = null
    @InjectView(R.id.login_countries_list)
    @JvmField
    internal var loginCountriesList: LinearLayout? = null
    @InjectView(R.id.v2_title)
    @JvmField
    internal var v2Title: TextView? = null
    @InjectView(R.id.cont_reg)
    @JvmField
    internal var contReg: LinearLayout? = null
    @InjectView(R.id.login_flag_icon)
    @JvmField
    internal var loginFlagIcon: ImageView? = null
    @InjectView(R.id.login_flag_select)
    @JvmField
    internal var loginFlagSelect: View? = null
    @InjectView(R.id.sub_info_conf)
    @JvmField
    internal var infoAgreement: TextView? = null
    @InjectView(R.id.info_conf_two_sub)
    @JvmField
    internal var infoPolicyPrivacy: TextView? = null

    private var maskedWatcher: MaskedWatcher? = null

    private var v2KeyNumber: V2KeyNumber? = null

    private var isProfil = false
    private var isRestart = false

    // TODO: figure out why Moxy doesn't inject presenter
//    @InjectPresenter(type = PresenterType.GLOBAL)
    var presenter: V2RegistrationPresenter = V2RegistrationPresenter()
//
//    @ProvidePresenterTag(presenterClass = V2RegistrationPresenter::class, type = PresenterType.GLOBAL)
//    fun provideV2RegistrationPresenterTag(): String = "Hello"
//
//    @ProvidePresenter(type = PresenterType.GLOBAL)
//    fun provideV2RegistrationPresenter() = V2RegistrationPresenter()


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
            loginFlagIcon?.visibility = it
            loginFlagSelect?.visibility = it
        }

        v2KeyNumber = childFragmentManager
                .findFragmentById(R.id.keyboard_number) as? V2KeyNumber
        v2KeyNumber?.initEdit(regEditPhone, true)

        presenter.setTitleDef(getString(if (isProfil)
            R.string.navigation_phone
        else
            R.string.registration_title))

        v2Title?.setText(presenter.getTitleDef())

        var textStart = getString(R.string.value_info_conf) + " "
        var textUrl = getString(R.string.sub_info_conf)
        val textFin = " " + getString(R.string.sub_info_conf_dop)
        var builder = initSpannableStringBuilder(textStart, textUrl, textFin)
        infoAgreement?.setText(builder, TextView.BufferType.SPANNABLE)

        textStart = getString(R.string.freg_sub_policy_info) + " "
        textUrl = getString(R.string.value_info_conf_two_sub)
        builder = initSpannableStringBuilder(textStart, textUrl, null)
        infoPolicyPrivacy?.setText(builder, TextView.BufferType.SPANNABLE)

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

        countryFlag?.let {
            loginFlagIcon?.setImageResource(it)
        }

        regPrefixPhone?.text = countryPhonePrefix

        // коррекция отступа от префикса:

        regEditPhone?.let {
            val padding = when (countryPhonePrefix.length) {
                1 -> getPX(76)
                2 -> getPX(86)
                3 -> getPX(96)
                4 -> getPX(110)
                else -> getPX(76)
            }

            it.setPadding(padding, 0, 0, 0)

            it.setHint(hintMask)
        }

        presenter.replaceMask()?.let {
            setMaskedWatcher(it)
        }
    }

    private fun setMaskedWatcher(mask: String) {
        regEditPhone?.let {
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
        loginCountriesList?.let {
            loginCountrySelect?.setVisibility(View.VISIBLE)
            it.removeAllViews()
            val layoutInflater = LayoutInflater.from(it.getContext())

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
                    loginCountrySelect?.setVisibility(View.GONE)
                    loginCountriesList?.removeAllViews()
                    v2KeyNumber?.setVisibilityKeyboord(View.VISIBLE)
                    presenter.initUI()
                }
            }
        }
    }

    override fun hideKeyboard() {
        hideKeyboard(regEditPhone)
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
        contReg?.run {
            if (visibility == View.GONE) {
                visibility = View.VISIBLE
                v2Title?.setText(presenter.getTitleDef())
            } else if (isProfil)
                aWork.showFProfil()
            else
                aWork.closeApp()
        }
    }

    @OnClick(R.id.reg_send_phone)
    fun regSendPhone() {
        presenter.onSendPhone(regEditPhone?.text.toString(), regPrefixPhone?.text.toString(), isVisible)
    }

    @OnClick(R.id.reg_del_phone)
    fun onDel() {
        regEditPhone?.setText("")
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

    companion object {
        @JvmStatic
        fun newInstance(isProfil: Boolean, isRestart: Boolean) =
                V2RegistrationFragment()
                        .withViewId(R.layout.f_registration)
                        .withArgument(IS_PROFIL, isProfil)
                        .withArgument(IS_RESTART, isRestart)
    }
}
