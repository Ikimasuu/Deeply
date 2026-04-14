package edu.cit.deeply.screens.history

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import edu.cit.deeply.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity(), HistoryContract.View {

    private lateinit var binding: ActivityHistoryBinding
    private val presenter: HistoryContract.Presenter = HistoryPresenter()
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
        presenter.onScreenOpened()
    }

    override fun onStop() {
        presenter.detachView()
        super.onStop()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(
            onSessionClicked = { position -> presenter.onSessionClicked(position) }
        )
        binding.rvSessions.layoutManager = LinearLayoutManager(this)
        binding.rvSessions.adapter = adapter
    }

    override fun displaySessions(items: List<HistoryItem>) {
        binding.rvSessions.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
        adapter.submitList(items)
    }

    override fun showEmptyState() {
        binding.rvSessions.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
