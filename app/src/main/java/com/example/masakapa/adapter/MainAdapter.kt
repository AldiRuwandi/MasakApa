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
import com.example.masakapa.activities.MainActivity
import com.example.masakapa.model.ModelMain

class MainAdapter(private val mContext: Context, private val items: MutableList<ModelMain>, private val onSelectData: MainActivity) : RecyclerView.Adapter<MainAdapter.ViewHolder>(), Filterable {
    var modelMainFilterList: List<ModelMain> = ArrayList(items)
    interface onSelectDatas {
        fun onSelected(modelMain: ModelMain?)
    }

    override fun getFilter(): Filter {
        return modelFilter
    }

    private val modelFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: MutableList<ModelMain> = ArrayList()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(modelMainFilterList)
            } else {
                val filterPattern = constraint.toString().lowercase()
                for (modelMainFilter in modelMainFilterList) {
                    if (modelMainFilter.strCategory?.lowercase()!!.contains(filterPattern) || modelMainFilter.strCategoryDescription?.lowercase()
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
            items.addAll(results?.values as List<ModelMain>)
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvKategori: TextView
        var cvKategori: CardView
        var imgKategori: ImageView

        init {
            cvKategori = itemView.findViewById(R.id.cvKategori)
            tvKategori = itemView.findViewById(R.id.tvKategori)
            imgKategori = itemView.findViewById(R.id.imgKategori)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_list_kategori, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]

        //Get Image
        Glide.with(mContext)
            .load(data.strCategoryThumb)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.imgKategori)
        holder.tvKategori.text = data.strCategory
        holder.cvKategori.setOnClickListener { onSelectData.onSelected(data) }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}