package com.example.quickcart.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quickcart.R
import com.example.quickcart.databinding.ActivityPaymentBinding
import com.example.quickcart.models.Orders
import com.example.quickcart.roomdb.CartProducts
import com.example.quickcart.utils.CartListener
import com.example.quickcart.utils.Utils
import com.example.quickcart.viewmodel.UserViewmodel

@Suppress("DEPRECATION")
class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private val viewModel: UserViewmodel by viewModels()
    private val correctPin = "1234"
    private var cartListener: CartListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setStatusBarColor()
        backToMainActivity()

        binding.pinEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnConfirm.setBackgroundColor(
                    if (s != null && s.length == 4) {
                        resources.getColor(R.color.green)
                    } else {
                        resources.getColor(R.color.grayish_blue)
                    }
                )
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnConfirm.setOnClickListener {
            val enteredPin = binding.pinEditText.text.toString()

            if (enteredPin.length != 4) {
                Toast.makeText(this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show()
            } else if (enteredPin == correctPin) {
                processOrder()
            } else {
                Toast.makeText(this, "Please enter correct PIN", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processOrder() {
        viewModel.getAll().observe(this) { cartProductList ->
            if (cartProductList.isNotEmpty()) {
                viewModel.getUserAddress { address ->
                    val orders = Orders(
                        orderId = Utils.getRandomId(),
                        orderList = cartProductList,
                        userAddress = address,
                        orderStatus = 0,
                        orderDate = Utils.getCurrentDate(),
                        orderingUserId = Utils.getCurrentUserId()
                    )
                    saveOrder(orders, cartProductList)
                }
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveOrder(orders: Orders, cartProductList: List<CartProducts>) {
        viewModel.saveOrderProducts(orders)
        cartProductList.forEach { product ->
            val count = product.productCount
            val stock = product.productStock?.minus(count ?: 0)
            viewModel.saveProductsAfterOrder(stock!!, product)
        }
        Utils.showToast(this@PaymentActivity, "Payment Done")

        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
        viewModel.deleteCartProducts()
        viewModel.savingCartItemCount(0)
        cartListener?.hideCartLayout()

        Utils.hideDialog()
        startActivity(Intent(this, UsersMainActivity::class.java))
        finish()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@PaymentActivity, R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun backToMainActivity() {
        binding.tbPayment.setOnClickListener {
            startActivity(Intent(this, OrderPlacedActivity::class.java))
            finish()
        }
    }
}
