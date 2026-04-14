package edu.cit.deeply.screens.profile

import edu.cit.deeply.data.models.User

interface ProfileContract {

    interface View {
        fun displayUser(user: User)
        fun navigateToLogin()
        fun showError(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onScreenOpened()
        fun onSignOutClicked()
        fun onRowClicked(rowId: String)
        fun onToggleChanged(rowId: String, isChecked: Boolean)
    }
}
