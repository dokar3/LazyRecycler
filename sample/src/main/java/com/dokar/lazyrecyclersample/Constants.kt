package com.dokar.lazyrecyclersample

import kotlin.random.Random

object Constants {
    const val ID_PAINTINGS = 1

    const val OPT_SHUFFLE = 2
    const val OPT_REMOVE_SECTION = 3
    const val OPT_NEW_SECTIONS = 4

    val OPTIONS = listOf(
        Option(OPT_SHUFFLE, "Shuffle"),
        Option(OPT_REMOVE_SECTION, "Remove"),
        Option(OPT_NEW_SECTIONS, "New sections"),
    )

    val VINCENT_PAINTINGS = listOf(
        Painting(
            "The Starry Night",
            1889,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg/303px-Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg",
            "https://en.wikipedia.org/wiki/The_Starry_Night"
        ),
        Painting(
            "Van Gogh self-portrait",
            1889,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/Vincent_van_Gogh_-_Self-Portrait_-_Google_Art_Project.jpg/395px-Vincent_van_Gogh_-_Self-Portrait_-_Google_Art_Project.jpg",
            "https://en.wikipedia.org/wiki/Van_Gogh_self-portrait_(1889)"
        ),
        Painting(
            "Sunflowers",
            1888,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/4/46/Vincent_Willem_van_Gogh_127.jpg/380px-Vincent_Willem_van_Gogh_127.jpg",
            "https://en.wikipedia.org/wiki/Sunflowers_(Van_Gogh_series)"
        ),
        Painting(
            "Starry Night Over the Rhône",
            1888,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/9/94/Starry_Night_Over_the_Rhone.jpg/310px-Starry_Night_Over_the_Rhone.jpg",
            "https://en.wikipedia.org/wiki/Starry_Night_Over_the_Rh%C3%B4ne"
        ),
        Painting(
            "Langlois Bridge at Arles",
            1888,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d5/Vincent_Van_Gogh_0014.jpg/312px-Vincent_Van_Gogh_0014.jpg",
            "https://en.wikipedia.org/wiki/Langlois_Bridge_at_Arles"
        ),
        Painting(
            "The Yellow House",
            1888,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7b/Vincent_van_Gogh_-_The_yellow_house_%28%27The_street%27%29.jpg/310px-Vincent_van_Gogh_-_The_yellow_house_%28%27The_street%27%29.jpg",
            "https://en.wikipedia.org/wiki/The_Yellow_House",
        ),
        Painting(
            "Self-Portrait With a Bandaged Ear",
            1889,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/Vincent_Willem_van_Gogh_106.jpg/424px-Vincent_Willem_van_Gogh_106.jpg",
            "https://en.wikipedia.org/wiki/File:Vincent_Willem_van_Gogh_106.jpg"
        ),
        Painting(
            "Sorrowing Old Man (At Eternity's Gate)",
            1890,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d2/Van_Gogh_-_Trauernder_alter_Mann.jpeg/364px-Van_Gogh_-_Trauernder_alter_Mann.jpeg",
            "https://en.wikipedia.org/wiki/At_Eternity%27s_Gate"
        ),
        Painting(
            "The Church at Auvers",
            1890,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Vincent_van_Gogh_-_The_Church_in_Auvers-sur-Oise%2C_View_from_the_Chevet_-_Google_Art_Project.jpg/372px-Vincent_van_Gogh_-_The_Church_in_Auvers-sur-Oise%2C_View_from_the_Chevet_-_Google_Art_Project.jpg",
            "https://en.wikipedia.org/wiki/The_Church_at_Auvers"
        )
    )

    private const val thirtyMinutes = 30 * 60 * 1000L

    private const val MSG_PLACEHOLDER =
        "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."

    private val RANDOM_DATES = List(30) {
        if (it < 4) {
            System.currentTimeMillis() - Random.nextLong(thirtyMinutes * 48, thirtyMinutes * 96)
        } else {
            System.currentTimeMillis() - Random.nextLong(1000L, thirtyMinutes)
        }
    }.sorted().iterator()

    val CHAT_MESSAGES = listOf(
        Message(
            0,
            "Robot",
            R.drawable.ic_unsplash_robot,
            MSG_PLACEHOLDER.substring(0, Random.nextInt(10, MSG_PLACEHOLDER.length / 2)),
            RANDOM_DATES.next()
        ),
        Message(
            1,
            "Mr. Bird",
            R.drawable.ic_unsplash_bird,
            MSG_PLACEHOLDER.substring(0, Random.nextInt(10, MSG_PLACEHOLDER.length)),
            RANDOM_DATES.next()
        ),
        Message(
            0,
            "Robot",
            R.drawable.ic_unsplash_robot,
            MSG_PLACEHOLDER.substring(0, Random.nextInt(10, MSG_PLACEHOLDER.length / 3)),
            RANDOM_DATES.next()
        ),
        Message(
            0,
            "Robot",
            R.drawable.ic_unsplash_robot,
            MSG_PLACEHOLDER.substring(0, Random.nextInt(10, MSG_PLACEHOLDER.length)),
            RANDOM_DATES.next()
        ),
        Message(
            1,
            "Mr. Bird",
            R.drawable.ic_unsplash_bird,
            MSG_PLACEHOLDER.substring(0, Random.nextInt(10, MSG_PLACEHOLDER.length / 2)),
            RANDOM_DATES.next()
        ),
        Message(
            0,
            "Robot",
            R.drawable.ic_unsplash_robot,
            MSG_PLACEHOLDER.substring(0, Random.nextInt(10, MSG_PLACEHOLDER.length / 4)),
            RANDOM_DATES.next()
        ),
        Message(
            1,
            "Mr. Bird",
            R.drawable.ic_unsplash_bird,
            MSG_PLACEHOLDER.substring(0, Random.nextInt(10, MSG_PLACEHOLDER.length)),
            RANDOM_DATES.next()
        ),
        Message(
            1,
            "Mr. Bird",
            R.drawable.ic_unsplash_bird,
            MSG_PLACEHOLDER.substring(0, Random.nextInt(10, MSG_PLACEHOLDER.length / 2)),
            RANDOM_DATES.next()
        ),
        Message(
            0,
            "Robot",
            R.drawable.ic_unsplash_robot,
            MSG_PLACEHOLDER.substring(0, Random.nextInt(10, MSG_PLACEHOLDER.length / 4)),
            RANDOM_DATES.next()
        ),
    )
}

data class Painting(
    val title: String,
    val year: Int,
    val thumbnail: String,
    val url: String
) {
    val id = Random.nextInt()
}

data class Option(
    val id: Int,
    val text: String
)

class SentDate(
    id: Int,
    val date: Long,
    val dateText: String,
) : ChatItem(id)

class Message(
    val senderId: Int,
    val senderName: String,
    val senderAvatar: Int,
    val content: String,
    val sentAt: Long,
) : ChatItem() {
    init {
        id = System.identityHashCode(this)
    }
}

abstract class ChatItem(var id: Int = 0)
