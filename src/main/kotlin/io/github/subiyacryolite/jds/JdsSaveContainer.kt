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
package io.github.subiyacryolite.jds

import javafx.beans.property.*
import javafx.beans.value.WritableValue
import java.time.Duration
import java.time.LocalDateTime
import java.time.MonthDay
import java.time.Period
import java.time.temporal.Temporal
import java.util.*

/**
 * Helper class used when performing [JdsEntity] saves
 */
class JdsSaveContainer {
    //time constructs
    val localDateTimeProperties: MutableList<HashMap<String, HashMap<Long, ObjectProperty<Temporal>>>> = ArrayList()
    val localDateProperties: MutableList<HashMap<String, HashMap<Long, ObjectProperty<Temporal>>>> = ArrayList()
    val localTimeProperties: MutableList<HashMap<String, HashMap<Long, ObjectProperty<Temporal>>>> = ArrayList()
    val zonedDateTimeProperties: MutableList<HashMap<String, HashMap<Long, ObjectProperty<Temporal>>>> = ArrayList()
    val monthDayProperties: MutableList<HashMap<String, HashMap<Long, ObjectProperty<MonthDay>>>> = ArrayList()
    val yearMonthProperties: MutableList<HashMap<String, HashMap<Long, ObjectProperty<Temporal>>>> = ArrayList()
    val periodProperties: MutableList<HashMap<String, HashMap<Long, ObjectProperty<Period>>>> = ArrayList()
    val durationProperties: MutableList<HashMap<String, HashMap<Long, ObjectProperty<Duration>>>> = ArrayList()
    //strings
    val stringProperties: MutableList<HashMap<String, HashMap<Long, StringProperty>>> = ArrayList()
    //primitives
    val booleanProperties: MutableList<HashMap<String, HashMap<Long, WritableValue<Boolean>>>> = ArrayList()
    val floatProperties: MutableList<HashMap<String, HashMap<Long, WritableValue<Float>>>> = ArrayList()
    val doubleProperties: MutableList<HashMap<String, HashMap<Long, WritableValue<Double>>>> = ArrayList()
    val longProperties: MutableList<HashMap<String, HashMap<Long, WritableValue<Long>>>> = ArrayList()
    val integerProperties: MutableList<HashMap<String, HashMap<Long, WritableValue<Int>>>> = ArrayList()
    //blobs
    val blobProperties: MutableList<HashMap<String, HashMap<Long, BlobProperty>>> = ArrayList()
    //arrays
    val objectCollections: MutableList<HashMap<String, HashMap<JdsFieldEntity<*>, MutableCollection<JdsEntity>>>> = ArrayList()
    val stringCollections: MutableList<HashMap<String, HashMap<Long, MutableCollection<String>>>> = ArrayList()
    val localDateTimeCollections: MutableList<HashMap<String, HashMap<Long, MutableCollection<LocalDateTime>>>> = ArrayList()
    val floatCollections: MutableList<HashMap<String, HashMap<Long, MutableCollection<Float>>>> = ArrayList()
    val doubleCollections: MutableList<HashMap<String, HashMap<Long, MutableCollection<Double>>>> = ArrayList()
    val longCollections: MutableList<HashMap<String, HashMap<Long, MutableCollection<Long>>>> = ArrayList()
    val integerCollections: MutableList<HashMap<String, HashMap<Long, MutableCollection<Int>>>> = ArrayList()
    //enumProperties
    val enumProperties: MutableList<HashMap<String, HashMap<JdsFieldEnum<*>, ObjectProperty<Enum<*>>>>> = ArrayList()
    val enumCollections: MutableList<HashMap<String, HashMap<JdsFieldEnum<*>, MutableCollection<Enum<*>>>>> = ArrayList()
    //objects
    val objects: MutableList<HashMap<String, HashMap<JdsFieldEntity<*>, ObjectProperty<JdsEntity>>>> = ArrayList()

    internal fun reset() {
        //time constructs
        localDateTimeProperties.clear()
        localDateProperties.clear()
        localTimeProperties.clear()
        zonedDateTimeProperties.clear()
        monthDayProperties.clear()
        yearMonthProperties.clear()
        periodProperties.clear()
        durationProperties.clear()
        //strings
        stringProperties.clear()
        //primitives
        booleanProperties.clear()
        floatProperties.clear()
        doubleProperties.clear()
        longProperties.clear()
        integerProperties.clear()
        //blobs
        blobProperties.clear()
        //arrays
        objectCollections.clear()
        stringCollections.clear()
        localDateTimeCollections.clear()
        floatCollections.clear()
        doubleCollections.clear()
        longCollections.clear()
        integerCollections.clear()
        //enumProperties
        enumProperties.clear()
        enumCollections.clear()
        //objects
        objects.clear()
    }

    internal fun reset(step: Int) {
        //time constructs
        localDateTimeProperties[step].clear()
        localDateProperties[step].clear()
        localTimeProperties[step].clear()
        zonedDateTimeProperties[step].clear()
        monthDayProperties[step].clear()
        yearMonthProperties[step].clear()
        periodProperties[step].clear()
        durationProperties[step].clear()
        //strings
        stringProperties[step].clear()
        //primitives
        booleanProperties[step].clear()
        floatProperties[step].clear()
        doubleProperties[step].clear()
        longProperties[step].clear()
        integerProperties[step].clear()
        //blobs
        blobProperties[step].clear()
        //arrays
        objectCollections[step].clear()
        stringCollections[step].clear()
        localDateTimeCollections[step].clear()
        floatCollections[step].clear()
        doubleCollections[step].clear()
        longCollections[step].clear()
        integerCollections[step].clear()
        //enumProperties
        enumProperties[step].clear()
        enumCollections[step].clear()
        //objects
        objects[step].clear()
    }
}
