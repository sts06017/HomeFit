package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.rabbito.homefit.data.User
import kr.rabbito.homefit.data.UserDB
import kr.rabbito.homefit.databinding.ActivityInitBinding

class InitActivity : AppCompatActivity() {
    private var mBinding: ActivityInitBinding? = null
    private val binding get() = mBinding!!

    private var userDB: UserDB? = null
    private var userId: Long? = null

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityInitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = UserDB.getInstance(this)

        userId = 0L  // 임시
        loadUserById(userId!!)

        binding.initBtnRegister.setOnClickListener {
            val userName = binding.initEtName.text.toString()
            val height = binding.initEtHeight.text.toString().toInt()
            val weight = binding.initEtWeight.text.toString().toInt()

            if (user != null) {
                val newUser = User(0, userName, height, weight)
                insertUser(newUser)
            } else {
                updateUserById(userId!!, userName, height, weight)
            }

            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun loadUserById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val users = userDB?.userDAO()?.getUserById(id)
            if (users!!.isNotEmpty()) {
                user = users[0]
            }

            if (user != null) {
                initEditTextView(user!!)
            }
        }
    }

    private fun insertUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            userDB?.userDAO()?.insert(user)
        }
    }

    private fun updateUserById(id: Long, userName: String, height: Int, weight: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            userDB?.userDAO()?.updateUserById(id, userName, height, weight)
        }
    }

    private fun initEditTextView(user: User) {
        binding.initEtName.setText(user.userName)
        binding.initEtHeight.setText(user.height.toString())
        binding.initEtWeight.setText(user.weight.toString())
    }
}