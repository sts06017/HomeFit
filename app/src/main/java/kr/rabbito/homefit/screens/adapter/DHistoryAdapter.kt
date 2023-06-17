package kr.rabbito.homefit.screens.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.rabbito.homefit.data.Diet
import kr.rabbito.homefit.databinding.DreportItemBinding
import kr.rabbito.homefit.screens.viewHolder.DHistoryViewHolder
import kr.rabbito.homefit.screens.viewHolder.DReportViewHolder

class DHistoryAdapter(private val results: List<Diet>):
    RecyclerView.Adapter<DHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DHistoryViewHolder {
        val binding = DreportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return DHistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return results.size
    }

    override fun onBindViewHolder(holder: DHistoryViewHolder, position: Int) {
//        holder.bind(keyList[position], results)
        holder.bind(results[position])
    }
}
