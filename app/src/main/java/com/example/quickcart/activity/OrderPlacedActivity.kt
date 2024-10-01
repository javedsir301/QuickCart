package com.example.quickcart.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.quickcart.R
import com.example.quickcart.utils.Utils
import com.example.quickcart.adapters.AdapterCartProducts
import com.example.quickcart.databinding.ActivityOrderPlacedBinding
import com.example.quickcart.databinding.AddressLayoutBinding
import com.example.quickcart.viewmodel.UserViewmodel
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class OrderPlacedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderPlacedBinding
    private val viewModel: UserViewmodel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlacedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllProducts()
        setStatusBarColor()
        backToMainActivity()
        onPlaceOrderClicked()

    }


    private fun onPlaceOrderClicked() {
        binding.btnNext.setOnClickListener {
            viewModel.getAddressStatus().observe(this) { status ->
                if (status) {
                    startActivity(Intent(this, PaymentActivity::class.java))
                    finish()
                } else {
                    val addressLayoutBinding =
                        AddressLayoutBinding.inflate(LayoutInflater.from(this))

                    val alertDialog = AlertDialog.Builder(this).setView(addressLayoutBinding.root)
                        .create()
                    alertDialog.show()

                    addressLayoutBinding.btnAddAddress.setOnClickListener {
                        saveAddress(alertDialog, addressLayoutBinding)
                    }
                }
            }
        }
    }

    private fun saveAddress(alertDialog: AlertDialog?, addressLayoutBinding: AddressLayoutBinding) {
        Utils.showDialog(this, "Processing....")
        val userPinCode = addressLayoutBinding.etPinCode.text.toString()
        val userPhoneNumber = addressLayoutBinding.etPhoneNo.text.toString()
        val userState = addressLayoutBinding.etState.text.toString()
        val userDistrict = addressLayoutBinding.etDistrict.text.toString()
        val userAddress = addressLayoutBinding.etAddress.text.toString()

        val address = "$userPinCode, $userDistrict($userState), $userAddress, $userPhoneNumber"


        lifecycleScope.launch {
            viewModel.saveUserAddress(address)
            viewModel.saveAddressStatus()
        }

        alertDialog?.dismiss()
        Utils.hideDialog()
    }

    private fun backToMainActivity() {
        binding.tbOrderFragment.setOnClickListener {
            startActivity(Intent(this, UsersMainActivity::class.java))
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getAllProducts() {
        viewModel.getAll().observe(this) { cartProductsList ->
            adapterCartProducts = AdapterCartProducts()
            binding.rvProductsItem.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductsList)

            var totalPrice = 0

            for (products in cartProductsList) {
                val price = products.productPrice?.substring(1)?.toInt()
                val itemCount = products.productCount!!
                totalPrice += price!! * itemCount
            }

            binding.tvSubTotal.text = "₹$totalPrice"

            if (totalPrice < 200) {
                binding.tvDeliveryCharge.text = "₹40"
                totalPrice += 40
            }

            binding.tvFinalTotal.text = "₹$totalPrice"

        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors =
                ContextCompat.getColor(this@OrderPlacedActivity, R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}