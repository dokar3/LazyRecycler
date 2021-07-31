package com.dokar.lazyrecycler.flow

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dokar.lazyrecycler.items
import com.dokar.lazyrecycler.lazyRecycler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class FlowTest {

    private val fakeLayoutId = 0

    @Test
    fun itemTest() {
        val id = 0
        val text = "ABC"
        val source = MutableStateFlow(text)

        val scope = TestCoroutineScope(SupervisorJob())

        val recycler = lazyRecycler {
            items(source.asMutSource(scope), fakeLayoutId, id) {}
        }

        recycler.observeChanges()

        assertEquals(text, recycler.getSectionItems(id)?.get(0))

        val text2 = "DEF"
        source.value = text2
        assertEquals(text2, recycler.getSectionItems(id)?.get(0))

        // unregistered
        recycler.stopObserving()

        val text3 = "GHI"
        source.value = text3
        assertNotEquals(text3, recycler.getSectionItems(id)?.get(0))

        // registered
        recycler.observeChanges()
        assertEquals(text3, recycler.getSectionItems(id)?.get(0))

        // Coroutine scope canceled
        scope.cancel()
        val text4 = "JKL"
        source.value = text4
        assertNotEquals(text4, recycler.getSectionItems(id)?.get(0))
    }

    @Test
    fun itemsTest() {
        val id = 0
        val list = listOf(1, 2, 3)
        val source = MutableStateFlow(list)

        val scope = TestCoroutineScope(SupervisorJob())

        val recycler = lazyRecycler {
            items(source.asMutSource(scope), fakeLayoutId, id) {}
        }
        recycler.observeChanges()

        assertEquals(list, recycler.getSectionItems(id))

        val list2 = listOf(4, 5, 6)
        source.value = list2
        assertEquals(list2, recycler.getSectionItems(id))

        // unregistered
        recycler.stopObserving()

        val list3 = listOf(7, 8, 9)
        source.value = list3
        assertNotEquals(list3, recycler.getSectionItems(id))

        // registered
        recycler.observeChanges()
        assertEquals(list3, recycler.getSectionItems(id))

        // Coroutine scope canceled
        scope.cancel()
        val list4 = listOf(10, 11, 12)
        source.value = list4
        assertNotEquals(list4, recycler.getSectionItems(id))
    }
}
