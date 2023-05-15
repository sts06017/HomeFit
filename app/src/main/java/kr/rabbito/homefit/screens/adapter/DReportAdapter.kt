package kr.rabbito.homefit.screens.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.rabbito.homefit.databinding.DreportItemBinding
import kr.rabbito.homefit.screens.viewHolder.DReportViewHolder

class DReportAdapter(private val results: Map<String, Any>):
    RecyclerView.Adapter<DReportViewHolder>() {

    private val keyList = results.keys.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DReportViewHolder {
        val binding = DreportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return DReportViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return keyList.size
    }

    override fun onBindViewHolder(holder: DReportViewHolder, position: Int) {
        holder.bind(keyList[position], results)
    }
}