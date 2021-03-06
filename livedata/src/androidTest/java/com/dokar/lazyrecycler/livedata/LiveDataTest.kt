package com.dokar.lazyrecycler.livedata

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.LazyRecycler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LiveDataTest {

    private lateinit var recycler: LazyRecycler

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Before
    fun setup() {
        recycler = LazyRecycler { }
    }

    @Test
    fun itemTest() {
        recycler.clearSections()

        val text = "ABC"
        val source = MutableLiveData(text)

        recycler.newSections {
            item<NoopBinding, String>(source) { _, _ -> }
        }
        assertEquals(text, recycler.sections[0].items[0])

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        ui {
            recycler.observeChanges { lifecycle }
        }

        val text2 = "DEF"
        ui {
            source.value = text2
        }
        assertEquals(text2, recycler.sections[0].items[0])

        // onStopped
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        val text3 = "GHI"
        ui {
            source.value = text3
        }
        assertNotEquals(text3, recycler.sections[0].items[0])

        // onResumed
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        assertEquals(text3, recycler.sections[0].items[0])

        // unregistered
        ui {
            recycler.stopObserving { lifecycle }
        }
        val text4 = "JKL"
        ui {
            source.value = text4
        }
        assertNotEquals(text4, recycler.sections[0].items[0])
    }

    @Test
    fun itemsTest() {
        recycler.clearSections()
        val list = listOf(1, 2, 3)
        val source = MutableLiveData(list)
        recycler.newSections {
            items<NoopBinding, Int>(source) { _, _ -> }
        }
        // default value
        assertEquals(list, recycler.sections[0].items)

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        ui {
            recycler.observeChanges { lifecycle }
        }

        val list2 = listOf(4, 5, 6)
        ui {
            source.value = list2
        }
        assertEquals(list2, recycler.sections[0].items)

        // onStopped
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        val list3 = listOf(7, 8, 9)
        ui {
            source.value = list3
        }
        assertNotEquals(list3, recycler.sections[0].items)

        // onResumed
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        assertEquals(list3, recycler.sections[0].items)

        // unregistered
        ui {
            recycler.stopObserving { lifecycle }
        }
        val list4 = listOf(10, 11, 12)
        ui {
            source.value = list4
        }
        assertNotEquals(list4, recycler.sections[0].items)
    }

    private inline fun ui(crossinline block: () -> Unit) {
        instrumentation.runOnMainSync {
            block()
        }
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