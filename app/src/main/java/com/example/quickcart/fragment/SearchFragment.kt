package com.example.quickcart.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quickcart.utils.CartListener
import com.example.quickcart.R
import com.example.quickcart.utils.Utils
import com.example.quickcart.adapters.AdapterProduct
import com.example.quickcart.databinding.FragmentSearchBinding
import com.example.quickcart.databinding.ItemViewProductsBinding
import com.example.quickcart.models.Product
import com.example.quickcart.roomdb.CartProducts
import com.example.quickcart.viewmodel.UserViewmodel
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapterProduct: AdapterProduct
    private var cartListener: CartListener? = null
    private val viewModel: UserViewmodel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(layoutInflater)
        getAllTheProducts()

        backToHomeFragment()
        searchProducts()
        return binding.root
    }

    private fun searchProducts() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = s.toString().trim()
                adapterProduct.getFilter().filter(query)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun backToHomeFragment() {
        binding.backButton.setOnClickListener {

            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
    }


    private fun getAllTheProducts() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {

            viewModel.fetchAllTheProducts().collect {

                if (it.isEmpty()) {
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                } else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }

                adapterProduct = AdapterProduct(
                    ::onAddButtonClick,
                    ::onIncrementButtonClicked,
                    ::onDecrementButtonClicked
                )
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList = it as ArrayList<Product>
                binding.shimmerViewContainer.visibility = View.GONE

            }
        }
    }


    private fun onAddButtonClick(product: Product, productsBinding: ItemViewProductsBinding) {
        productsBinding.tvAdd.visibility = View.GONE
        productsBinding.llProductCount.visibility = View.VISIBLE

        var itemCount = productsBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productsBinding.tvProductCount.text = itemCount.toString()

        cartListener?.showCartLayout(1)

        product.itemCount = itemCount

        lifecycleScope.launch {
            ///saving item in shared pref
            cartListener?.savingCartItemCount(1)
            //saving item in room db
            savedProductInRoomDb(product)
            viewModel.updateItemCount(product,itemCount)

        }


    }

    private fun savedProductInRoomDb(product: Product) {

        val cartProducts = CartProducts(
            productId = product.productRandomId!!,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = "₹${product.productPrice}",
            productCount = product.itemCount,
            productStock = product.productStock,
            productImages = product.productImagesUris?.get(0)!!,
            productCategory = product.productCategory,
            adminUid = product.adminUid,
            productType = product.productType
        )

        lifecycleScope.launch {
            viewModel.insertCartProducts(cartProducts)
        }

    }

    private fun onIncrementButtonClicked(
        product: Product,
        productsBinding: ItemViewProductsBinding
    ) {
        var itemCountIncrement = productsBinding.tvProductCount.text.toString().toInt()
        itemCountIncrement++

        if(product.productStock!!+1>itemCountIncrement){
            productsBinding.tvProductCount.text = itemCountIncrement.toString()

            cartListener?.showCartLayout(1)

            product.itemCount = itemCountIncrement

            lifecycleScope.launch {
                ///saving item in shared pref
                cartListener?.savingCartItemCount(1)
                //saving item in room db
                savedProductInRoomDb(product)
                viewModel.updateItemCount(product,itemCountIncrement)


            }
        }else{
            Utils.showToast(requireContext(),"Can't add more item of this")
        }



    }

    @SuppressLint("SetTextI18n")
    private fun onDecrementButtonClicked(
        product: Product,
        productsBinding: ItemViewProductsBinding
    ) {
        var itemCountDecrement = productsBinding.tvProductCount.text.toString().toInt()

        itemCountDecrement--

        product.itemCount = itemCountDecrement

        lifecycleScope.launch {
            ///saving item in shared pref
            cartListener?.savingCartItemCount(-1)
            //saving item in room db
            savedProductInRoomDb(product)
            viewModel.updateItemCount(product,itemCountDecrement)


        }
        if (itemCountDecrement > 0) {

            productsBinding.tvProductCount.text = itemCountDecrement.toString()

        } else {
            lifecycleScope.launch {
                viewModel.deleteCartProduct(product.productRandomId!!)
            }
            productsBinding.tvAdd.visibility = View.VISIBLE
            productsBinding.llProductCount.visibility = View.GONE
            productsBinding.tvProductCount.text = "0"
        }
        cartListener?.showCartLayout(-1)


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CartListener) {
            cartListener = context
        } else {
            throw ClassCastException("please implement cart listener")
        }
    }


}