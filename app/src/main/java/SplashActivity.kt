package com.example.dailyhabittracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class SplashActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnGetStarted: View

    private val images = listOf(
        R.drawable.screen_1_daily_habit_tracker,
        R.drawable.screen_2_build_better_habits,
        R.drawable.screen_3_stay_on_track
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        viewPager = findViewById(R.id.viewPager)
        btnGetStarted = findViewById(R.id.btnGetStarted)

        viewPager.adapter = SplashPagerAdapter(images)

        viewPager.setCurrentItem(0, false)

        btnGetStarted.setOnClickListener {

            when (viewPager.currentItem) {

                0 -> {
                    viewPager.setCurrentItem(1, true)
                }

                1 -> {
                    viewPager.setCurrentItem(2, true)
                }

                2 -> {
                    startActivity(
                        Intent(
                            this,
                            MainActivity::class.java
                        )
                    )

                    finish()
                }
            }
        }
    }
}

class SplashPagerAdapter(
    private val images: List<Int>
) : RecyclerView.Adapter<SplashPagerAdapter.SplashViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SplashViewHolder {

        val imageView = ImageView(parent.context)

        imageView.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

        imageView.scaleType =
            ImageView.ScaleType.CENTER_CROP

        return SplashViewHolder(imageView)
    }

    override fun onBindViewHolder(
        holder: SplashViewHolder,
        position: Int
    ) {
        holder.imageView.setImageResource(
            images[position]
        )
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class SplashViewHolder(
        val imageView: ImageView
    ) : RecyclerView.ViewHolder(imageView)
}