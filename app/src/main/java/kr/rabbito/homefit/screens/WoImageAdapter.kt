package kr.rabbito.homefit.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import kr.rabbito.homefit.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class WoImageAdapter(private val context: Context, private val imageList: Array<Int>) : BaseAdapter(){

    private val woImages = mutableListOf(
        R.drawable.temp_push_up_tile,
        R.drawable.temp_pull_up_tile,
        R.drawable.temp_squat_tile,
        R.drawable.temp_side_lateral_raise_tile,
        R.drawable.temp_dumbbell_curl_tile,
        R.drawable.temp_leg_raise_tile
    )

    var hiddenItems = mutableSetOf<Int>()

    override fun getCount(): Int = woImages.size

    override fun getItem(position: Int): Any = woImages[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var imageView = convertView

        if(convertView == null){
//            imageView = ImageView(context)
//            imageView.run{
//                layoutParams = ViewGroup.LayoutParams(350,350)
//                scaleType = ImageView.ScaleType.FIT_CENTER
//                setPadding(10,10,10,10)
            imageView = LayoutInflater.from(parent?.context).inflate(R.layout.gridview_wo_item,parent,false)
            }
//        }else{
//        var imageView = convertView as ImageView
//    }
        imageView?.findViewById<ImageView>(R.id.gridview_imageview)?.setImageResource(woImages[position])
        imageView?.setOnClickListener{
            val intent = Intent(context, WODetailActivity::class.java)
            when (woImages[position]){
                imageList[0] -> {
                    Log.d("gv test","push up")
                    intent.putExtra("workoutIndex",0)
                    context.startActivity(intent)
                }
                imageList[1] -> {
                    Log.d("gv test","chin up")
                    intent.putExtra("workoutIndex",1)
                    context.startActivity(intent)
                }
                imageList[2] -> {
                    Log.d("gv test", "squat")
                    intent.putExtra("workoutIndex",2)
                    context.startActivity(intent)
                }
                imageList[3] -> {
                    Log.d("gv test", "side lateral raise")
                    intent.putExtra("workoutIndex",3)
                    context.startActivity(intent)
                }
                imageList[4] -> {
                    Log.d("gv test","dumbbell curl")
                    intent.putExtra("workoutIndex",4)
                    context.startActivity(intent)
                }
                imageList[5] -> {
                    Log.d("gv test", "leg raise")
                    intent.putExtra("workoutIndex",5)
                    context.startActivity(intent)
                }
            }
        }
//        if(hiddenItems.contains(position)){
//            imageView?.visibility = View.GONE
//            notifyDataSetChanged()
//        }else{
//            imageView?.visibility = View.VISIBLE
//        }

        return imageView
    }
    fun removeItem(){
        woImages.clear()
        notifyDataSetChanged()
    }
    fun showItem(position: Array<Int>){
//        for(i in position.iterator()){
//            if (backup[i] != null){
//                woImages.add(i, backup[i]!!)
//                backup.removeAt(i)
//            }else{
//                woImages.add(i, R.drawable.main_iv_tag_lg)
//            }
//        }
        for(i in position.indices) {
            woImages.add(position[i])
        }
        notifyDataSetChanged()
    }
}