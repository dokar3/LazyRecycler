# RecyclerView，Compose like

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.dokar3/lazyrecycler/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.dokar3/lazyrecycler)

![](arts/lazyrecycler.png)

**LazyRecycler** is a library that provides LazyColumn (from Jetpack Compose) like apis to build lists with RecyclerView. 

### Usage

```groovy
implementation 'io.github.dokar3:lazyrecycler:0.1.2'
```

With LazyRecycler, a few dozen lines of code can do almost all things for RecyclerView. Adapter, LayoutManager, DiffUtil, OnItemClickListener and more, these are **all in one** block:

```kotlin
LazyRecycler(recyclerView, spanCount = 3) {
    item(R.layout.header, Unit) {}

    items(listOfNews) { binding: ItemNewsBinding, news ->
        // bind
    }.clicks { view, item ->
        // handle item clicks
    }.spanSize { position ->
        // map to SpanSizeLookup.getSpanSize()
        if (position % 3 == 0) 3 else 1
    }.differ {
        areItemsTheSame { oldItem, newItem ->
            // map to DiffUtil.ItemCallback.areItemsTheSame()
            oldItem.id == newItem.id
        }
        areContentsTheSame { oldItem, newItem ->
            // map to DiffUtil.ItemCallback.areContentsTheSame()
            oldItem.title == newItem.title && ...
        }
    }
    
    ...
    
    item(R.layout.footer, Unit) {}
}
```

# Basic

### Sections

LazyRecycler uses sections to build lists, A section will contain id, items, view creator, view binder, click listeners and other needed properties, it represents a view type in adapter. To create a new section, use the `item` and `items` function:

```kotlin
item(...) { ... }

items(...) { ... }
```

Sections should only be used in `LazyRecycler` / `newSections` block, so it's necessary to pass a unique id when creating a dynamic section (Require doing some operations after list is created, like update, remove, hide and show). Or use observable data sources (see the section below). 

```kotlin
val lazyRecycler = LazyRecycler {
    items<News>(..., sectionId = SOME_ID) { ... } 
}

// update
lazyRecycler.updateSection(SOME_ID, items)
// remove
lazyRecycler.removeSection(SOME_ID)
// hide and show
lazyRecycler.setSectionVisible(SOME_ID, false)
```

### Layout

layout id and ViewBinding are supported:

```kotlin
items(R.layout.item_news, news) { ... }
items<ItemNewsBinding, News>(news) { ... }
```

Providing a item view in the bind scope is also supported:

```kotlin
items<String>(...) {
	val tv = TextView(context)
    bind { text ->
    	tv.text = text
    }
    return@item tv
}
```

### Bind

For ViewBinding item/items:

```kotlin
items<ItemNewsBinding, News>(...) { binding, news -> 
	binding.title.text = news.title
    binding.image.load(news.cover)
    ...
}
```

For other item/items:

```kotlin
items(...) { ...
    ...
    val tv: TextView = view.findViewById(R.id.title)
	bind { item ->
    	tv.text = item.title
    }
    ...
}
```

### Generics

* `I` for item type
* `V` for the View Binding

```kotlin
items<I>(layoutId, I) { ... }
items<V, I>(items) { ... }
```

### Overloads

There are a lots of `item`/`items` functions to support various layouts, for helping compiler to pick correct one, lambdas may require explicit argument(s):

```kotlin
// item
item(R.layout.item, Any()) {}
item(Any()) { binding: ItemBinding, item -> }
// Require -> to distinguish from view binding one
item(Any()) { -> TextView(context) }

// items
items(R.layout.item, listOf(Any()) {}
items(listOf(Any()) { binding: ItemBinding, item -> }
// Require -> to distinguish from view binding one
items(listOf(Any()) { -> TextView(context) }
```

### LayoutManager

LazyRecycler create a LinearLayoutManager by default, if `spanCount`  is defined, GridLayoutManager will be picked.  To skip the LayoutManager setup, set `setupLayoutManager` to `false`:

```kotlin
LazyRecycler(
    recyclerView,
	setupLayoutManager = false,
    isHorizontal = false, // ignored
    spanCount = 3, // ignored
    reverseLayout = false, // ignored
    stackFromEnd = false, // ignored
) { ... }
....
recyclerView.layoutManager = ...
```

`spanSize()` is used to define `SpanSizeLookup` for GridLayoutManager:

```kotlin
item(...) {
}.spanSize {
	3
}

items<V, I>(...) {
	...
}.spanSize { position ->
	if (position == 0) 3 else 1
}
```

### DiffUtil

Use `differ` function, then LazyRecycler will do the all works after section items changed. 

``` 
items<V, I>(...) {
	...
}.differ {
    areItemsTheSame { oldItem, newItem ->
        ...
    }
    areContentsTheSame { oldItem, newItem ->
    	...
    }
}
```

### Clicks

`clicks` for the OnItemClickListener, `longClicks` for the OnItemLongClickListener.

```kotlin
items(...) {
	...
}.clicks { view, item -> 
	...
}.longClicks { view, item -> 
    ...
}
```

# Observable data sources

