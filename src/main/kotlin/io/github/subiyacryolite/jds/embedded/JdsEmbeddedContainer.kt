/**
 * Jenesis Data Store Copyright (c) 2017 Ifunga Ndana. All rights reserved.
 *
 * 1. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 2. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 3. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * Neither the name Jenesis Data Store nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.github.subiyacryolite.jds.embedded

import io.github.subiyacryolite.jds.JdsEntity
import io.github.subiyacryolite.jds.annotations.JdsEntityAnnotation
import java.sql.Timestamp

data class JdsStoreBlob(var id: Long = 0, var v: ByteArray? = null)

data class JdsStoreBoolean(var k: Long = 0, var v: Int? = null)

data class JdsStoreDouble(var k: Long = 0, var v: Double? = null)

data class JdsStoreDoubleCollection(var k: Long = 0, val v: MutableCollection<Double> = ArrayList())

data class JdsStoreEnum(var k: Long = 0, var v: Int? = null)

data class JdsStoreEnumCollection(var k: Long = 0, val v: MutableCollection<Int> = ArrayList())

data class JdsStoreInteger(var k: Long = 0, var v: Int? = null)

data class JdsStoreIntegerCollection(var k: Long = 0, val v: MutableCollection<Int> = ArrayList())

data class JdsStoreLong(var k: Long = 0, var v: Long? = null)

data class JdsStoreZonedDateTime(var k: Long = 0, var v: Long? = null)

data class JdsStoreLongCollection(var k: Long = 0, val v: MutableCollection<Long> = ArrayList())

data class JdsStoreTime(var k: Long = 0, var v: Long? = null)

data class JdsStoreString(var k: Long = 0, var v: String? = null)

data class JdsStoreStringCollection(var k: Long = 0, val v: MutableCollection<String> = ArrayList())

data class JdsStoreFloat(var k: Long = 0, var v: Float? = null)

data class JdsStoreFloatCollection(var k: Long = 0, val v: MutableCollection<Float> = ArrayList())

data class JdsStoreDateTime(var k: Long = 0, var v: Timestamp? = null)

data class JdsStoreDateTimeCollection(var k: Long = 0, var v: MutableCollection<Timestamp> = ArrayList())

data class JdsStoreDate(var k: Long = 0, var v: Timestamp? = null)

data class JdsStoreDuration(var k: Long = 0, var v: Long? = null)

data class JdsStoreYearMonth(var k: Long = 0, var v: String? = null)

data class JdsStoreMonthDay(var k: Long = 0, var v: String? = null)

data class JdsStorePeriod(var k: Long = 0, var v: String? = null)

/**
 *
 * @param compositeKey composite key
 * @param uuid uuid
 * @param uuidLocation uuid location
 * @param editVersion uuid location version
 * @param entityId entity id
 * @param fieldId field id
 * @param live live
 * @param version version
 */
data class JdsEntityOverview(var uuid: String = "",
                             var editVersion: Int = 0,
                             var entityId: Long = 0,
                             var fieldId: Long? = null,
                             var version: Long = 0)

/**
 * @param entities a collection of [JdsEntity] objects to store in a portable manner
 */
class JdsEmbeddedContainer(entities: Iterable<JdsEntity>) {

    //empty constructor needed for json serialization
    constructor() : this(emptyList())

    /**
     * Embedded objects
     */
    val e: MutableList<JdsEmbeddedObject> = ArrayList()

    init {
        entities.forEach {
            val classHasAnnotation = it.javaClass.isAnnotationPresent(JdsEntityAnnotation::class.java)
            val superclassHasAnnotation = it.javaClass.superclass.isAnnotationPresent(JdsEntityAnnotation::class.java)
            if (classHasAnnotation || superclassHasAnnotation) {
                val eb = JdsEmbeddedObject()
                eb.fieldId = null
                eb.init(it)
                e.add(eb)
            } else {
                throw RuntimeException("You must annotate the class [" + it.javaClass.canonicalName + "] or its parent with [" + JdsEntityAnnotation::class.java + "]")
            }
        }
    }
}