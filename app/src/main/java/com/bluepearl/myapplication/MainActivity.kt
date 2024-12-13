package com.bluepearl.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.bluepearl.myapplication.ui.theme.MyApplicationTheme
import com.bluepearl.myapplication.viewmodels.RazorpayVModel
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener


class MainActivity : ComponentActivity(), PaymentResultWithDataListener, ExternalWalletListener {

    private val razorpayVModel: RazorpayVModel by viewModels()


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Razorpay
        Checkout.preload(applicationContext)

        setContent {
            MyApplicationTheme {

                val navController  = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(stringResource(R.string.app_name))
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                ) { innerPadding ->

                    NavGraph(innerPadding, navController, razorpayVModel)

                }
            }
        }
    }

    override fun onExternalWalletSelected(paymentId: String?, paymentData: PaymentData?) {

        try {

            Log.d("onExternalWalletSelected", paymentId.toString())
            Log.d("onExternalWalletSelected", paymentData.toString())
            Toast.makeText(this, "Payment Successful: $paymentId", Toast.LENGTH_LONG).show()

            razorpayVModel.razorpayPaymentFailure(paymentId, paymentData)

        } catch (e: Exception) {

            Log.e("onPaymentError", "Exception while handling payment error: ${e.message}")
            Toast.makeText(this, "An error occurred while processing the payment failure.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPaymentSuccess(paymentId: String?, paymentData: PaymentData?) {

        try {

            razorpayVModel.razorpayPaymentSuccess(paymentId, paymentData)

        } catch (e: Exception) {

            razorpayVModel.razorpayPaymentFailure(e.message.toString(),  null)

        }
    }

    override fun onPaymentError(errorCode: Int, errorMessage: String?, paymentData: PaymentData?) {
        try {

            razorpayVModel.razorpayPaymentFailure(errorMessage, paymentData)

        } catch (e: Exception) {

            razorpayVModel.razorpayPaymentFailure(e.message, null)

        }
    }

}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {

    }
}
