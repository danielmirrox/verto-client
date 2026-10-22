package com.samsung.verto

import com.samsung.verto.data.local.InMemorySessionStore
import com.samsung.verto.data.local.MemoryRecipientHistory
import com.samsung.verto.domain.model.User
import com.samsung.verto.domain.usecase.UseCases
import com.samsung.verto.presentation.main.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RecipientHistoryTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test fun selectedRecipientsAreUniqueNewestFirstAndLimitedToTen() {
        val storage = MemoryRecipientHistory()
        val vm = MainViewModel(UseCases(FakePaymentRepository()), InMemorySessionStore(), storage)
        (1L..12L).forEach { vm.selectRecipient(User(it, "user$it@example.com", null)) }
        assertEquals((12L downTo 3L).toList(), vm.uiState.value.recipientHistory.map { it.id })
        vm.selectRecipient(User(5L, "user5@example.com", null))
        assertEquals(10, vm.uiState.value.recipientHistory.size)
        assertEquals(5L, vm.uiState.value.recipientHistory.first().id)
        assertEquals("5", vm.uiState.value.transferUserId)
        assertEquals(1, vm.uiState.value.recipientHistory.count { it.id == 5L })
    }

    @Test fun historyRestoresInNewViewModelAndClearRemovesStoredItems() {
        val storage = MemoryRecipientHistory()
        val cases = UseCases(FakePaymentRepository())
        val vm = MainViewModel(cases, InMemorySessionStore(), storage)
        vm.selectRecipient(User(42L, "recipient@example.com", "Получатель"))
        val restored = MainViewModel(cases, InMemorySessionStore(), storage)
        assertEquals(42L, restored.uiState.value.recipientHistory.single().id)
        restored.clearHistory()
        assertTrue(storage.load().isEmpty())
        assertTrue(restored.uiState.value.recipientHistory.isEmpty())
    }

    @Test fun clearQueryDoesNotEraseRecipientHistory() {
        val vm = MainViewModel(UseCases(FakePaymentRepository()), InMemorySessionStore())
        vm.selectRecipient(User(1L, "one@example.com", null))
        vm.updateSearchQuery("one")
        vm.clearSearch()
        assertEquals("", vm.uiState.value.searchQuery)
        assertEquals(1, vm.uiState.value.recipientHistory.size)
        assertTrue(vm.uiState.value.searchResults.isEmpty())
    }
}
