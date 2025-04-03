package com.neko.hiepdph.dynamicislandvip.common.base_component

//import com.example.baseproject.BuildConfig
//import com.example.baseproject.common.InterAdsEnum
//import com.example.baseproject.common.isInternetAvailable
//import com.example.baseproject.view.home.MainActivity
//import com.neko.hiepdph.mypiano.view.dialog.DialogFragmentLoadingInterAds
//import com.gianghv.admob.InterstitialSingleReqAdManager
//import com.gianghv.libads.max.MaxInterstitialSingleReqAdManager
//import com.example.baseproject.common.ads.utils.AdsConfigUtils
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment

import androidx.viewbinding.ViewBinding
import com.neko.hiepdph.dynamicislandvip.common.AppSharePreference
import com.neko.hiepdph.mypiano.common.base_component.BaseActivity


abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    lateinit var binding: VB


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding(inflater, container)
        return binding.root
    }



    protected fun changeBackPressCallBack(action: () -> Unit) {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                action.invoke()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        callbackSharePreference()?.let { callback ->
            AppSharePreference.INSTANCE.registerOnSharedPreferenceChangeListener(callback)
        }
    }

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    abstract fun initView()

    open fun callbackSharePreference(): SharedPreferences.OnSharedPreferenceChangeListener? {
        return null
    }




    @MainThread
    fun pushTo(@IdRes resId: Int, args: Bundle? = null) {
        (activity as BaseActivity<*>).pushTo(resId, args)
    }

    @MainThread
    fun popTo(@IdRes destinationId: Int? = null, inclusive: Boolean = false) {
        (activity as BaseActivity<*>).popTo(destinationId, inclusive)
    }


//    protected fun navigateToFragment(fragmentId: Int, actionId: Int, bundle: Bundle? = null) {
//        if (fragmentId == findNavController().currentDestination?.id) {
//            lifecycleScope.launch {
//                withStarted {
//                    findNavController().navigate(actionId, bundle)
//                }
//            }
//        }
//    }
//
//    protected fun navigateToFragment(id: Int, action: NavDirections) {
//        if (findNavController().currentDestination?.id == id) {
//            lifecycleScope.launch {
//                withStarted {
//                    findNavController().navigate(action)
//                }
//            }
//        }
//    }
//
//    protected fun navigateBack(id: Int) {
//        if (findNavController().currentDestination?.id == id) {
//            lifecycleScope.launch {
//                withStarted {
//                    findNavController().popBackStack()
//                }
//            }
//        }
//    }

    //    protected fun navigateBackUsingActivityController() {
//        lifecycleScope.launch {
//            withStarted {
//                if (activity is MainActivity) {
//                    (activity as MainActivity).getNavController()?.popBackStack()
//                }
//            }
//        }
//    }


}