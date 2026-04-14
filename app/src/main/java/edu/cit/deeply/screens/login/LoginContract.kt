package edu.cit.deeply.screens.login

interface LoginContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun navigateToDashboard()
        fun showError(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onContinueClicked(email: String, password: String, isSignUp: Boolean)
        fun onGoogleSignInClicked()
    }
}
