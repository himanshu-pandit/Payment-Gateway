package com.bluepearl.myapplication.screens

import android.app.Activity
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bluepearl.myapplication.PaymentResponse
import com.bluepearl.myapplication.R
import com.bluepearl.myapplication.component.AlertBox
import com.bluepearl.myapplication.viewmodels.RazorpayVModel

@Composable
fun RazorPay(innerPadding: PaddingValues, razorpayVModel: RazorpayVModel) {

    val context = LocalContext.current as Activity

    val amount by razorpayVModel.amount.observeAsState(0.0)

    val contact by razorpayVModel.contact.observeAsState("")

    val email by razorpayVModel.email.observeAsState("")

    val alert by razorpayVModel.alert.observeAsState(true)

    val razorpayResponse by razorpayVModel.razorpayResponse.observeAsState()


    Column (
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){

        razorpayResponse?.let { response ->
            Log.d("razorpayResponse", response.message.toString() + response.data.toString())
            when(response){
                is PaymentResponse.Loading -> {
                    Toast.makeText(context, "Payment in progress...", Toast.LENGTH_LONG).show()
                }

                is PaymentResponse.Success -> {
                    if(alert){
                        AlertBox(
                            onConfirmation = {
                                razorpayVModel.updateAlert(false)
                            },
                            onDismissRequest = {
                                razorpayVModel.updateAlert(false)
                            },
                            dialogTitle = "Success!",
                            dialogText = response.message.toString(),
                            icon = Icons.Default.Warning
                        )
                    }
                }

                is PaymentResponse.Failure -> {
                    if(alert) {
                        AlertBox(
                            onConfirmation = {
                                razorpayVModel.updateAlert(false)
                            },
                            onDismissRequest = {
                                razorpayVModel.updateAlert(false)
                            },
                            dialogTitle = "Failure!",
                            dialogText = response.message.toString(),
                            icon = Icons.Default.Done
                        )
                    }
                }
            }
        }

        Image(
            painter = painterResource(R.drawable.razorpay),
            contentDescription = "Razorpay image",
            modifier = Modifier.width(200.dp).padding(16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                razorpayVModel.updateEmail(it)
            },
            label = {
                Text(stringResource(R.string.enter_email_address))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = {
                Text(stringResource(R.string.example_com))
            },
            textStyle = LocalTextStyle.current,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            singleLine = true,
        )

        OutlinedTextField(
            value = contact,
            onValueChange = {
                razorpayVModel.updateContact(it)
            },
            label = {
                Text(stringResource(R.string.enter_contact_number))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = {
                Text(stringResource(R.string._0000000000))
            },
            textStyle = LocalTextStyle.current,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
            ),
            singleLine = true,
        )

        OutlinedTextField(
            value = if (amount  == 0.0) "" else amount.toString(),
            onValueChange = {
                if (it.isBlank()){
                    razorpayVModel.updateAmount(0.0)
                }else{
                    razorpayVModel.updateAmount(it.toDoubleOrNull() ?: 0.0)
                }
            },
            label = { Text(stringResource(R.string.enter_amount)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = {
                Text(stringResource(R.string._100))
            },
            textStyle = LocalTextStyle.current,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isValidEmail(email) && isValidContact(contact) && isValidAmount(amount.toString())){
                        razorpayVModel.startRazorpayPayment(
                            activity = context,
                            contact = contact,
                            email = email,
                            amount = amount
                        )
                    }
                }
            ),
            singleLine = true,
        )

        Button(
            onClick = {
                if (isValidEmail(email) && isValidContact(contact) && isValidAmount(amount.toString())){
                    razorpayVModel.startRazorpayPayment(
                        activity = context,
                        contact = contact,
                        email = email,
                        amount = amount
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(stringResource(R.string.pay_amount), modifier = Modifier.padding(8.dp), fontSize = 16.sp)
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Icon",
                tint = Color.White,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}


fun isValidAmount(amount: String): Boolean {
    return if (amount.isEmpty()){
        false
    }else{
        amount.toDoubleOrNull()?.let { it > 0 } == true
    }
}

fun isValidContact(contact: String): Boolean {
    return if (TextUtils.isEmpty(contact)){
        false
    }else{
        Patterns.PHONE.matcher(contact).matches()
    }
}

fun isValidEmail(email: String): Boolean {
    return if (TextUtils.isEmpty(email)){
        false
    }else{
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

