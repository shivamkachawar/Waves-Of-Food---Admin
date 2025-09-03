package com.example.adminwavesoffood.Model

import android.os.Parcel
import android.os.Parcelable

class OrderDetails() : Parcelable {
    var userId: String? = null
    var userName: String? = null
    var foodNames: MutableList<String>? = null
    var foodImages: MutableList<String>? = null
    var foodPrices: MutableList<String>? = null
    var foodQuantities: MutableList<Int>? = null
    var address: String? = null
    var totalPrice: String? = null
    var phoneNumber: String? = null
    var orderAccepted: Boolean = false
    var paymentReceived: Boolean = false
    var itemPushKey: String? = null
    var currentTime: Long = 0

    // Primary constructor with parameters
    constructor(
        userId: String? = null,
        userName: String? = null,
        foodNames: MutableList<String>? = null,
        foodImages: MutableList<String>? = null,
        foodPrices: MutableList<String>? = null,
        foodQuantities: MutableList<Int>? = null,
        address: String? = null,
        totalPrice: String? = null,
        phoneNumber: String? = null,
        orderAccepted: Boolean = false,
        paymentReceived: Boolean = false,
        itemPushKey: String? = null,
        currentTime: Long = 0
    ) : this() {
        this.userId = userId
        this.userName = userName
        this.foodNames = foodNames
        this.foodImages = foodImages
        this.foodPrices = foodPrices
        this.foodQuantities = foodQuantities
        this.address = address
        this.totalPrice = totalPrice
        this.phoneNumber = phoneNumber
        this.orderAccepted = orderAccepted
        this.paymentReceived = paymentReceived
        this.itemPushKey = itemPushKey
        this.currentTime = currentTime
    }

    // Parcel constructor - reads data from Parcel
    constructor(parcel: Parcel) : this() {
        userId = parcel.readString()
        userName = parcel.readString()
        foodNames = parcel.createStringArrayList()
        foodImages = parcel.createStringArrayList()
        foodPrices = parcel.createStringArrayList()
        foodQuantities = parcel.createIntArray()?.toMutableList()
        address = parcel.readString()
        totalPrice = parcel.readString()
        phoneNumber = parcel.readString()
        orderAccepted = parcel.readByte() != 0.toByte()
        paymentReceived = parcel.readByte() != 0.toByte()
        itemPushKey = parcel.readString()
        currentTime = parcel.readLong()
    }

    // Write object data to Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeStringList(foodNames)
        parcel.writeStringList(foodImages)
        parcel.writeStringList(foodPrices)
        parcel.writeIntArray(foodQuantities?.toIntArray())
        parcel.writeString(address)
        parcel.writeString(totalPrice)
        parcel.writeString(phoneNumber)
        parcel.writeByte(if (orderAccepted) 1 else 0)
        parcel.writeByte(if (paymentReceived) 1 else 0)
        parcel.writeString(itemPushKey)
        parcel.writeLong(currentTime)
    }

    // Describe contents (usually returns 0)
    override fun describeContents(): Int {
        return 0
    }

    // Creator object - required for Parcelable
    companion object CREATOR : Parcelable.Creator<OrderDetails> {
        override fun createFromParcel(parcel: Parcel): OrderDetails {
            return OrderDetails(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetails?> {
            return arrayOfNulls(size)
        }
    }
}