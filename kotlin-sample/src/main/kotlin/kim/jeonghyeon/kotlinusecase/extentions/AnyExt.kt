@file:Suppress("RedundantExplicitType", "unused", "UNUSED_VARIABLE")

package kim.jeonghyeon.kotlinusecase.extentions

import java.io.File


/**
 * also(). similar to apply() but not use this. so, it is useful on the below case
 */
fun Block.copy() = Block().also {
    it.content = this.content
}

/**
 * takeIf(), takeUnless() : single value filtering. used with elvis-operator
 */
fun takeIfMethod(): Boolean {

    val outputDir = File("")
    val outDirFile = File(outputDir.path).takeIf { it.exists() } ?: return false
    val outDirFile2 = File(outputDir.path).takeUnless { it.exists() } ?: return false
    return true
}


class Block {
    var content: String? = null
}