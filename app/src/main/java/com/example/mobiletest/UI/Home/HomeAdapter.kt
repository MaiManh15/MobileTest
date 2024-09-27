package com.example.mobiletest.UI.Home

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiletest.Datas.Model.ItemModel
import com.example.mobiletest.R

class HomeAdapter(
    private var list: List<ItemModel>,
    private var searchKeyword: String
) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    private var dataChangeListener: DataChangeListener? = null

    lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClicked(item: ItemModel) {}
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    interface DataChangeListener {
        fun onDataChanged(filteredList: List<ItemModel>)
    }

    fun setDataChangeListener(listener: DataChangeListener) {
        dataChangeListener = listener
    }

    inner class HomeViewHolder(view: View, listener: onItemClickListener) : RecyclerView.ViewHolder(view) {
        val address: TextView
        val directBtn: ImageView

        init {
            address = view.findViewById(R.id.address_txt)
            directBtn = view.findViewById(R.id.img_btn)

            view.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = list[position]
                    listener.onItemClicked(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_address_model, parent, false)
        return HomeViewHolder(view, mListener)
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currItem = list[position]

        val addressBuilder = StringBuilder()
        currItem.address.label?.let { addressBuilder.append(" ").append(it) }
//        currItem.address.houseNumber?.let { addressBuilder.append(" ").append(it) }
//        currItem.address.street?.let { addressBuilder.append(" ").append(it) }
//        currItem.address.subdistrict?.let { addressBuilder.append(" ").append(it) }
//        currItem.address.district?.let { addressBuilder.append(" ").append(it) }
//        currItem.address.city?.let { addressBuilder.append(" ").append(it) }
//        currItem.address.state?.let { addressBuilder.append(" ").append(it) }
//        currItem.address.countryName?.let { addressBuilder.append(" ").append(it) }

        val fullAddress = addressBuilder.toString()

        val spannableAddress = SpannableString(fullAddress)

        if (searchKeyword.isNotEmpty()) {
            for (char in searchKeyword) {
                var startIndex = fullAddress.indexOf(char, ignoreCase = true)

                // Tìm kiếm và làm nổi bật tất cả các ký tự khớp với từ khóa
                while (startIndex != -1) {
                    spannableAddress.setSpan(
                        StyleSpan(Typeface.BOLD),
                        startIndex,
                        startIndex + 1, // Chỉ làm nổi bật ký tự đơn lẻ
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    startIndex = fullAddress.indexOf(char, startIndex + 1, ignoreCase = true) // Tìm kiếm ký tự tiếp theo
                }
            }
        }

        // Đặt chuỗi đã được làm nổi bật vào TextView của ViewHolder
        holder.address.text = spannableAddress
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<ItemModel>, searchInput: String) {
        list = newList
        searchKeyword = searchInput
        notifyDataSetChanged()
        dataChangeListener?.onDataChanged(newList)
    }

}