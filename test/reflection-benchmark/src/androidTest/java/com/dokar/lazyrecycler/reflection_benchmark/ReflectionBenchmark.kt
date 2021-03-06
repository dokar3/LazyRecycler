package com.dokar.lazyrecycler.reflection_benchmark

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewbinding.ViewBinding
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.IllegalStateException

@RunWith(AndroidJUnit4::class)
class ReflectionBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun newInstanceTest() {
        benchmarkRule.measureRepeated {
            repeat(1000) {
                val obj = ForTest::class.java.newInstance()
            }
        }
    }

    @Test
    fun newOperatorTest() {
        benchmarkRule.measureRepeated {
            repeat(1000) {
                val obj = ForTest()
            }
        }
    }

    @Test
    fun reflectInflationTest() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val inflater = LayoutInflater.from(context)
        val parent = FrameLayout(context)
        benchmarkRule.measureRepeated {
            val func = TestLayoutBinding::class.java.getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            )
            repeat(1000) {
                val binding = func.invoke(null, inflater, parent, false)
            }
        }
    }

    @Test
    fun directInflationTest() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val inflater = LayoutInflater.from(context)
        val parent = FrameLayout(context)
        benchmarkRule.measureRepeated {
            repeat(1000) {
                val binding = TestLayoutBinding.inflate(inflater, parent, false)
            }
        }
    }

    class ForTest

    class TestLayoutBinding : ViewBinding {

        override fun getRoot(): Nothing {
            throw IllegalStateException("Noop")
        }

        companion object {

            @JvmStatic
            fun inflate(
                layoutInflater: LayoutInflater,
                parent: ViewGroup,
                attachToParent: Boolean
            ): TestLayoutBinding {
                return TestLayoutBinding()
            }
        }

    }
}