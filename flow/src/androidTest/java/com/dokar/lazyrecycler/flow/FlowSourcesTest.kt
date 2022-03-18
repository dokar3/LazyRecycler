package com.dokar.lazyrecycler.flow

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dokar.lazyrecycler.item
import com.dokar.lazyrecycler.items
import com.dokar.lazyrecycler.lazyRecycler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class FlowSourcesTest {
    @Test
    fun observe_MutableStateFlow_item() {
        val id = 0
        val text = "ABC"
        val source = MutableStateFlow(text)

        val scope = TestScope(UnconfinedTestDispatcher())

        val recycler = lazyRecycler {
            item(
                data = source.toMutableValue(scope),
                layout = 0,
                id = id,
            ) {}
        }

        recycler.observeChanges()

        assertEquals(text, recycler.getSectionItems(id)?.get(0))

        val text2 = "DEF"
        source.value = text2
        assertEquals(text2, recycler.getSectionItems(id)?.get(0))

        // Stop observing
        recycler.stopObserving()

        val text3 = "GHI"
        source.value = text3
        assertNotEquals(text3, recycler.getSectionItems(id)?.get(0))

        // Re-observe
        recycler.observeChanges()
        assertEquals(text3, recycler.getSectionItems(id)?.get(0))
    }

    @Test
    fun observe_MutableStateFlow_items() {
        val id = 0
        val list = listOf(1, 2, 3)
        val source = MutableStateFlow(list)

        val scope = TestScope(UnconfinedTestDispatcher())

        val recycler = lazyRecycler {
            items(
                data = source.toMutableValue(scope),
                layout = 0,
                id = id,
            ) {}
        }
        recycler.observeChanges()

        assertEquals(list, recycler.getSectionItems(id))

        val list2 = listOf(4, 5, 6)
        source.value = list2
        assertEquals(list2, recycler.getSectionItems(id))

        // Stop observing
        recycler.stopObserving()

        val list3 = listOf(7, 8, 9)
        source.value = list3
        assertNotEquals(list3, recycler.getSectionItems(id))

        // Re-observe
        recycler.observeChanges()
        assertEquals(list3, recycler.getSectionItems(id))
    }
}
