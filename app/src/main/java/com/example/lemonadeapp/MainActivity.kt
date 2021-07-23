package com.example.lemonadeapp

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    // 状態を表す定数
    private val LEMONADE_STATE = "LEMONADE_STATE"
    private val LEMON_SIZE = "LEMON_SIZE"
    private val SQUEEZE_COUNT = "SQEEZE_COUNT"
    private val SELECT ="select"
    private val SQUEEZE = "squeeze"
    private val DRINK = "drink"
    private val RESTART = "restart"

    // レモネードの状態(デフォルトはselect)
    private var lemonadeState = "select"
    // レモンのサイズ(デフォルトは-1)
    private var lemonSize = -1
    // レモンを絞った回数(デフォルトは0)
    private var squeezeCount = 0

    private var lemonTree = LemonTree()
    private var lemonImage: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // アプリStateの取得
        if (savedInstanceState != null) {
            lemonadeState = savedInstanceState.getString(LEMONADE_STATE, "select")
            lemonSize = savedInstanceState.getInt(LEMON_SIZE, -1)
            squeezeCount = savedInstanceState.getInt(SQUEEZE_COUNT, -1)
        }

        lemonImage = findViewById(R.id.lemon_image)
        setViewElements()
        // レモンの画像をタップしたとき
        lemonImage!!.setOnClickListener {
            clickLemonImage()
        }

        // レモンの画像を長押したとき
        lemonImage!!.setOnLongClickListener {
            showSnackbar()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // stateの更新
        outState.putString(LEMONADE_STATE, lemonadeState)
        outState.putInt(LEMON_SIZE, lemonSize)
        outState.putInt(SQUEEZE_COUNT, squeezeCount)
        super.onSaveInstanceState(outState)
    }

    /**
     * レモン画像をクリックしたときの挙動
     */
    private fun clickLemonImage() {
        when (lemonadeState) {
            SELECT -> {
                // レモンサイズをランダム設定
                lemonSize = lemonTree.pick()
                // 絞り回数を初期化
                squeezeCount = 0
                // 状態を更新
                lemonadeState = SQUEEZE
            }
            SQUEEZE -> {
                // 絞り回数とレモンサイズを更新
                squeezeCount += 1
                lemonSize -= 1

                // 絞り終えたら状態を更新
                if (lemonSize == 0) {
                    lemonadeState = DRINK
                }
            }
            DRINK -> {
                // レモンサイズを初期化
                lemonSize = -1
                // 状態を更新
                lemonadeState = RESTART
            }
            RESTART -> {
                // 状態を更新
                lemonadeState = SELECT
            }
        }

        // Viewの更新
        setViewElements()
    }

    /**
     * 画像、テキストの更新
     */
    private fun setViewElements() {
        val actionTextView: TextView = findViewById(R.id.action_text)

        var actionTextId = 0
        var lemonImageId = 0

        Log.d("### setViewElements", "lemonadeState: ${lemonadeState}")

        // 状態に応じて画像、テキストのIDをセット
        when (lemonadeState) {
            SELECT -> {
                actionTextId = R.string.lemon_select
                lemonImageId = R.drawable.lemon_tree
            }
            SQUEEZE -> {
                actionTextId = R.string.lemon_squeeze
                lemonImageId = R.drawable.lemon_squeeze
            }
            DRINK -> {
                actionTextId = R.string.lemon_drink
                lemonImageId = R.drawable.lemon_drink
            }
            RESTART -> {
                actionTextId = R.string.lemon_empty_glass
                lemonImageId = R.drawable.lemon_restart
            }
        }

        // テキスト、画像をセット
        actionTextView.setText(actionTextId)
        lemonImage!!.setImageResource(lemonImageId)
    }

    /**
     * スナックバーでの残り絞り回数表示
     */
    private fun showSnackbar(): Boolean {
        // 絞り中でないときは何もしない
        if (lemonadeState != SQUEEZE) {
            return false
        }

        // スナックバーでテキストを表示
        // テキストであと何回絞ればいいかを通知
        val squeezeText = getString(R.string.squeeze_count, squeezeCount)
        Snackbar.make(
            findViewById(R.id.constraint_layout),
            squeezeText,
            Snackbar.LENGTH_SHORT
        ).show()
        return true
    }
}

class LemonTree {
    fun pick(): Int {
        return (2..4).random()
    }
}