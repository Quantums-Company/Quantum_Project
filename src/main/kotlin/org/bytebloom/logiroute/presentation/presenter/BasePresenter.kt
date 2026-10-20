package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bytebloom.logiroute.presentation.toUserMessage
import org.bytebloom.logiroute.presentation.screens.BaseScreen

/**
 * Base class of every presenter.
 *
 * A presenter receives its [view] (an interface) and drives it: loading indicator, results, errors.
 * It never knows if the view is a terminal, a Fragment or a Compose screen.
 *
 * Every user action ("intent") is a plain function that returns the [Job] doing the work:
 *  - a terminal UI calls `intent().join()` to wait for the result before asking the next question,
 *  - an Android UI simply ignores the returned Job.
 *
 * All work runs in a child of [parentScope] (the application scope on the terminal,
 * a ViewModel scope on Android); [close] cancels only this presenter's work.
 */
abstract class BasePresenter<V : BaseScreen>(
    protected val view: V,
    parentScope: CoroutineScope
) {
    private val supervisor = SupervisorJob(parentScope.coroutineContext[Job])
    protected val scope: CoroutineScope = CoroutineScope(parentScope.coroutineContext + supervisor)

    fun close() = supervisor.cancel()

    /**
     * Shows the loading indicator, runs [task], hides the indicator, and only then gives the
     * outcome to the view: [onSuccess] with the value, or `showErrorMessage` with a readable message.
     * Cancellation is never swallowed, so structured concurrency keeps working.
     */
    protected fun <T> perform(task: suspend () -> T, onSuccess: (T) -> Unit): Job = scope.launch {
        view.showLoadingIndicator(true)
        val attempt = attemptOf(task)
        view.showLoadingIndicator(false)

        when (attempt) {
            is Attempt.Success -> onSuccess(attempt.value)
            is Attempt.Failure -> view.showErrorMessage(attempt.error.toUserMessage())
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun <T> attemptOf(task: suspend () -> T): Attempt<T> =
        try {
            Attempt.Success(task())
        } catch (cancellation: CancellationException) {
            view.showLoadingIndicator(false)
            throw cancellation
        } catch (failure: Exception) {
            Attempt.Failure(failure)
        }

    private sealed interface Attempt<out T> {
        class Success<T>(val value: T) : Attempt<T>
        class Failure(val error: Exception) : Attempt<Nothing>
    }
}
