package com.krosskomics.login.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.krosskomics.R
import com.krosskomics.common.data.DataAge
import kotlinx.android.synthetic.main.item_spinner_age.view.*


class AgeSpinnerAdapter(val items: ArrayList<DataAge>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if(view == null) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.item_spinner_age, parent, false);
        }

        val item: DataAge = items[position]
        view?.apply {
            ageTextView.text = item.age_text
            if (position == 0 && item.isUpSelect) {
                upArrowImageView.isSelected = true
            } else if (position == count - 1 && item.isDownSelect) {
                downArrowImageView.isSelected = true
            } else {
                upArrowImageView.isSelected = false
                downArrowImageView.isSelected = false
            }
        }

        return view!!
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}