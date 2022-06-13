package com.example.masakapa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.masakapa.R
import com.example.masakapa.activities.FilterActivity
import com.example.masakapa.model.ModelFilter
import com.example.masakapa.model.ModelMain

class FilterMakananAdapter(private val context: Context ,private val items: MutableList<ModelFilter>, private val onSelectData: FilterActivity) : RecyclerView.Adapter<FilterMakananAdapter.ViewHolder>(), Filterable {

    var modelFilterMakanan: List<ModelFilter> = ArrayList(items)

    interface onSelectDatas {
        fun onSelected(modelMain: ModelFilter?)
    }

    override fun getFilter(): Filter {
        return modelFilterList
    }

    private val modelFilterList: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: MutableList<ModelFilter> = ArrayList()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(modelFilterMakanan)
            } else {
                val filterPattern = constraint.toString().lowercase()
                for (modelMainFilter in modelFilterMakanan) {
                    if (modelMainFilter.strMeal?.lowercase()!!.contains(filterPattern) || modelMainFilter.strMealThumb?.lowercase()
                        !!.contains(filterPattern)
                    ) {
                        filteredList.add(modelMainFilter)
                    }
                }
            }
            val result = FilterResults()
            result.values = filteredList
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            items.clear()
            items.addAll(results?.values as List<ModelFilter>)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_list_filter, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]
        Glide.with(context)
            .load(data.strMealThumb)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.imgThumb)
        holder.tvMeal.text = data.strMeal
        holder.cvFilterMeal.setOnClickListener { onSelectData.onSelected(data) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvMeal: TextView
        var cvFilterMeal: CardView
        var imgThumb: ImageView

        init {
            cvFilterMeal = itemView.findViewById(R.id.cvFilterMeal)
            tvMeal = itemView.findViewById(R.id.tvMeal)
            imgThumb = itemView.findViewById(R.id.imgThumb)
        }
    }
}