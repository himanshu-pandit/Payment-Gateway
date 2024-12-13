package com.bluepearl.myapplication

sealed class ScreenList(val route: String) {
    object Razorpay : ScreenList("razorpay")
    object Paytmpay : ScreenList("paytmpay")
    object Icicipay : ScreenList("icicipay")
}