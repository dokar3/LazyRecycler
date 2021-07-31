package com.dokar.lazyrecycler.livedata

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import androidx.test.platform.app.InstrumentationRegistry
import com.dokar.lazyrecycler.items
import com.dokar.lazyrecycler.lazyRecycler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LiveDataTest {

    private val fakeLayoutId = 0

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Test
    fun itemTest() {
        val id = 0
        val text = "ABC"
        val source = MutableLiveData(text)

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val recycler = lazyRecycler {
            items(source.asMutSource { lifecycle }, id,fakeLayoutId) {}
        }
        assertEquals(text, recycler.getSectionItems(id)?.get(0))

        ui {
            recycler.observeChanges()
        }

        val text2 = "DEF"
        ui {
            source.value = text2
        }
        assertEquals(text2, recycler.getSectionItems(id)?.get(0))

        // onStopped
        val text3 = "GHI"
        ui {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            source.value = text3
        }
        assertNotEquals(text3, recycler.getSectionItems(id)?.get(0))

        // onResumed
        ui {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
        assertEquals(text3, recycler.getSectionItems(id)?.get(0))

        // unregistered
        ui {
            recycler.stopObserving()
        }
        val text4 = "JKL"
        ui {
            source.value = text4
        }
        assertNotEquals(text4, recycler.getSectionItems(id)?.get(0))
    }

    @Test
    fun itemsTest() {
        val id = 0
        val list = listOf(1, 2, 3)
        val source = MutableLiveData(list)

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val recycler = lazyRecycler {
            items(source.asMutSource { lifecycle }, fakeLayoutId, 0) {}
        }
        // default value
        assertEquals(list, recycler.getSectionItems(id))
        ui {
            recycler.observeChanges()
        }

        val list2 = listOf(4, 5, 6)
        ui {
            source.value = list2
        }
        assertEquals(list2, recycler.getSectionItems(id))

        // onStopped
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        val list3 = listOf(7, 8, 9)
        ui {
            source.value = list3
        }
        assertNotEquals(list3, recycler.getSectionItems(id))

        // onResumed
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        assertEquals(list3, recycler.getSectionItems(id))

        // unregistered
        ui {
            recycler.stopObserving()
        }
        val list4 = listOf(10, 11, 12)
        ui {
            source.value = list4
        }
        assertNotEquals(list4, recycler.getSectionItems(id))
    }

    private inline fun ui(crossinline block: () -> Unit) {
        instrumentation.runOnMainSync {
            block()
        }
    }
}
