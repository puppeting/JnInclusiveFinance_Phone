package com.inclusive.finance.jh.ui.account

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.blankj.utilcode.util.SPUtils
import com.hwangjr.rxbus.RxBus
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.databinding.FragmentAccountCenterBinding
import org.jetbrains.anko.support.v4.act
import java.util.concurrent.Executor

class AccountCenterFragment : MyBaseFragment(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentAccountCenterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)

        viewBind = FragmentAccountCenterBinding.inflate(inflater, container, false).apply {
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    private val handler: Handler = Handler()

    private val executor: Executor = Executor { command -> handler.post(command) }
    lateinit var biometricManager: BiometricManager
    override fun initView() {
        biometricManager = BiometricManager.from(requireContext())


    }

    override fun initEvent() {
        viewBind.btClose.setOnClickListener(this)
        viewBind.btPwdChange.setOnClickListener(this)
        viewBind.btGestureChange.setOnClickListener(this)
        viewBind.isGestureLogin.setOnCheckedChangeListener(this)
        viewBind.isFingerLogin.setOnCheckedChangeListener(this)

    }

    //生物认证的setting
//    private lateinit var cryptographyManager: CryptographyManager
//    private fun showBiometricPromptForEncryption() {
//        val canAuthenticate = BiometricManager.from(this).canAuthenticate(Authenticators.BIOMETRIC_WEAK)
//        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
//            val secretKeyName = "getString(R.string.secret_key_name)"
//            cryptographyManager = CryptographyManager()
//            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
//            val biometricPrompt =
//                BiometricPromptUtils.createBiometricPrompt(this, ::encryptAndStoreServerToken)
//            val promptInfo = BiometricPromptUtils.createPromptInfo()
//            biometricPrompt.authenticate(promptInfo, CryptoObject(cipher))
//        }
//    }
//
//    private fun encryptAndStoreServerToken(authResult: AuthenticationResult) {
//        authResult.cryptoObject?.cipher?.apply {
//            val encryptedServerTokenWrapper = cryptographyManager.encryptData("token", this)
//            cryptographyManager.persistCiphertextWrapperToSharedPrefs(
//                encryptedServerTokenWrapper,
//                this@LoginPasswordActivity,
//                "SHARED_PREFS_FILENAME",
//                Context.MODE_PRIVATE,
//                "CIPHERTEXT_WRAPPER"
//            )
//        }
//    }

    override fun onClick(v: View?) {
        when (v) {
            viewBind.btClose -> {
//                dismiss()
                RxBus.get().post("dismiss", "dismiss")
            }
            viewBind.btPwdChange -> {
                NavHostFragment.findNavController(this).navigate(R.id.action_to_pwd)
            }
            viewBind.btGestureChange -> {
                NavHostFragment.findNavController(this).navigate(R.id.action_to_gesture)
            }
            else -> {
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView) {
            viewBind.isGestureLogin -> {
                if (isChecked) {
                    if (SPUtils.getInstance().getString(Constants.SPUtilsConfig.GESTURELOCK_KEY)
                            .isEmpty()) {
                        viewModel.isGestureLogin.value = false
                        NavHostFragment.findNavController(this).navigate(R.id.action_to_gesture)
                    } else {
                        SPUtils.getInstance()
                            .put(Constants.SPUtilsConfig.ISGESTURELOCK_KEY, isChecked)
                    }
                } else {
                    viewModel.isGestureLogin.value = false
                    SPUtils.getInstance().put(Constants.SPUtilsConfig.ISGESTURELOCK_KEY, isChecked)
                }
            }
            viewBind.isFingerLogin -> {
                if (isChecked) {
                    viewModel.isFingerLogin.value = false
                    when (biometricManager.canAuthenticate(Authenticators.BIOMETRIC_WEAK)) {
                        BiometricManager.BIOMETRIC_SUCCESS -> {
//                Toast.makeText(this, "应用可以进行生物识别技术进行身份验证", Toast.LENGTH_SHORT).show()
                            viewModel.isFingerLogin.value =true
                            SPUtils.getInstance().put(Constants.SPUtilsConfig.ISFINGERLOCK_KEY, true)

                        }
                        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Toast.makeText(requireContext(), "该设备上没有搭载可用的生物特征功能", Toast.LENGTH_SHORT)
                            .show()
                        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Toast.makeText(requireContext(), "生物识别功能当前不可用", Toast.LENGTH_SHORT)
                            .show()
                        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> Toast.makeText(requireContext(), "用户没有录入生物识别数据", Toast.LENGTH_SHORT)
                            .show()

                    }
                }else{
                    viewModel.isFingerLogin.value = false
                    SPUtils.getInstance().put(Constants.SPUtilsConfig.ISFINGERLOCK_KEY, false)
                }


            }
        }

    }


}