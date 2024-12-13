package com.bluepearl.myapplication

sealed class PaymentResponse<T>(val data: T? = null , val message: T? = null) {
    class Success<T>(data: T?, message: T?) : PaymentResponse<T>(data, message)
    class Failure<T>(data: T?, message: T?) : PaymentResponse<T>(data, message)
    class Loading<T>() : PaymentResponse<T>()
}