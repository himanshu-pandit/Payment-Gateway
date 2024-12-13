package com.bluepearl.myapplication

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bluepearl.myapplication.screens.IciciPay
import com.bluepearl.myapplication.screens.PaytmPay
import com.bluepearl.myapplication.screens.RazorPay
import com.bluepearl.myapplication.viewmodels.RazorpayVModel

@Composable
fun NavGraph(
    innerPadding: PaddingValues,
    navController: NavHostController,
    razorpayVModel: RazorpayVModel
) {

    NavHost(navController = navController, startDestination = ScreenList.Razorpay.route){

        composable(ScreenList.Razorpay.route) {
            RazorPay(innerPadding, razorpayVModel)
        }

        composable(ScreenList.Icicipay.route){
            IciciPay(innerPadding)
        }

        composable(ScreenList.Paytmpay.route){
            PaytmPay(innerPadding)
        }

    }

}