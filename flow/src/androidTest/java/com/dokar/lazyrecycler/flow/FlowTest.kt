package com.dokar.lazyrecycler.flow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.LazyRecycler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FlowTest {

    private lateinit var recycler: LazyRecycler

    @Before
    fun setup() {
        recycler = LazyRecycler {  }
    }

    @Test
    fun itemTest() {
        recycler.clearSections()

        val text = "ABC"
        val source = MutableStateFlow(text)
        recycler.newSections {
            item<NoopBinding, String>(source) { _, _ -> }
        }

        val scope = TestCoroutineScope(SupervisorJob())
        recycler.observeChanges(scope)

        assertEquals(text, recycler.sections[0].items[0])

        val text2 = "DEF"
        source.value = text2
        assertEquals(text2, recycler.sections[0].items[0])

        // unregistered
        recycler.stopObserving(scope)

        val text3 = "GHI"
        source.value = text3
        assertNotEquals(text3, recycler.sections[0].items[0])

        // registered
        recycler.observeChanges(scope)
        assertEquals(text3, recycler.sections[0].items[0])

        // Coroutine scope canceled
        scope.cancel()
        val text4 = "JKL"
        source.value = text4
        assertNotEquals(text4, recycler.sections[0].items[0])
    }

    @Test
    fun itemsTest() {
        recycler.clearSections()

        val list = listOf(1, 2, 3)
        val source = MutableStateFlow(list)

        recycler.newSections {
            items<NoopBinding, Int>(source) { _, _ -> }
        }

        val scope = TestCoroutineScope(SupervisorJob())
        recycler.observeChanges(scope)

        assertEquals(list, recycler.sections[0].items)

        val list2 = listOf(4, 5, 6)
        source.value = list2
        assertEquals(list2, recycler.sections[0].items)

        // unregistered
        recycler.stopObserving(scope)

        val list3 = listOf(7, 8, 9)
        source.value = list3
        assertNotEquals(list3, recycler.sections[0].items)

        // registered
        recycler.observeChanges(scope)
        assertEquals(list3, recycler.sections[0].items)

        // Coroutine scope canceled
        scope.cancel()
        val list4 = listOf(10, 11, 12)
        source.value = list4
        assertNotEquals(list4, recycler.sections[0].items)
    }


    class NoopBinding private constructor(private val root: View) : ViewBinding {

        override fun getRoot(): View {
            return root
        }

        companion object {

            @JvmStatic
            fun inflate(
                layoutInflater: LayoutInflater,
                parent: ViewGroup,
                attachToParent: Boolean
            ) {
                NoopBinding(View(parent.context))
            }
        }
    }
}