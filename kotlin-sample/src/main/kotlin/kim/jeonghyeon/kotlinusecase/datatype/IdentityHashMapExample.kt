package kim.jeonghyeon.kotlinusecase.datatype

import java.nio.ByteBuffer
import java.util.*

class IdentityHashMapExample {

    /**
     *
     * only if object is same
     * "aa" == "aa" may not be same if it's different object
     * if there is buffer. it is good to use this.
     *
     * Map to convert between a byte array, received from the camera, and its associated byte buffer.
     * We use byte buffers internally because this is a more efficient way to call into native code
     * later (avoids a potential copy).
     *
     *
     * **Note:** uses IdentityHashMap here instead of HashMap because the behavior of an array's
     * equals, hashCode and toString methods is both useless and unexpected. IdentityHashMap enforces
     * identity ('==') check on the keys.
     */
    private val bytesToByteBuffer = IdentityHashMap<ByteArray, ByteBuffer>()
}