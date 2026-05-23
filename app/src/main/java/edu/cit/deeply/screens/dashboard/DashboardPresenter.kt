package edu.cit.deeply.screens.dashboard

import edu.cit.deeply.data.repositories.AuthRepository

class DashboardPresenter : DashboardContract.Presenter {

    private var view: DashboardContract.View? = null

    override fun attachView(view: DashboardContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun onScreenOpened() {
        val user = AuthRepository.getCurrentUser()
        view?.displayGreeting(user.name)
    }

    override fun onStartSessionClicked() {
        view?.navigateToPreSession()
    }

    override fun onHistoryClicked() {
        view?.navigateToHistory()
    }

    override fun onProfileClicked() {
        view?.navigateToProfile()
    }
}
