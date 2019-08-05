package com.opentadam.ui.registration

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import butterknife.InjectView
import butterknife.OnClick
import com.opentadam.App
import com.opentadam.Constants
import com.opentadam.Injector
import com.opentadam.R
import com.opentadam.data.Countries
import com.opentadam.data.DialogClient
import com.opentadam.network.IGetApiResponse
import com.opentadam.network.rest.Country
import com.opentadam.network.rest.SubmitRequest
import com.opentadam.ui.BaseFr
import com.opentadam.utils.CustomTypefaceSpan
import kotlinx.android.synthetic.main.f_registration.*
import java.util.*

private const val IS_PROFIL = "isProfil"
private const val IS_RESTART = "isRestart"

class V2RegistrationFragment : BaseFr()  {
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

    private var phoneUser: String? = null
    private var maskedWatcher: MaskedWatcher? = null
    private var mMask: String? = null
    private var titleDef: String? = null
    private var v2KeyNumber: V2KeyNumber? = null

    private var isProfil = false
    private var isRestart = false

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

        titleDef = getString(if (isProfil)
            R.string.navigation_phone
        else
            R.string.registration_title)

        v2Title?.setText(titleDef)

        var textStart = getString(R.string.value_info_conf) + " "
        var textUrl = getString(R.string.sub_info_conf)
        val textFin = " " + getString(R.string.sub_info_conf_dop)
        var builder = initSpannableStringBuilder(textStart, textUrl, textFin)
        infoAgreement?.setText(builder, TextView.BufferType.SPANNABLE)

        textStart = getString(R.string.freg_sub_policy_info) + " "
        textUrl = getString(R.string.value_info_conf_two_sub)
        builder = initSpannableStringBuilder(textStart, textUrl, null)
        infoPolicyPrivacy?.setText(builder, TextView.BufferType.SPANNABLE)

        initUI()
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

    private fun initUI() {
        val countries = Countries.getRegCountries(getCountriesList())

        Countries.getCountryFlag(countries)?.let {
            loginFlagIcon?.setImageResource(it)
        }

        val countryPhonePrefix = Countries.getCountryPhonePrefix(countries)
        regPrefixPhone?.text = countryPhonePrefix
        mMask = Countries.getCountryPhoneMask(countries)
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

            val hintMask = mMask?.let {
                it.substring(0, it.length - Countries.ADD_MASK.length)
            }

            it.setHint(hintMask)
        }

        mMask = mMask?.replace("X", "#")

        mMask?.let {
            setMaskedWatcher(it)
        }
    }

    private fun openWebpage(url: String?) {

        url.let {
            val address = Uri.parse(it)
            val openlinkIntent = Intent(Intent.ACTION_VIEW, address)

            startActivity(Intent
                    .createChooser(openlinkIntent, getString(R.string.select_app)))
        }
    }

    private fun validatePhone() {

        if (regEditPhone == null
                || mMask == null
                || regEditPhone?.getText() == null
                || Countries.MASK_DEFAULT != mMask && regEditPhone!!.getText()!!.length < mMask!!.length - Countries.ADD_MASK.length) {
            DialogClient.alertInfo(resources
                    .getString(R.string.error_phone), aWork)
            return
        }

        phoneUser = regEditPhone?.getText().toString().trim { it <= ' ' }

        aWork.showWorkProgress()
        val phone = regPrefixPhone?.text.toString() + phoneUser?.replace("\\D".toRegex(), "")
        val value = phone.replace(" ", "")
        val restConnect = Injector.getRC()

        val submitRequest = SubmitRequest(value)
        val rfererrClient = Injector.getSettingsStore().refererrClient
        rfererrClient.let {
            Log.e("test_referal", "sms файрбах rfererrClient = " + it)
            submitRequest.referralCode = it
        }

        restConnect.sendPhoneToServers(submitRequest, IGetApiResponse { apiResponse ->
            if (!isVisible)
                return@IGetApiResponse

            apiResponse.error?.let {
                DialogClient.alertInfo(getString(R.string.freg_error_phone_servers), aWork)
                return@IGetApiResponse
            }

            if (Constants.PATH_REG_PHONE == apiResponse.path) {
                val ph = reg_prefix_phone.text.toString() + phoneUser?.replace("\\D".toRegex(), "")
                val vl = ph.replace(" ", "")
                val submitted = apiResponse.submitted
                val id = submitted.id
                aWork.showV2FSmsCode(reg_prefix_phone.text.toString() + phoneUser, id, vl, isProfil, isRestart)
            }
        })
    }

    private fun getCountriesList(): Array<String?> {
        val countryList: List<Country> = Injector.getCountryList() ?: ArrayList()

        val size = countryList.size

        return arrayOfNulls<String>(size).also { lst ->
            for (i in 0 until size) {
                val country = countryList[i]
                lst[i] = country.isoCode
            }
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

    override fun onDestroyView() {
        aWork.hideWorkProgress()
        aWork.window
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        super.onDestroyView()
    }

    @OnClick(R.id.select_prefix)
    fun onSelectCountryClicked() {
        if (Injector.getCountryList() == null)
            return

        v2KeyNumber?.setVisibilityKeyboord(View.GONE)
        loginCountriesList?.let {
            loginCountrySelect?.setVisibility(View.VISIBLE)
            it.removeAllViews()
            val layoutInflater = LayoutInflater.from(it.getContext())

            for (countryAcronim in getCountriesList()) {
                val countryItem = layoutInflater
                        .inflate(R.layout.i_login_country, it, false) as LinearLayout
                val holder = CountryViewHolder(countryItem)

                val countryFlag = Countries.getCountryFlag(countryAcronim)

                if (countryFlag != null)
                    holder.iLoginCountryFlag.setImageResource(countryFlag)

                holder.iLoginCountryName.text = Countries.getCountryName(countryAcronim)
                holder.iLoginCountryPrefix.text = Countries.getCountryPhonePrefix(countryAcronim)


                it.addView(countryItem)

                countryItem.setOnClickListener {
                    Injector.getSettingsStore().writeString("countries", countryAcronim)
                    loginCountrySelect?.setVisibility(View.GONE)
                    loginCountriesList?.removeAllViews()
                    v2KeyNumber?.setVisibilityKeyboord(View.VISIBLE)
                    initUI()
                }
            }
        }
    }


    @OnClick(R.id.sub_info_conf)
    fun subInfoConf() {
        val urlUserAgreement = App.app.hashBC.URL_USER_AGREEMENT
        openWebpage(urlUserAgreement)
    }

    @OnClick(R.id.info_conf_two_sub)
    fun infoConfTwoSub() {
        val urlPolicyPrivacy = App.app.hashBC.URL_POLICY_PRIVACY
        openWebpage(urlPolicyPrivacy)
    }

    @OnClick(R.id.v2_bask)
    fun onv2Bask() {
        contReg?.run {
            if (visibility == View.GONE) {
                visibility = View.VISIBLE
                v2Title?.setText(titleDef)
            } else if (isProfil)
                aWork.showFProfil()
            else
                aWork.closeApp()
        }
    }

    override fun onBackPressed(): Boolean {
        onv2Bask()
        return true
    }

    @OnClick(R.id.reg_send_phone)
    fun regSendPhone() {
        hideKeyboard(regEditPhone)
        validatePhone()
    }

    @OnClick(R.id.reg_del_phone)
    fun onDel() {
        regEditPhone?.setText("")
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
