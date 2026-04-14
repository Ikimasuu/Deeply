package edu.cit.deeply.screens.profile

import android.util.Log
import edu.cit.deeply.data.repositories.AuthRepository

class ProfilePresenter : ProfileContract.Presenter {

    private var view: ProfileContract.View? = null

    override fun attachView(view: ProfileContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun onScreenOpened() {
        val user = AuthRepository.getCurrentUser()
        view?.displayUser(user)
    }

    override fun onSignOutClicked() {
        AuthRepository.signOut()
        view?.navigateToLogin()
    }

    override fun onRowClicked(rowId: String) {
        Log.d("ProfilePresenter", "Row clicked: $rowId")
    }

    override fun onToggleChanged(rowId: String, isChecked: Boolean) {
        Log.d("ProfilePresenter", "Toggle changed: $rowId = $isChecked")
    }
}
