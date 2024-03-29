package com.dokar.lazyrecycler.rxjava3

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dokar.lazyrecycler.item
import com.dokar.lazyrecycler.items
import com.dokar.lazyrecycler.lazyRecycler
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RxSourcesTest {
    @Test
    fun observe_Observable_item() {
        val id = 0
        val source = PublishSubject.create<String>()

        val recycler = lazyRecycler {
            item(
                data = source.toMutableValue(),
                layout = 0,
                id = id,
            ) {}
        }
        recycler.observeChanges()

        val text = "ABC"
        source.onNext(text)
        assertEquals(text, recycler.getSectionItems(id)?.get(0))

        recycler.stopObserving()

        val text2 = "DEF"
        source.onNext(text2)
        assertNotEquals(text2, recycler.getSectionItems(id)?.get(0))
    }

    @Test
    fun observe_BehaviorSubject_item() {
        val text = "ABC"
        val id = 0
        val source = BehaviorSubject.createDefault(text)

        val recycler = lazyRecycler {
            item(
                data = source.toMutableValue(),
                layout = 0,
                id = id,
            ) {}
        }
        recycler.observeChanges()

        assertEquals(text, recycler.getSectionItems(id)?.get(0))

        val text2 = "DEF"
        source.onNext(text2)
        assertEquals(text2, recycler.getSectionItems(id)?.get(0))

        recycler.stopObserving()
        val text3 = "GHI"
        source.onNext(text3)
        assertNotEquals(text3, recycler.getSectionItems(id)?.get(0))
    }

    @Test
    fun observe_Observable_items() {
        val id = 0
        val source = PublishSubject.create<List<Int>>()

        val recycler = lazyRecycler {
            items(
                data = source.toMutableValue(),
                layout = 0,
                id = id,
            ) {}
        }
        recycler.observeChanges()

        val list = listOf(1, 2, 3)
        source.onNext(list)
        assertEquals(list, recycler.getSectionItems(id))

        recycler.stopObserving()
        val list2 = listOf(4, 5, 6)
        source.onNext(list2)
        assertNotEquals(list2, recycler.getSectionItems(id))
    }

    @Test
    fun observe_BehaviorSubject_items() {
        val id = 0
        val list = listOf(1, 2, 3)
        val source = BehaviorSubject.createDefault(list)

        val recycler = lazyRecycler {
            items(
                data = source.toMutableValue(),
                layout = 0,
                id = id,
            ) {}
        }
        recycler.observeChanges()

        assertEquals(list, recycler.getSectionItems(id))

        val list2 = listOf(4, 5, 6)
        source.onNext(list2)
        assertEquals(list2, recycler.getSectionItems(id))

        recycler.stopObserving()
        val list3 = listOf(7, 8, 9)
        source.onNext(list3)
        assertNotEquals(list3, recycler.getSectionItems(id))
    }

    @Test
    fun observe_if_error_occurred() {
        val id = 0
        val source = Observable.create<Int> {
            val x = 3 / 0
            it.onNext(x)
        }

        val recycler = lazyRecycler {
            item(
                data = source.toMutableValue(),
                layout = 0,
                id = id,
            ) {}
        }
        recycler.observeChanges()

        assertTrue(recycler.getSectionItems(id)?.isEmpty() ?: true)
    }
}
