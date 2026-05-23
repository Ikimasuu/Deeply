package edu.cit.deeply.screens.dashboard

interface DashboardContract {
    interface View {
        fun displayGreeting(name: String)
        fun navigateToPreSession()
        fun navigateToHistory()
        fun navigateToProfile()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onScreenOpened()
        fun onStartSessionClicked()
        fun onHistoryClicked()
        fun onProfileClicked()
    }
}
