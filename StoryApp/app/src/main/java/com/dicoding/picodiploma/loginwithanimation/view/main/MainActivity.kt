package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.adapter.LoadingStateAdapter
import com.dicoding.picodiploma.loginwithanimation.adapter.StoryAdapter
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.home.HomeActivity
import com.dicoding.picodiploma.loginwithanimation.view.maps.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.upload.UpStoryActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        showRecyclerList()
    }

    private fun setupAction() {
        viewModel.getSession().observe(this) { session ->
            if (!session.isLogin) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                binding.progressBar.visibility = View.GONE
                getStoriesData()
            }
        }

        binding.toAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuLogout -> {
                    AlertDialog.Builder(this).apply {
                        setTitle(R.string.message)
                        setMessage(R.string.ask_logout)
                        setPositiveButton(R.string.yes) { _, _ ->
                            viewModel.logout()
                        }
                        setNegativeButton(R.string.no) { _, _ ->

                        }
                        create()
                        show()
                    }
                    true
                }

                R.id.menuMaps -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)

                    true
                }

                else -> false
            }
        }
        binding.fabCamera.setOnClickListener {
            val intent = Intent(this, UpStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showRecyclerList() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)
    }

    private fun getStoriesData() {
        val adapter = StoryAdapter()
        adapter.removeDivider(binding.rvStory)

        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        viewModel.getStories().observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.getSession()
    }
}