To support `Flow`, `LiveData` or `RxJava`, add the dependencies first:

```groovy
// Flow
implementation 'io.github.dokar3:lazyrecycler-flow:0.1.2'

// LiveData
implementation 'io.github.dokar3:lazyrecycler-livedata:0.1.2'

// RxJava3
implementation 'io.github.dokar3:lazyrecycler-rxjava3:0.1.2'
```

### Flow

1. import:

   ```kotlin
   import com.dokar.lazyrecycler.flow.item
   import com.dokar.lazyrecycler.flow.items
   ```

2. Replace the data sources:

   ```kotlin
   val entry: Flow<I> = ...
   item<V, I>(entry) { ... }
   
   val source: Flow<List<I>> = ...
   items<V, I>(source) { ... }
   ```

3. Register/unregister observers:

   ```kotlin
   val lazyRecycler = LazyRecycler { ... }
   ...
   // Observe data changes
   lazyRecycler.observeChanges(coroutineScope)
   // Stop observing, if the register scope is lifecycleScope 
   // then there is no need to unregister it manually
   lazyRecycler.stopObserving(coroutineScope)
   ```

### LiveData

1. import:

   ```kotlin
   import com.dokar.lazyrecycler.livedata.item
   import com.dokar.lazyrecycler.livedata.items
   ```

2. Replace the data sources:

   ```kotlin
   val entry: LiveData<I> = ...
   item<V, I>(entry) { ... }
   
   val source: LiveData<List<I>> = ...
   items<V, I>(source) { ... }
   ```

3. Register/unregister observers:

   ```kotlin
   val lazyRecycler = LazyRecycler { ... }
   ...
   // Observe data changes
   lazyRecycler.observeChanges(lifecycleOwner)
   // Stop observing, no need to unregister it manually
   lazyRecycler.stopObserving(lifecycleOwner)
   ```

### RxJava

1. import:

   ```kotlin
   import com.dokar.lazyrecycler.rxjava3.item
   import com.dokar.lazyrecycler.rxjava3.items
   ```

2. Replace the data sources:

   ```kotlin
   val entry: Observable<I> = ...
   item<V, I>(entry) { ... }
   
   val source: Observable<List<I>> = ...
   items<V, I>(source) { ... }
   ```

3. Register/unregister observers:

   ```kotlin
   val lazyRecycler = LazyRecycler { ... }
   ...
   // Observe data changes
   lazyRecycler.observeChanges()
   // Required: Stop observing
   lazyRecycler.stopObserving()
   ```

# Advanced

### Alternate sections

`template()` + `items(section, ...)`: A section can be created from a `Template`:

```kotlin
LazyRecycler {
	// layout id definition
    val sectionHeader = template<String>(R.layout.section_header) {
    	// bind
    }
    // ViewBinding definition
    val normalItem = template<ItemViewBinding, Item> { binding, item ->
    	// bind
    }
    
    item(sectionHeader, "section 1")
    items(normalItem, someItems)
    
    item(sectionHeader, "section 2")
    items(normalItem, otherItems)
    ...
}
```

### Single items with multiple view types

`template()` + `subSection()`: Multiple view types for single data source is supported:

```kotlin
LazyRecycler {
	val bubbleFromMe = template<ItemMsgFromMeBinding, Message> { binding, msg ->
    	// bind
    }
    
    items<ItemMsgBinding, Message>(messages) { binding, msg ->
    	// bind
    }.subSection(bubbleFromMe) { msg, position ->
    	// condition
        msg.isFromMe == true
    }
    ...
}
```

### Add new sections dynamically

`LazyRecycler.newSections()`:

```kotlin
val lazyRecycler = ...
...
lazyRecycler.newSections {
    item(...) { ... }
    items(...) { ... }
    ...
}
// If new sections contain any observable data source
lazyRecycler.registerObservers(...)
```

### Build list in background

`LazyRecycler.attchTo()`:

```kotlin
backgroundThread {
	val recycler = LazyRecycler {
    	...
    }
    uiThread {
		lazyRecycler.attchTo(recyclerView)
    }
}
```

# ProGuard

If `ViewBinding` item/items are used, add this rule to proguard file:

```pro
# keep ViewBinding.inflate for LazyRecycler
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
}
```

# Demos

### 1. Multiple sections

| ![](arts/screenshot_gallery.png) | ![](arts/screenshot_gallery_night.png) |
| :------------------------------: | :------------------------------------: |
|               Day                |                 Night                  |

### 2. Chat screen

| ![](arts/screenshot_chat.png) | ![](arts/screenshot_chat_night.png) |
| :---------------------------: | :---------------------------------: |
|              Day              |                Night                |

### 3. Tetris game (Just for fun)

| ![](arts/screenshot_tetris.png) | ![](arts/screenshot_tetris_night.png) |
| :-----------------------------: | :-----------------------------------: |
|               Day               |                 Night                 |

# Links

* [Jetpack Compose](https://developer.android.com/jetpack/compose)
* [LazyColumn](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#lazycolumn)
* [sqaure/cycler](https://github.com/square/cycler)

# License

[Apache License 2.0](./LICENSE.txt)