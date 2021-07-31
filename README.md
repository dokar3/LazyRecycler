# RecyclerView，Compose like

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.dokar3/lazyrecycler/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.dokar3/lazyrecycler)

![](arts/lazyrecycler.png)

**LazyRecycler** is a library that provides [LazyColumn](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary) (from [Jetpack Compose](https://developer.android.com/jetpack/compose)) like APIs to build lists with RecyclerView. 

### Usage

```groovy
implementation 'io.github.dokar3:lazyrecycler:0.1.8'
```

With LazyRecycler, a few dozen lines of code can do almost all things for RecyclerView. Adapter, LayoutManager, DiffUtil, OnItemClickListener and more, these are **all in one** block:

```kotlin
lazyRecycler(recyclerView, spanCount = 3) {
    item(Unit, R.layout.header) {}

    items(listOfNews, ItemNewsBinding::inflate) { binding, news ->
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
    item(Unit, R.layout.footer) {}
}
```

# Basic

### Sections

LazyRecycler uses sections to build lists, A section will contain id, items, view creator, view binder, click listeners and other needed properties, it represents a view type in adapter. To create a new section, use the `item` and `items` function:

```kotlin
item(...) { ... }

items(...) { ... }
```

Sections should only be used in `lazyRecycler` / `newSections` block, so it's necessary to pass a unique id when creating a dynamic section (Require doing some operations after list is created, like update, remove, hide and show). Or use mutable data sources (see the section below).

```kotlin
val recycler = lazyRecycler {
    items(..., sectionId = SOME_ID) { ... } 
}

// update
recycler.updateSection(SOME_ID, items)
// remove
recycler.removeSection(SOME_ID)
// hide and show
recycler.setSectionVisible(SOME_ID, false)
```

### Layout

layout id and ViewBinding/DataBinding are supported:

```kotlin
items(news, R.layout.item_news) { ... }

items(news, ItemNewsBinding::inflate) { binding, item -> ... }
```

Providing an item view in the bind scope is also supported:

```kotlin
items(news) { parent ->
    val itemView = CustomNewsItemView(context)
    ...
    return@items itemView
}
```

### Bind

For ViewBinding item/items:

```kotlin
items(news, ItemNewsBinding::inflate) { binding, item -> 
    binding.title.text = item.title
    binding.image.load(item.cover)
    ...
}
```

For DataBinding item/items:

```kotlin
items(news, ItemNewsDataBinding) { binding, item -> 
    binding.news = item
}
```

For layoutId item/items:

```kotlin
items(news, R.layout.item_news) { view ->
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
    return@items itemView
}
```

### Generics

All the `item`, `items`, and `template` are generic functions:

```kotlin
items<I>(news, layoutId) { ... }
items<V, I>(news, ItemViewBinding::infalte) { ... }
items<I>(news) { ... }
```

* `I` for item type
* `V` for the View Binding

### Overloads

There are a lots of `item`/`items` functions to support various layouts, for helping compiler to pick correct one, lambdas may require explicit argument(s):

```kotlin
// item
item(Unit, R.layout.item) {
	// Parameter 'view' can be ommited
}
item(Unit, ItemBinding::inflate) { binding, item -> 
}
item(Unit) { parent -> 
    // Parameter 'parent' is required to distinguish from view binding one
    // Replace with '_' if it's unused
    return@item TextView(context) 
}

// items
items(listOf(Unit), R.layout.item) {
    // Parameter 'view' can be ommited
}
items(listOf(Unit), ItemBinding::infalte) { binding, item -> 
}
items(listOf(Unit)) { parent -> 
    // Parameter 'parent' is required to distinguish from view binding one
    // Replace with '_' if it's unused
    return@items TextView(context) 
}
```

### LayoutManager

LazyRecycler creates a LinearLayoutManager by default, if `spanCount`  is defined, GridLayoutManager will be picked.  To skip the LayoutManager setup, set `setupLayoutManager` to `false`:

```kotlin
lazyRecycler(
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
implementation 'io.github.dokar3:lazyrecycler-flow:0.1.8'
// LiveData
implementation 'io.github.dokar3:lazyrecycler-livedata:0.1.8'
// RxJava3
implementation 'io.github.dokar3:lazyrecycler-rxjava3:0.1.8'
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


### Observe/stop observing data sources:

LazyRecycler will observe the data changes automatically, so it's no necessary to call it manually. But there is a call if really needed:

```kotlin
// After stopObserving() have been called
recycler.observeChanges()
```

Manually stop the observing is required when using a RxJava data source, or not using lifecycleScope to create a Flow data source:

```kotlin
// onDestroy(), etc.
recycler.stopObserving()
```

# Advanced

### Alternate sections

`template()` + `items(section, ...)`: A section can be created from a `Template`:

```kotlin
lazyRecycler {
    // layout id definition
    val sectionHeader = template<String>(R.layout.section_header) {
        // bind item
    }
    // ViewBinding definition
    val normalItem = template<ItemViewBinding, Item> { binding, item ->
        // bind item
    }
    
    item(sectionHeader, "section 1")
    items(someItems, normalItem)
    
    item(sectionHeader, "section 2")
    items(otherItems, normalItem)
    ...
}
```

### Single items with multiple view types

`template()` + `subSection()`: Multiple view types for single data source is supported:

```kotlin
lazyRecycler {
    val bubbleFromMe = template<Message>(ItemMsgFromMeBinding::infalte) { binding, msg ->
        // bind item
    }
    
    items(messages, ItemMsgBinding::inflate) { binding, msg ->
        // bind item
    }.subSection(bubbleFromMe) { msg, position ->
        // condition
        msg.isFromMe
    }
    ...
}
```

### Add new sections dynamically

`recycler.newSections()`:

```kotlin
val recycler = ...
...
recycler.newSections {
    item(...) { ... }
    items(...) { ... }
    ...
}
// If new sections contain any observable data source
recycler.observeChanges()
```

### Build list in background

`recycler.attchTo()`:

```kotlin
backgroundThread {
    val recycler = lazyRecycler {
        ...
    }
    uiThread {
        recycler.attchTo(recyclerView)
    }
}
```

# ProGuard

It's not needed anymore.

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