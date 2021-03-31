# RecyclerView，Compose like

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.dokar3/lazyrecycler/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.dokar3/lazyrecycler)

![](arts/lazyrecycler.png)

**LazyRecycler** is a library that provides LazyColumn (from Jetpack Compose) like apis to build lists with RecyclerView. 

### Usage

```groovy
implementation 'io.github.dokar3:lazyrecycler:0.1.5'
```

With LazyRecycler, a few dozen lines of code can do almost all things for RecyclerView. Adapter, LayoutManager, DiffUtil, OnItemClickListener and more, these are **all in one** block:

```kotlin
LazyRecycler(recyclerView, spanCount = 3) {
    item(R.layout.header, Unit) {}

    items(listOfNews) { binding: ItemNewsBinding, news ->
        // bind item
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

Sections should only be used in `LazyRecycler` / `newSections` block, so it's necessary to pass a unique id when creating a dynamic section (Require doing some operations after list is created, like update, remove, hide and show). Or use mutable data sources (see the section below). 

```kotlin
val lazyRecycler = LazyRecycler {
    items(..., sectionId = SOME_ID) { ... } 
}

// update
lazyRecycler.updateSection(SOME_ID, items)
// remove
lazyRecycler.removeSection(SOME_ID)
// hide and show
lazyRecycler.setSectionVisible(SOME_ID, false)
```

### Layout

layout id and ViewBinding/DataBinding are supported:

```kotlin
items(R.layout.item_news, news) { ... }

items(news) { binding: ItemNewsBinding, item -> ... }
```

Providing an item view in the bind scope is also supported:

```kotlin
items(news) { parent ->
    val itemView = CustomNewsItemView(context)
    ...
    return@item itemView
}
```

### Bind

For ViewBinding item/items:

```kotlin
items(news) { binding: ItemNewsBinding, item -> 
    binding.title.text = item.title
    binding.image.load(item.cover)
    ...
}
```

For DataBinding item/items:

```kotlin
items(news) { binding: ItemNewsDataBinding, item -> 
    binding.news = item
}
```

For layoutId item/items:

```kotlin
items(R.layout.item_news, news) { view ->
    ...
    val tv: TextView = view.findViewById(R.id.title)
    bind { item ->
        tv.text = item.title
    }
}
```

For view required item/items:

```kotlin
items(news) { parent -> 
    val itemView = CustomNewsItemView(context)
    bind { item ->
        itemView.title(item.title)
        ...
    }
    return@item itemView
}
```

### Generics

All the `item`, `items`, and `template` are generic functions:

```kotlin
items<I>(layoutId, I) { ... }
items<V, I>(items) { ... }
items<I>(items) { ... }
```

* `I` for item type
* `V` for the View Binding

### Overloads

There are a lots of `item`/`items` functions to support various layouts, for helping compiler to pick correct one, lambdas may require explicit argument(s):

```kotlin
// item
item(R.layout.item, Any()) {
	// Parameter 'view' can be ommited
}
item(Any()) { binding: ItemBinding, item -> 
}
item(Any()) { parent -> 
    // Parameter 'parent' is required to distinguish from view binding one
    // Replace with '_' if it's unused
    return@item TextView(context) 
}

// items
items(R.layout.item, listOf(Any()) {
    // Parameter 'view' can be ommited
}
items(listOf(Any()) { binding: ItemBinding, item -> 
}
items(listOf(Any()) { parent -> 
    // Parameter 'parent' is required to distinguish from view binding one
    // Replace with '_' if it's unused
    return@items TextView(context) 
}
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

items(...) {
    ...
}.spanSize { position ->
    if (position == 0) 3 else 1
}
```

### DiffUtil

Use `differ` function, then LazyRecycler will do the all works after section items changed. 

``` kotlin
items(...) {
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

# Mutable data sources

To support some mutable(observable) data sources like `Flow`, `LiveData`, or `RxJava`, add the dependencies:

```groovy
// Flow
implementation 'io.github.dokar3:lazyrecycler-flow:0.1.5'
// LiveData
implementation 'io.github.dokar3:lazyrecycler-livedata:0.1.5'
// RxJava3
implementation 'io.github.dokar3:lazyrecycler-rxjava3:0.1.5'
```

### Flow

1. Use extension function `Flow.asMutSource(CoroutineScope)`:

   ```kotlin
   import com.dokar.lazyrecycler.flow.asMutSource
   
   val entry: Flow<I> = ...
   // Use items() instead of item()
   items(entry.asMutSource(coroutineScope)) { ... }
   
   val source: Flow<List<I>> = ...
   items(source.asMutSource(coroutineScope)) { ... }
   ```

3. Observe/stop observing: 

   ```kotlin
   val lazyRecycler = LazyRecycler { ... }
   ...
   // Not needed, Observe data changes, will be called automatically
   lazyRecycler.observeChanges()
   // Stop observing, Not needed if mutable data sources are created from 
   // a lifecycleScope
   lazyRecycler.stopObserving()
   ```

### LiveData

1. Use extension function `LiveData.asMutSource(LifecycleOwner)`:

   ```kotlin
   import com.dokar.lazyrecycler.livedata.asMutSource
   
   val entry: LiveData<I> = ...
   // Use items() instead of item()
   items(entry.asMutSource(lifecycleOwner)) { ... }
   
   val source: LiveData<List<I>> = ...
   items(source.asMutSource(lifecycleOwner)) { ... }
   ```

3. Observe/stop observing: 

   ```kotlin
   // Not needed, lifecycle will remove observers when components are destroyed
   ```

### RxJava

1. Use extension function `Observable.asMutSource()`:

   ```kotlin
   import com.dokar.lazyrecycler.rxjava3.asMutSource
   
   val entry: Observable<I> = ...
   // Use items() instead of item()
   items(entry.asMutSource()) { ... }
   
   val source: Observable<List<I>> = ...
   items(source.asMutSource()) { ... }
   ```

3. Observe/stop observing:

   ```kotlin
   val lazyRecycler = LazyRecycler { ... }
   ...
   // Not needed, Observe data changes, will be called automatically
   lazyRecycler.observeChanges()
   // Call it when Activitiy/Fragment is destroyed
   lazyRecycler.stopObserving()
   ```

# Advanced

### Alternate sections

`template()` + `items(section, ...)`: A section can be created from a `Template`:

```kotlin
LazyRecycler {
    // layout id definition
    val sectionHeader = template<String>(R.layout.section_header) {
        // bind item
    }
    // ViewBinding definition
    val normalItem = template<ItemViewBinding, Item> { binding, item ->
        // bind item
    }
    
    item(sectionHeader, "section 1")
    items(normalItem, source)
    
    item(sectionHeader, "section 2")
    items(normalItem, source)
    ...
}
```

### Single items with multiple view types

`template()` + `subSection()`: Multiple view types for single data source is supported:

```kotlin
LazyRecycler {
    val bubbleFromMe = template<ItemMsgFromMeBinding, Message> { binding, msg ->
        // bind item
    }
    
    items(messages) { binding: ItemMsgBinding, msg ->
        // bind item
    }.subSection(bubbleFromMe) { msg, position ->
        // condition
        msg.isFromMe
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
lazyRecycler.observeChanges()
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