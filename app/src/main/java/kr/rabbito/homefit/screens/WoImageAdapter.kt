package kr.rabbito.homefit.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kr.rabbito.homefit.R

class WoImageAdapter(private val context: Context, private val imageList: Array<Int>) :
    RecyclerView.Adapter<WoImageAdapter.ViewHolder>() {

    private val woImages = mutableListOf(
        R.drawable.temp_push_up_tile,
        R.drawable.temp_pull_up_tile,
        R.drawable.temp_squat_tile,
        R.drawable.temp_side_lateral_raise_tile,
        R.drawable.temp_dumbbell_curl_tile,
        R.drawable.temp_leg_raise_tile
    )

    var hiddenItems = mutableSetOf<Int>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.gridview_imageview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.gridview_wo_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(woImages[position])

        holder.imageView.setOnClickListener {
            val intent = Intent(context, WODetailActivity::class.java)
            when (woImages[position]) {
                imageList[0] -> {
                    Log.d("gv test","push up")
                    intent.putExtra("workoutIndex",0)
                }
                imageList[1] -> {
                    Log.d("gv test","chin up")
                    intent.putExtra("workoutIndex",1)
                }
                imageList[2] -> {
                    Log.d("gv test", "squat")
                    intent.putExtra("workoutIndex",2)
                }
                imageList[3] -> {
                    Log.d("gv test", "side lateral raise")
                    intent.putExtra("workoutIndex",3)
                }
                imageList[4] -> {
                    Log.d("gv test","dumbbell curl")
                    intent.putExtra("workoutIndex",4)
                }
                imageList[5] -> {
                    Log.d("gv test", "leg raise")
                    intent.putExtra("workoutIndex",5)
                }
            }

            context.startActivity(intent)
            (context as Activity).finish()
        }

        if (hiddenItems.contains(position)) {
            holder.itemView.visibility = View.GONE
        } else {
            holder.itemView.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = woImages.size

    fun removeItem() {
        woImages.clear()
        notifyDataSetChanged()
    }

    fun showItem(position: Array<Int>) {
        for (i in position.indices) {
            woImages.add(position[i])
        }
        notifyDataSetChanged()
    }
}
