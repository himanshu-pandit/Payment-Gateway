package com.bluepearl.myapplication.viewmodels

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluepearl.myapplication.BuildConfig
import com.bluepearl.myapplication.PaymentResponse
import com.bluepearl.myapplication.Utils
import com.razorpay.Checkout
import com.razorpay.PaymentData
import org.json.JSONObject

class RazorpayVModel : ViewModel() {

    private val _razorpayResponse = MutableLiveData<PaymentResponse<Any>>()
    val razorpayResponse : LiveData<PaymentResponse<Any>> get() = _razorpayResponse

    private val _contact = MutableLiveData<String>("")
    val contact : LiveData<String> get() = _contact

    private val _email = MutableLiveData<String>("")
    val email : LiveData<String> get() = _email

    private val _amount = MutableLiveData<Double>(0.0)
    val amount : LiveData<Double> get() = _amount

    private val _alert = MutableLiveData<Boolean>(true)
    val alert : LiveData<Boolean> get() = _alert

    fun updateContact(contact: String){
        _contact.value = contact
    }

    fun updateEmail(email: String){
        _email.value = email
    }

    fun updateAmount(amount: Double){
        _amount.value = amount
    }

    fun updateAlert(alert: Boolean){
        println("updateAlert$alert")
        _alert.value = alert
    }

    fun startRazorpayPayment(activity: Activity, contact: String, email: String, amount: Double) {

        //validate inputs
        if (contact.isBlank() || email.isBlank() || contact <= 0.toString()){
            _razorpayResponse.postValue(
                PaymentResponse.Failure(
                    null,
                    "Invalid input data. Check contact, email, or amount."
                )
            )
            return
        }

        val checkout = Checkout()
        checkout.setKeyID(Utils.RAZORPAY_KEY)

        try {
            // Set payment options
            val options = JSONObject()
            options.put("name", "Razor Paytm Icici")
            options.put("description", "Razor pay implementation")
            options.put("image", Utils.RAZORPAY_LOGO)
            options.put("theme.color", "#3399cc")
            options.put("currency", "INR")
            options.put("order_id", "order_PWdTjdToMeJKf");
            options.put("amount", amount*100.00)  // Amount in paise (i.e., Rs 500.00)

            //Add option for retry
            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            // Add prefill details (optional)
            val prefill = JSONObject()
            prefill.put("email", email)
            prefill.put("contact", "+91$contact")
            options.put("prefill", prefill)

            Log.d("options", options.toString())

            _razorpayResponse.postValue(PaymentResponse.Loading())

            // Open the checkout activity
            checkout.open(activity, options)
        } catch (e: Exception) {
            _razorpayResponse.postValue(PaymentResponse.Failure(data = null, message = "Error in payment $e.message"))
            e.printStackTrace()
        }
    }

    fun razorpayPaymentSuccess(paymentId: String?, paymentData: PaymentData?){
        _razorpayResponse.postValue(PaymentResponse.Success(data = paymentData, message = "Payment successful! Thank you for your purchase."))
    }

    fun razorpayPaymentFailure(paymentMessage: String?, paymentData: PaymentData?){
        _razorpayResponse.postValue(PaymentResponse.Failure(data = paymentData, message = "Payment failed. Please try again later."))
    }

}