package edu.cit.deeply.screens.login

import edu.cit.deeply.data.repositories.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LoginPresenter : LoginContract.Presenter {

    private var view: LoginContract.View? = null
    private var scope: CoroutineScope? = null

    override fun attachView(view: LoginContract.View) {
        this.view = view
        scope = CoroutineScope(Dispatchers.Main + Job())
    }

    override fun detachView() {
        scope?.cancel()
        scope = null
        this.view = null
    }

    override fun onContinueClicked(email: String, password: String, isSignUp: Boolean) {
        if (email.isBlank() || password.isBlank()) {
            view?.showError("Please enter your email and password.")
            return
        }

        view?.showLoading()
        scope?.launch {
            val result = if (isSignUp) {
                AuthRepository.signUp(email, password)
            } else {
                AuthRepository.signIn(email, password)
            }
            view?.hideLoading()
            result.fold(
                onSuccess = {
                    // TODO: navigate to Dashboard once built
                    view?.navigateToDashboard()
                },
                onFailure = { e ->
                    view?.showError(e.message ?: "Authentication failed.")
                }
            )
        }
    }

    override fun onGoogleSignInClicked() {
        view?.showLoading()
        scope?.launch {
            val result = AuthRepository.signInWithGoogle()
            view?.hideLoading()
            result.fold(
                onSuccess = {
                    // TODO: navigate to Dashboard once built
                    view?.navigateToDashboard()
                },
                onFailure = { e ->
                    view?.showError(e.message ?: "Google sign-in failed.")
                }
            )
        }
    }
}
