package com.dokar.lazyrecycler.rxjava3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.LazyRecycler
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RxTest {

    private lateinit var recycler: LazyRecycler

    @Before
    fun setup() {
        recycler = LazyRecycler { }
    }

    @Test
    fun observableItemTest() {
        recycler.clearSections()

        val source = PublishSubject.create<String>()

        recycler.newSections {
            item<NoopBinding, String>(source) { _, _ -> }
        }
        recycler.observeChanges()

        val text = "ABC"
        source.onNext(text)
        assertEquals(text, recycler.sections[0].items[0])

        recycler.stopObserving()

        val text2 = "DEF"
        source.onNext(text2)
        assertNotEquals(text2, recycler.sections[0].items[0])
    }

    @Test
    fun behaviorSubjectItemTest() {
        recycler.clearSections()

        val text = "ABC"
        val source = BehaviorSubject.createDefault(text)

        recycler.newSections {
            item<NoopBinding, String>(source) { _, _ -> }
        }
        recycler.observeChanges()

        assertEquals(text, recycler.sections[0].items[0])

        val text2 = "DEF"
        source.onNext(text2)
        assertEquals(text2, recycler.sections[0].items[0])

        recycler.stopObserving()
        val text3 = "GHI"
        source.onNext(text3)
        assertNotEquals(text3, recycler.sections[0].items[0])
    }

    @Test
    fun observableItemsTest() {
        recycler.clearSections()

        val source = PublishSubject.create<List<Int>>()

        recycler.newSections {
            items<NoopBinding, Int>(source) { _, _ -> }
        }
        recycler.observeChanges()

        val list = listOf(1, 2, 3)
        source.onNext(list)
        assertEquals(list, recycler.sections[0].items)

        recycler.stopObserving()
        val list2 = listOf(4, 5, 6)
        source.onNext(list2)
        assertNotEquals(list2, recycler.sections[0].items)
    }

    @Test
    fun behaviorSubjectItemsTest() {
        recycler.clearSections()

        val list = listOf(1, 2, 3)
        val source = BehaviorSubject.createDefault(list)

        recycler.newSections {
            items<NoopBinding, Int>(source) { _, _ -> }
        }
        recycler.observeChanges()

        assertEquals(list, recycler.sections[0].items)

        val list2 = listOf(4, 5, 6)
        source.onNext(list2)
        assertEquals(list2, recycler.sections[0].items)

        recycler.stopObserving()
        val list3 = listOf(7, 8, 9)
        source.onNext(list3)
        assertNotEquals(list3, recycler.sections[0].items)
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