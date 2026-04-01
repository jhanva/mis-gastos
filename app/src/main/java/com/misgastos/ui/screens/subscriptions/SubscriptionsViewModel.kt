package com.misgastos.ui.screens.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misgastos.domain.model.PaymentMethod
import com.misgastos.domain.model.Subscription
import com.misgastos.domain.model.SubscriptionDraft
import com.misgastos.domain.repository.SubscriptionRepository
import com.misgastos.utils.parseAmountInputToCents
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SubscriptionsUiState(
    val subscriptions: List<Subscription> = emptyList(),
    val summary: SubscriptionSummary = SubscriptionSummary(),
)

sealed interface SubscriptionsEvent {
    data class Message(val value: String) : SubscriptionsEvent
    data class Saved(val value: String) : SubscriptionsEvent
    data class Deleted(val value: String) : SubscriptionsEvent
}

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    val uiState: StateFlow<SubscriptionsUiState> =
        subscriptionRepository.observeSubscriptions()
            .map { subscriptions ->
                SubscriptionsUiState(
                    subscriptions = subscriptions,
                    summary = buildSubscriptionSummary(subscriptions),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SubscriptionsUiState(),
            )

    private val _events = MutableSharedFlow<SubscriptionsEvent>()
    val events = _events.asSharedFlow()

    fun saveSubscription(
        subscriptionId: Long?,
        name: String,
        amountInput: String,
        billingDay: Int,
        paymentMethod: PaymentMethod,
    ) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _events.emit(SubscriptionsEvent.Message("La plataforma necesita un nombre"))
                return@launch
            }

            val amountInCents = parseAmountInputToCents(amountInput)
            if (amountInCents == null) {
                _events.emit(SubscriptionsEvent.Message("Ingresa un valor mensual valido"))
                return@launch
            }

            if (billingDay !in 1..31) {
                _events.emit(SubscriptionsEvent.Message("El dia de cobro debe estar entre 1 y 31"))
                return@launch
            }

            if (paymentMethod !in recurringPaymentMethods()) {
                _events.emit(SubscriptionsEvent.Message("El pago recurrente debe ir por debito o credito"))
                return@launch
            }

            subscriptionRepository.saveSubscription(
                SubscriptionDraft(
                    id = subscriptionId,
                    name = name,
                    monthlyAmountInCents = amountInCents,
                    billingDay = billingDay,
                    paymentMethod = paymentMethod,
                ),
            )
            _events.emit(SubscriptionsEvent.Saved("Plataforma guardada"))
        }
    }

    fun deleteSubscription(subscriptionId: Long) {
        viewModelScope.launch {
            subscriptionRepository.deleteSubscription(subscriptionId)
            _events.emit(SubscriptionsEvent.Deleted("Plataforma eliminada"))
        }
    }
}

internal fun recurringPaymentMethods(): List<PaymentMethod> {
    return listOf(
        PaymentMethod.DEBIT_CARD,
        PaymentMethod.CREDIT_CARD,
    )
}
