/*
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

import com.javaworld.NamedCallableStatement
import com.javaworld.NamedPreparedStatement
import io.github.subiyacryolite.jds.annotations.JdsEntityAnnotation
import io.github.subiyacryolite.jds.embedded.*
import io.github.subiyacryolite.jds.enums.JdsFieldType
import javafx.beans.property.*
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.sql.Connection
import java.sql.Timestamp
import java.time.*
import java.time.temporal.Temporal
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * This class allows for all mapping operations in JDS, it also uses
 * [JdsEntityBase] to store overview data
 */
abstract class JdsEntity : IJdsEntity {

    override var overview: IJdsOverview = JdsOverview()

    override var entityName: String = ""


    //fieldEntity and enum maps
    private val fields: MutableSet<JdsField> = HashSet()
    private val objects: MutableSet<Long> = HashSet()
    private val enums: MutableSet<JdsFieldEnum<*>> = HashSet()
    //time constructs
    private val localDateTimeProperties: HashMap<Long, SimpleObjectProperty<Temporal>> = HashMap()
    private val zonedDateTimeProperties: HashMap<Long, SimpleObjectProperty<Temporal>> = HashMap()
    private val localDateProperties: HashMap<Long, SimpleObjectProperty<Temporal>> = HashMap()
    private val localTimeProperties: HashMap<Long, SimpleObjectProperty<Temporal>> = HashMap()
    private val monthDayProperties: HashMap<Long, SimpleObjectProperty<MonthDay>> = HashMap()
    private val yearMonthProperties: HashMap<Long, SimpleObjectProperty<Temporal>> = HashMap()
    private val periodProperties: HashMap<Long, SimpleObjectProperty<Period>> = HashMap()
    private val durationProperties: HashMap<Long, SimpleObjectProperty<Duration>> = HashMap()
    //strings
    private val stringProperties: HashMap<Long, SimpleStringProperty> = HashMap()
    //numeric
    private val floatProperties: HashMap<Long, SimpleFloatProperty> = HashMap()
    private val doubleProperties: HashMap<Long, SimpleDoubleProperty> = HashMap()
    private val booleanProperties: HashMap<Long, SimpleBooleanProperty> = HashMap()
    private val longProperties: HashMap<Long, SimpleLongProperty> = HashMap()
    private val integerProperties: HashMap<Long, SimpleIntegerProperty> = HashMap()
    //arrays
    private val objectArrayProperties: HashMap<JdsFieldEntity<*>, SimpleListProperty<JdsEntity>> = HashMap()
    private val stringArrayProperties: HashMap<Long, SimpleListProperty<String>> = HashMap()
    private val dateTimeArrayProperties: HashMap<Long, SimpleListProperty<LocalDateTime>> = HashMap()
    private val floatArrayProperties: HashMap<Long, SimpleListProperty<Float>> = HashMap()
    private val doubleArrayProperties: HashMap<Long, SimpleListProperty<Double>> = HashMap()
    private val longArrayProperties: HashMap<Long, SimpleListProperty<Long>> = HashMap()
    private val integerArrayProperties: HashMap<Long, SimpleListProperty<Int>> = HashMap()
    //enumProperties
    private val enumProperties: HashMap<JdsFieldEnum<*>, SimpleObjectProperty<Enum<*>>> = HashMap()
    private val enumCollectionProperties: HashMap<JdsFieldEnum<*>, SimpleListProperty<Enum<*>>> = HashMap()
    //objects
    private val objectProperties: HashMap<JdsFieldEntity<*>, SimpleObjectProperty<JdsEntity>> = HashMap()
    private val objectCascade: HashMap<JdsFieldEntity<*>, Boolean> = HashMap()
    //blobs
    private val blobProperties: HashMap<Long, SimpleBlobProperty> = HashMap()


    init {
        if (javaClass.isAnnotationPresent(JdsEntityAnnotation::class.java)) {
            val entityAnnotation = javaClass.getAnnotation(JdsEntityAnnotation::class.java)
            entityName = entityAnnotation.entityName
            overview.entityId = entityAnnotation.entityId
            overview.version = entityAnnotation.version
        } else {
            throw RuntimeException("You must annotate the class [" + javaClass.canonicalName + "] with [" + JdsEntityAnnotation::class.java + "]")
        }
    }

    /**
     * @param jdsField
     * @param integerProperty
     */
    protected fun map(jdsField: JdsField, integerProperty: SimpleBlobProperty) {
        if (jdsField.type != JdsFieldType.BLOB)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        blobProperties.put(jdsField.id, integerProperty)
    }

    /**
     * @param jdsField
     * @param integerProperty
     */
    protected fun map(jdsField: JdsField, integerProperty: SimpleIntegerProperty) {
        if (jdsField.type != JdsFieldType.INT)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        integerProperties.put(jdsField.id, integerProperty)
    }

    protected fun mapMonthDay(jdsField: JdsField, property: SimpleObjectProperty<MonthDay>) {
        if (jdsField.type != JdsFieldType.MONTH_DAY)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        monthDayProperties.put(jdsField.id, property)
    }

    protected fun mapPeriod(jdsField: JdsField, property: SimpleObjectProperty<Period>) {
        if (jdsField.type != JdsFieldType.PERIOD)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        periodProperties.put(jdsField.id, property)
    }

    protected fun mapDuration(jdsField: JdsField, property: SimpleObjectProperty<Duration>) {
        if (jdsField.type != JdsFieldType.DURATION)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        durationProperties.put(jdsField.id, property)
    }

    /**
     * @param jdsField
     * @param temporalProperty
     */
    protected fun map(jdsField: JdsField, temporalProperty: SimpleObjectProperty<out Temporal>) {
        val temporal = temporalProperty.get()
        when (temporal) {
            is LocalDateTime -> {
                if (jdsField.type != JdsFieldType.DATE_TIME)
                    throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
                fields.add(jdsField)
                localDateTimeProperties.put(jdsField.id, temporalProperty as SimpleObjectProperty<Temporal>)
            }
            is ZonedDateTime -> {
                if (jdsField.type != JdsFieldType.ZONED_DATE_TIME)
                    throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
                fields.add(jdsField)
                zonedDateTimeProperties.put(jdsField.id, temporalProperty as SimpleObjectProperty<Temporal>)
            }
            is LocalDate -> {
                if (jdsField.type != JdsFieldType.DATE)
                    throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
                fields.add(jdsField)
                localDateProperties.put(jdsField.id, temporalProperty as SimpleObjectProperty<Temporal>)
            }
            is LocalTime -> {
                if (jdsField.type != JdsFieldType.TIME)
                    throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
                fields.add(jdsField)
                localTimeProperties.put(jdsField.id, temporalProperty as SimpleObjectProperty<Temporal>)
            }
            is YearMonth -> {
                if (jdsField.type != JdsFieldType.YEAR_MONTH)
                    throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
                fields.add(jdsField)
                yearMonthProperties.put(jdsField.id, temporalProperty as SimpleObjectProperty<Temporal>)

            }
        }
    }

    /**
     * @param jdsField
     * @param property
     */
    protected fun map(jdsField: JdsField, property: SimpleStringProperty) {
        if (jdsField.type != JdsFieldType.TEXT)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        stringProperties.put(jdsField.id, property)

    }

    /**
     * @param jdsField
     * @param property
     */
    protected fun map(jdsField: JdsField, property: SimpleFloatProperty) {
        if (jdsField.type != JdsFieldType.FLOAT)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        floatProperties.put(jdsField.id, property)
    }

    /**
     * @param jdsField
     * @param property
     */
    protected fun map(jdsField: JdsField, property: SimpleLongProperty) {
        if (jdsField.type != JdsFieldType.LONG)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        longProperties.put(jdsField.id, property)
    }

    /**
     * @param jdsField
     * @param property
     */
    protected fun map(jdsField: JdsField, property: SimpleDoubleProperty) {
        if (jdsField.type != JdsFieldType.DOUBLE)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        doubleProperties.put(jdsField.id, property)

    }

    /**
     * @param jdsField
     * @param property
     */
    protected fun map(jdsField: JdsField, property: SimpleBooleanProperty) {
        if (jdsField.type != JdsFieldType.BOOLEAN)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        booleanProperties.put(jdsField.id, property)
    }

    /**
     * @param jdsField
     * @param properties
     */
    protected fun mapStrings(jdsField: JdsField, properties: SimpleListProperty<String>) {
        if (jdsField.type != JdsFieldType.ARRAY_TEXT)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        stringArrayProperties.put(jdsField.id, properties)
    }

    /**
     * @param jdsField
     * @param properties
     */
    protected fun mapDateTimes(jdsField: JdsField, properties: SimpleListProperty<LocalDateTime>) {
        if (jdsField.type != JdsFieldType.ARRAY_DATE_TIME)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        dateTimeArrayProperties.put(jdsField.id, properties)
    }

    /**
     * @param jdsField
     * @param properties
     */
    protected fun mapFloats(jdsField: JdsField, properties: SimpleListProperty<Float>) {
        if (jdsField.type != JdsFieldType.ARRAY_FLOAT)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        floatArrayProperties.put(jdsField.id, properties)
    }

    /**
     * @param jdsField
     * @param properties
     */
    protected fun mapIntegers(jdsField: JdsField, properties: SimpleListProperty<Int>) {
        if (jdsField.type != JdsFieldType.ARRAY_INT)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        integerArrayProperties.put(jdsField.id, properties)
    }

    /**
     * @param jdsField
     * @param properties
     */
    protected fun mapDoubles(jdsField: JdsField, properties: SimpleListProperty<Double>) {
        if (jdsField.type != JdsFieldType.ARRAY_DOUBLE)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        doubleArrayProperties.put(jdsField.id, properties)
    }

    /**
     * @param jdsField
     * @param properties
     */
    protected fun mapLongs(jdsField: JdsField, properties: SimpleListProperty<Long>) {
        if (jdsField.type != JdsFieldType.ARRAY_LONG)
            throw RuntimeException("Please set jdsField [$jdsField] to the correct type")
        fields.add(jdsField)
        longArrayProperties.put(jdsField.id, properties)
    }

    /**
     * @param jdsFieldEnum
     * @param property
     */
    protected fun map(jdsFieldEnum: JdsFieldEnum<*>, property: SimpleObjectProperty<out Enum<*>>) {
        if (jdsFieldEnum.field.type != JdsFieldType.ENUM)
            throw RuntimeException("Please set fieldEntity [$jdsFieldEnum] to the correct type")
        enums.add(jdsFieldEnum)
        fields.add(jdsFieldEnum.field)
        enumProperties.put(jdsFieldEnum, property as SimpleObjectProperty<Enum<*>>)
    }

    /**
     * @param jdsFieldEnum
     * @param properties
     */
    protected fun mapEnums(jdsFieldEnum: JdsFieldEnum<*>, properties: SimpleListProperty<out Enum<*>>) {
        if (jdsFieldEnum.field.type != JdsFieldType.ENUM_COLLECTION)
            throw RuntimeException("Please set fieldEntity [$jdsFieldEnum] to the correct type")
        enums.add(jdsFieldEnum)
        fields.add(jdsFieldEnum.field)
        enumCollectionProperties.put(jdsFieldEnum, properties as SimpleListProperty<Enum<*>>)

    }

    /**
     * @param entity
     * @param property
     * @param cascadeOnDelete
     */
    protected fun <T : IJdsEntity> map(fieldEntity: JdsFieldEntity<T>, property: SimpleObjectProperty<T>, cascadeOnDelete: Boolean) {
        if (fieldEntity.fieldEntity.type != JdsFieldType.CLASS)
            throw RuntimeException("Please supply a valid type for JdsFieldEntity")
        if (fieldEntity.entityType.isAnnotationPresent(JdsEntityAnnotation::class.java)) {
            if (!objectArrayProperties.containsKey(fieldEntity) && !objectProperties.containsKey(fieldEntity)) {
                val entityAnnotation = fieldEntity.entityType.getAnnotation(JdsEntityAnnotation::class.java)
                objectProperties.put(fieldEntity, property as SimpleObjectProperty<JdsEntity>)
                objects.add(entityAnnotation.entityId)
                objectCascade.put(fieldEntity, cascadeOnDelete)
            } else {
                throw RuntimeException("You can only bind a class to one property. This class is already bound to one object or object array")
            }
        } else {
            throw RuntimeException("You must annotate the class [" + fieldEntity.entityType.canonicalName + "] with [" + JdsEntityAnnotation::class.java + "]")
        }
    }

    /**
     * @param entity
     * @param property
     */
    protected fun <T : IJdsEntity> map(fieldEntity: JdsFieldEntity<T>, property: SimpleObjectProperty<T>) {
        map(fieldEntity, property, false)
    }

    /**
     * @param entity
     * @param properties
     * @param cascadeOnDelete
     */
    protected fun <T : IJdsEntity> map(fieldEntity: JdsFieldEntity<T>, properties: SimpleListProperty<T>, cascadeOnDelete: Boolean) {
        if (fieldEntity.fieldEntity.type != JdsFieldType.CLASS)
            throw RuntimeException("Please supply a valid type for JdsFieldEntity")
        if (fieldEntity.entityType.isAnnotationPresent(JdsEntityAnnotation::class.java)) {
            if (!objectArrayProperties.containsKey(fieldEntity)) {
                val entityAnnotation = fieldEntity.entityType.getAnnotation(JdsEntityAnnotation::class.java)
                objectArrayProperties.put(fieldEntity, properties as SimpleListProperty<JdsEntity>)
                objects.add(entityAnnotation.entityId)
                objectCascade.put(fieldEntity, cascadeOnDelete)
            } else {
                throw RuntimeException("You can only bind a class to one property. This class is already bound to one object or object array")
            }
        } else {
            throw RuntimeException("You must annotate the class [" + fieldEntity.entityType.canonicalName + "] with [" + JdsEntityAnnotation::class.java + "]")
        }
    }

    /**
     * @param fieldEntity
     * @param properties
     */
    protected fun <T : IJdsEntity> map(fieldEntity: JdsFieldEntity<T>, properties: SimpleListProperty<T>) {
        map(fieldEntity, properties, false)
    }

    /**
     * Copy values from matching fields found in both objects
     *
     * @param source The entity to copy values from
     * @param <T>    A valid JDSEntity
    </T> */
    fun <T : JdsEntity> copy(source: T) {
        copyHeaderValues(source)
        copyPropertyValues(source)
        copyArrayValues(source)
        copyEnumValues(source)
        copyObjectAndObjectArrayValues(source)
    }

    /**
     * Copy all header overview information
     *
     * @param source The entity to copy values from
     * @param <T>    A valid JDSEntity
    </T> */
    private fun <T : IJdsEntity> copyArrayValues(source: T) {
        overview.dateCreated = source.overview.dateCreated
        overview.dateModified = source.overview.dateModified
        overview.entityGuid = source.overview.entityGuid
    }

    /**
     * Copy all property values
     *
     * @param source The entity to copy values from
     * @param <T>    A valid JDSEntity A valid JDSEntity
    </T> */
    private fun <T : JdsEntity> copyPropertyValues(source: T) {
        val dest = this
        source.booleanProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.booleanProperties.containsKey(srcEntry.key)) {
                dest.booleanProperties[srcEntry.key]?.set(srcEntry.value.get())
            }
        }
        source.localDateTimeProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.localDateTimeProperties.containsKey(srcEntry.key)) {
                dest.localDateTimeProperties[srcEntry.key]?.set(srcEntry.value.get())
            }
        }
        source.zonedDateTimeProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.zonedDateTimeProperties.containsKey(srcEntry.key)) {
                dest.zonedDateTimeProperties[srcEntry.key]?.set(srcEntry.value.get())
            }
        }
        source.localTimeProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.localTimeProperties.containsKey(srcEntry.key)) {
                dest.localTimeProperties[srcEntry.key]?.set(srcEntry.value.get())
            }
        }
        source.localDateProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.localDateProperties.containsKey(srcEntry.key)) {
                dest.localDateProperties[srcEntry.key]?.set(srcEntry.value.get())
            }
        }
        source.stringProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.stringProperties.containsKey(srcEntry.key)) {
                dest.stringProperties[srcEntry.key]?.set(srcEntry.value.get())
            }
        }
        source.floatProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.floatProperties.containsKey(srcEntry.key)) {
                dest.floatProperties[srcEntry.key]?.set(srcEntry.value.get())
            }
        }
        source.doubleProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.doubleProperties.containsKey(srcEntry.key)) {
                dest.doubleProperties[srcEntry.key]?.set(srcEntry.value.get())
            }
        }
        source.longProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.longProperties.containsKey(srcEntry.key)) {
                dest.longProperties[srcEntry.key]?.set(srcEntry.value.get())
            }
        }
        source.integerProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.integerProperties.containsKey(srcEntry.key)) {
                dest.integerProperties[srcEntry.key]?.set(srcEntry.value.get())
            }
        }
        source.blobProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.blobProperties.containsKey(srcEntry.key)) {
                dest.blobProperties[srcEntry.key]?.set(srcEntry.value.get()!!)
            }
        }
    }

    /**
     * Copy all property array values
     *
     * @param source The entity to copy values from
     * @param <T>    A valid JDSEntity A valid JDSEntity
    </T> */
    private fun <T : JdsEntity> copyHeaderValues(source: T) {
        val dest = this
        source.stringArrayProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.stringArrayProperties.containsKey(srcEntry.key)) {
                val entry = dest.stringArrayProperties[srcEntry.key]
                entry?.clear()
                entry?.set(srcEntry.value.get())
            }
        }
        source.dateTimeArrayProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.dateTimeArrayProperties.containsKey(srcEntry.key)) {
                val entry = dest.dateTimeArrayProperties[srcEntry.key]
                entry?.clear()
                entry?.set(srcEntry.value.get())
            }
        }
        source.floatArrayProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.floatArrayProperties.containsKey(srcEntry.key)) {
                val entry = dest.floatArrayProperties[srcEntry.key]
                entry?.clear()
                entry?.set(srcEntry.value.get())
            }
        }
        source.doubleArrayProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.doubleArrayProperties.containsKey(srcEntry.key)) {
                val entry = dest.doubleArrayProperties[srcEntry.key]
                entry?.clear()
                entry?.set(srcEntry.value.get())
            }
        }
        source.longArrayProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.longArrayProperties.containsKey(srcEntry.key)) {
                val entry = dest.longArrayProperties[srcEntry.key]
                entry?.clear()
                entry?.set(srcEntry.value.get())
            }
        }
        source.integerArrayProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.integerArrayProperties.containsKey(srcEntry.key)) {
                val entry = dest.integerArrayProperties[srcEntry.key]
                entry?.clear()
                entry?.set(srcEntry.value.get())
            }
        }
    }

    /**
     * Copy over object and object array values
     *
     * @param source The entity to copy values from
     * @param <T>    A valid JDSEntity
    </T> */
    private fun <T : JdsEntity> copyObjectAndObjectArrayValues(source: T) {
        val dest = this
        source.objectProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.objectProperties.containsKey(srcEntry.key)) {
                dest.objectProperties[srcEntry.key]?.set(srcEntry.value.get())
            }
        }

        source.objectArrayProperties.entries.parallelStream().forEach { srcEntry ->
            if (dest.objectArrayProperties.containsKey(srcEntry.key)) {
                val entry = dest.objectArrayProperties[srcEntry.key]
                entry?.clear()
                entry?.set(srcEntry.value.get())
            }
        }
    }

    /**
     * Copy over object enum values
     *
     * @param source The entity to copy values from
     * @param <T>    A valid JDSEntity
    </T> */
    private fun <T : JdsEntity> copyEnumValues(source: T) {
        val dest = this
        source.enumCollectionProperties.entries.parallelStream().forEach { srcEntry ->
            val key = srcEntry.key
            if (dest.enumCollectionProperties.containsKey(key)) {
                val dstEntry = dest.enumCollectionProperties[srcEntry.key]
                dstEntry?.clear()
                val it = srcEntry.value.iterator()
                while (it.hasNext()) {
                    val nxt = it.next()
                    dstEntry?.add(nxt)
                }
            }
        }
    }


    @Throws(IOException::class)
    override fun writeExternal(objectOutputStream: ObjectOutput) {
        //fieldEntity and enum maps
        objectOutputStream.writeObject(overview)
        objectOutputStream.writeObject(fields)
        objectOutputStream.writeObject(objects)
        objectOutputStream.writeObject(enums)
        objectOutputStream.writeUTF(entityName)
        //objects
        objectOutputStream.writeObject(serializeObject(objectProperties))
        //time constructs
        objectOutputStream.writeObject(serializeTemporal(localDateTimeProperties))
        objectOutputStream.writeObject(serializeTemporal(zonedDateTimeProperties))
        objectOutputStream.writeObject(serializeTemporal(localDateProperties))
        objectOutputStream.writeObject(serializeTemporal(localTimeProperties))
        objectOutputStream.writeObject(monthDayProperties)
        objectOutputStream.writeObject(yearMonthProperties)
        objectOutputStream.writeObject(periodProperties)
        objectOutputStream.writeObject(durationProperties)
        //strings
        objectOutputStream.writeObject(serializableString(stringProperties))
        //numeric
        objectOutputStream.writeObject(serializeFloat(floatProperties))
        objectOutputStream.writeObject(serializeDouble(doubleProperties))
        objectOutputStream.writeObject(serializeBoolean(booleanProperties))
        objectOutputStream.writeObject(serializeLong(longProperties))
        objectOutputStream.writeObject(serializeInteger(integerProperties))
        //blobs
        objectOutputStream.writeObject(serializeBlobs(blobProperties))
        //arrays
        objectOutputStream.writeObject(serializeObjects(objectArrayProperties))
        objectOutputStream.writeObject(serializeStrings(stringArrayProperties))
        objectOutputStream.writeObject(serializeDateTimes(dateTimeArrayProperties))
        objectOutputStream.writeObject(serializeFloats(floatArrayProperties))
        objectOutputStream.writeObject(serializeDoubles(doubleArrayProperties))
        objectOutputStream.writeObject(serializeLongs(longArrayProperties))
        objectOutputStream.writeObject(serializeIntegers(integerArrayProperties))
        //enumProperties
        objectOutputStream.writeObject(serializeEnums(enumProperties))
        objectOutputStream.writeObject(serializeEnumCollections(enumCollectionProperties))
    }

    private fun serializeEnums(input: Map<JdsFieldEnum<*>, SimpleObjectProperty<Enum<*>>>): Map<JdsFieldEnum<*>, Enum<*>> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeEnumCollections(input: Map<JdsFieldEnum<*>, SimpleListProperty<Enum<*>>>): Map<JdsFieldEnum<*>, List<Enum<*>>> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeBlobs(input: Map<Long, SimpleBlobProperty>): Map<Long, SimpleBlobProperty> {
        return input.entries.associateBy({ it.key }, { it.value })
    }

    private fun serializeIntegers(input: Map<Long, SimpleListProperty<Int>>): Map<Long, List<Int>> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeLongs(input: Map<Long, SimpleListProperty<Long>>): Map<Long, List<Long>> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeDoubles(input: Map<Long, SimpleListProperty<Double>>): Map<Long, List<Double>> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeFloats(input: Map<Long, SimpleListProperty<Float>>): Map<Long, List<Float>> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeDateTimes(input: Map<Long, SimpleListProperty<LocalDateTime>>): Map<Long, List<LocalDateTime>> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeStrings(input: Map<Long, SimpleListProperty<String>>): Map<Long, List<String>> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeObjects(input: Map<JdsFieldEntity<*>, SimpleListProperty<JdsEntity>>): Map<JdsFieldEntity<*>, List<JdsEntity>> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeObject(input: Map<JdsFieldEntity<*>, SimpleObjectProperty<JdsEntity>>): Map<JdsFieldEntity<*>, JdsEntity> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeFloat(input: Map<Long, SimpleFloatProperty>): Map<Long, Float> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeDouble(input: Map<Long, SimpleDoubleProperty>): Map<Long, Double> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeBoolean(input: Map<Long, SimpleBooleanProperty>): Map<Long, Boolean> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeLong(input: Map<Long, SimpleLongProperty>): Map<Long, Long> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeInteger(input: Map<Long, SimpleIntegerProperty>): Map<Long, Int> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializeTemporal(input: Map<Long, SimpleObjectProperty<out Temporal>>): Map<Long, Temporal> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    private fun serializableString(input: Map<Long, SimpleStringProperty>): Map<Long, String> {
        return input.entries.associateBy({ it.key }, { it.value.get() })
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    override fun readExternal(objectInputStream: ObjectInput) {
        //fieldEntity and enum maps
        overview = objectInputStream.readObject() as JdsOverview
        fields.addAll(objectInputStream.readObject() as Set<JdsField>)
        objects.addAll(objectInputStream.readObject() as Set<Long>)
        enums.addAll(objectInputStream.readObject() as Set<JdsFieldEnum<*>>)
        entityName = objectInputStream.readUTF()
        //objects
        putObject(objectProperties, objectInputStream.readObject() as Map<JdsFieldEntity<*>, JdsEntity>)
        //time constructs
        putTemporal(localDateTimeProperties, objectInputStream.readObject() as Map<Long, Temporal>)
        putTemporal(zonedDateTimeProperties, objectInputStream.readObject() as Map<Long, Temporal>)
        putTemporal(localDateProperties, objectInputStream.readObject() as Map<Long, Temporal>)
        putTemporal(localTimeProperties, objectInputStream.readObject() as Map<Long, Temporal>)
        putMonthDays(monthDayProperties, objectInputStream.readObject() as Map<Long, MonthDay>)
        putYearMonths(yearMonthProperties, objectInputStream.readObject() as Map<Long, Temporal>)
        putPeriods(periodProperties, objectInputStream.readObject() as Map<Long, Period>)
        putDurations(durationProperties, objectInputStream.readObject() as Map<Long, Duration>)
        //string
        putString(stringProperties, objectInputStream.readObject() as Map<Long, String>)
        //numeric
        putFloat(floatProperties, objectInputStream.readObject() as Map<Long, Float>)
        putDouble(doubleProperties, objectInputStream.readObject() as Map<Long, Double>)
        putBoolean(booleanProperties, objectInputStream.readObject() as Map<Long, Boolean>)
        putLong(longProperties, objectInputStream.readObject() as Map<Long, Long>)
        putInteger(integerProperties, objectInputStream.readObject() as Map<Long, Int>)
        //blobs
        putBlobs(blobProperties, objectInputStream.readObject() as Map<Long, SimpleBlobProperty>)
        //arrays
        putObjects(objectArrayProperties, objectInputStream.readObject() as Map<JdsFieldEntity<*>, List<JdsEntity>>)
        putStrings(stringArrayProperties, objectInputStream.readObject() as Map<Long, List<String>>)
        putDateTimes(dateTimeArrayProperties, objectInputStream.readObject() as Map<Long, List<LocalDateTime>>)
        putFloats(floatArrayProperties, objectInputStream.readObject() as Map<Long, List<Float>>)
        putDoubles(doubleArrayProperties, objectInputStream.readObject() as Map<Long, List<Double>>)
        putLongs(longArrayProperties, objectInputStream.readObject() as Map<Long, List<Long>>)
        putIntegers(integerArrayProperties, objectInputStream.readObject() as Map<Long, List<Int>>)
        //enumProperties
        putEnum(enumProperties, objectInputStream.readObject() as Map<JdsFieldEnum<*>, Enum<*>>)
        putEnums(enumCollectionProperties, objectInputStream.readObject() as Map<JdsFieldEnum<*>, List<Enum<*>>>)
    }

    private fun putDurations(destination: HashMap<Long, SimpleObjectProperty<Duration>>, source: Map<Long, Duration>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.set(entry.value) }
    }

    private fun putPeriods(destination: HashMap<Long, SimpleObjectProperty<Period>>, source: Map<Long, Period>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.set(entry.value) }
    }

    private fun putYearMonths(destination: HashMap<Long, SimpleObjectProperty<Temporal>>, source: Map<Long, Temporal>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.set(entry.value) }
    }

    private fun putMonthDays(destination: HashMap<Long, SimpleObjectProperty<MonthDay>>, source: Map<Long, MonthDay>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.set(entry.value) }
    }

    private fun putEnums(destination: Map<JdsFieldEnum<*>, SimpleListProperty<Enum<*>>>, source: Map<JdsFieldEnum<*>, List<Enum<*>>>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.addAll(entry.value) }
    }

    private fun putEnum(destination: Map<JdsFieldEnum<*>, SimpleObjectProperty<Enum<*>>>, source: Map<JdsFieldEnum<*>, Enum<*>>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.set(entry.value) }
    }

    private fun putObjects(destination: Map<JdsFieldEntity<*>, SimpleListProperty<JdsEntity>>, source: Map<JdsFieldEntity<*>, List<JdsEntity>>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.addAll(entry.value) }
    }

    private fun putStrings(destination: Map<Long, SimpleListProperty<String>>, source: Map<Long, List<String>>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.addAll(entry.value) }
    }

    private fun putDateTimes(destination: Map<Long, SimpleListProperty<LocalDateTime>>, source: Map<Long, List<LocalDateTime>>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.addAll(entry.value) }
    }

    private fun putFloats(destination: Map<Long, SimpleListProperty<Float>>, source: Map<Long, List<Float>>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.addAll(entry.value) }
    }

    private fun putDoubles(destination: Map<Long, SimpleListProperty<Double>>, source: Map<Long, List<Double>>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.addAll(entry.value) }
    }

    private fun putLongs(destination: Map<Long, SimpleListProperty<Long>>, source: Map<Long, List<Long>>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.addAll(entry.value) }
    }

    private fun putIntegers(destination: Map<Long, SimpleListProperty<Int>>, source: Map<Long, List<Int>>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.addAll(entry.value) }
    }

    private fun putInteger(destination: Map<Long, SimpleIntegerProperty>, source: Map<Long, Int>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.set(entry.value) }
    }

    private fun putBlobs(destination: Map<Long, SimpleBlobProperty>, source: Map<Long, SimpleBlobProperty>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.set(entry.value.get()!!) }
    }

    private fun putLong(destination: Map<Long, SimpleLongProperty>, source: Map<Long, Long>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.set(entry.value) }
    }

    private fun putBoolean(destination: Map<Long, SimpleBooleanProperty>, source: Map<Long, Boolean>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.set(entry.value) }
    }

    private fun putDouble(destination: Map<Long, SimpleDoubleProperty>, source: Map<Long, Double>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.set(entry.value) }
    }

    private fun putObject(destination: Map<JdsFieldEntity<*>, SimpleObjectProperty<JdsEntity>>, source: Map<JdsFieldEntity<*>, JdsEntity>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.set(entry.value) }
    }

    private fun putFloat(destination: Map<Long, SimpleFloatProperty>, source: Map<Long, Float>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.set(entry.value) }
    }

    private fun putTemporal(destination: Map<Long, SimpleObjectProperty<Temporal>>, source: Map<Long, Temporal>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.set(entry.value) }
    }

    private fun putString(destination: Map<Long, SimpleStringProperty>, source: Map<Long, String>) {
        source.entries.stream().filter { entry -> destination.containsKey(entry.key) }.forEachOrdered { entry -> destination[entry.key]?.set(entry.value) }
    }

    internal fun assign(step: Int, saveContainer: JdsSaveContainer) {
        //==============================================
        //PRIMITIVES
        //==============================================
        saveContainer.booleanProperties[step].put(overview.entityGuid, booleanProperties)
        saveContainer.stringProperties[step].put(overview.entityGuid, stringProperties)
        saveContainer.floatProperties[step].put(overview.entityGuid, floatProperties)
        saveContainer.doubleProperties[step].put(overview.entityGuid, doubleProperties)
        saveContainer.longProperties[step].put(overview.entityGuid, longProperties)
        saveContainer.integerProperties[step].put(overview.entityGuid, integerProperties)
        //==============================================
        //Dates & Time
        //==============================================
        saveContainer.localDateTimeProperties[step].put(overview.entityGuid, localDateTimeProperties)
        saveContainer.zonedDateTimeProperties[step].put(overview.entityGuid, zonedDateTimeProperties)
        saveContainer.localTimeProperties[step].put(overview.entityGuid, localTimeProperties)
        saveContainer.localDateProperties[step].put(overview.entityGuid, localDateProperties)
        saveContainer.monthDayProperties[step].put(overview.entityGuid, monthDayProperties)
        saveContainer.yearMonthProperties[step].put(overview.entityGuid, yearMonthProperties)
        saveContainer.periodProperties[step].put(overview.entityGuid, periodProperties)
        saveContainer.durationProperties[step].put(overview.entityGuid, durationProperties)
        //==============================================
        //BLOB
        //==============================================
        saveContainer.blobProperties[step].put(overview.entityGuid, blobProperties)
        //==============================================
        //Enums
        //==============================================
        saveContainer.enumProperties[step].put(overview.entityGuid, enumProperties)
        saveContainer.enumCollections[step].put(overview.entityGuid, enumCollectionProperties)
        //==============================================
        //ARRAYS
        //==============================================
        saveContainer.stringCollections[step].put(overview.entityGuid, stringArrayProperties)
        saveContainer.localDateTimeCollections[step].put(overview.entityGuid, dateTimeArrayProperties)
        saveContainer.floatCollections[step].put(overview.entityGuid, floatArrayProperties)
        saveContainer.doubleCollections[step].put(overview.entityGuid, doubleArrayProperties)
        saveContainer.longCollections[step].put(overview.entityGuid, longArrayProperties)
        saveContainer.integerCollections[step].put(overview.entityGuid, integerArrayProperties)
        //==============================================
        //EMBEDDED OBJECTS
        //==============================================
        saveContainer.objectCollections[step].put(overview.entityGuid, objectArrayProperties)
        saveContainer.objects[step].put(overview.entityGuid, objectProperties)
    }

    internal fun assign(embeddedObject: JdsEmbeddedObject) {
        //==============================================
        //PRIMITIVES
        //==============================================
        booleanProperties.entries.parallelStream().forEach {
            embeddedObject.b.add(JdsBooleanValues(it.key, when (it.value.value) {true -> 1;false -> 0
            }))
        }
        stringProperties.entries.parallelStream().forEach { embeddedObject.s.add(JdsTextValues(it.key, it.value.value)) }
        floatProperties.entries.parallelStream().forEach { embeddedObject.f.add(JdsFloatValues(it.key, it.value.value)) }
        doubleProperties.entries.parallelStream().forEach { embeddedObject.d.add(JdsDoubleValues(it.key, it.value.value)) }
        longProperties.entries.parallelStream().forEach { embeddedObject.l.add(JdsLongValues(it.key, it.value.value)) }
        integerProperties.entries.parallelStream().forEach { embeddedObject.i.add(JdsIntegerValues(it.key, it.value.value)) }
        //==============================================
        //Dates & Time
        //==============================================
        localDateTimeProperties.entries.parallelStream().forEach { embeddedObject.ldt.add(JdsLocalDateTimeValues(it.key, Timestamp.valueOf(it.value.value as LocalDateTime))) }
        zonedDateTimeProperties.entries.parallelStream().forEach { embeddedObject.zdt.add(JdsZonedDateTimeValues(it.key, (it.value.value as ZonedDateTime).toInstant().toEpochMilli())) }
        localTimeProperties.entries.parallelStream().forEach { embeddedObject.t.add(JdsTimeValues(it.key, (it.value.value as LocalTime).toSecondOfDay())) }
        localDateProperties.entries.parallelStream().forEach { embeddedObject.ld.add(JdsLocalDateValues(it.key, Timestamp.valueOf((it.value.value as LocalDate).atStartOfDay()))) }
        durationProperties.entries.parallelStream().forEach { embeddedObject.du.add(JdsDurationValues(it.key, it.value.value.toNanos())) }
        monthDayProperties.entries.parallelStream().forEach { embeddedObject.md.add(JdsMonthDayValues(it.key, it.value.value.toString())) }
        yearMonthProperties.entries.parallelStream().forEach { embeddedObject.ym.add(JdsYearMonthValues(it.key, (it.value.value as YearMonth).toString())) }
        periodProperties.entries.parallelStream().forEach { embeddedObject.p.add(JdsPeriodValues(it.key, it.value.value.toString())) }
        //==============================================
        //BLOB
        //==============================================
        blobProperties.entries.parallelStream().forEach { embeddedObject.bl.add(JdsBlobValues(it.key, it.value.get() ?: ByteArray(0))) }
        //==============================================
        //Enums
        //==============================================
        enumProperties.entries.parallelStream().forEach { embeddedObject.e.add(JdsEnumValues(it.key.field.id, it.value.value.ordinal)) }
        enumCollectionProperties.entries.parallelStream().forEach { it.value.forEachIndexed { i, child -> embeddedObject.ea.add(JdsEnumCollections(it.key.field.id, i, child.ordinal)) } }
        //==============================================
        //ARRAYS
        //==============================================
        stringArrayProperties.entries.parallelStream().forEach { it.value.forEachIndexed { i, child -> embeddedObject.sa.add(JdsTextCollections(it.key, i, child)) } }
        dateTimeArrayProperties.entries.parallelStream().forEach { it.value.forEachIndexed { i, child -> embeddedObject.dta.add(JdsDateCollections(it.key, i, Timestamp.valueOf(child))) } }
        floatArrayProperties.entries.parallelStream().forEach { it.value.forEachIndexed { i, child -> embeddedObject.fa.add(JdsFloatCollections(it.key, i, child)) } }
        doubleArrayProperties.entries.parallelStream().forEach { it.value.forEachIndexed { i, child -> embeddedObject.da.add(JdsDoubleCollections(it.key, i, child)) } }
        longArrayProperties.entries.parallelStream().forEach { it.value.forEachIndexed { i, child -> embeddedObject.la.add(JdsLongCollections(it.key, i, child)) } }
        integerArrayProperties.entries.parallelStream().forEach { it.value.forEachIndexed { i, child -> embeddedObject.ia.add(JdsIntegerCollections(it.key, i, child)) } }
        //==============================================
        //EMBEDDED OBJECTS
        //==============================================
        objectArrayProperties.entries.parallelStream().forEach { itx ->
            itx.value.forEach {
                embeddedObject.eb.add(JdsStoreEntityBinding(overview.entityGuid, it.overview.entityGuid, itx.key.fieldEntity.id, it.overview.entityId))
                embeddedObject.eo.add(JdsEmbeddedObject(it))
            }
        }
        objectProperties.entries.parallelStream().forEach {
            embeddedObject.eb.add(JdsStoreEntityBinding(overview.entityGuid, it.value.value.overview.entityGuid, it.key.fieldEntity.id, it.value.value.overview.entityId))
            embeddedObject.eo.add(JdsEmbeddedObject(it.value.value))
        }
    }

    /**
     * @param jdsFieldType
     * @param fieldId
     * @param value
     */
    internal fun populateProperties(jdsFieldType: JdsFieldType, fieldId: Long, value: Any?) {
        if (value == null)
            return //I.HATE.NULL - Rather retain default values
        when (jdsFieldType) {
            JdsFieldType.FLOAT -> floatProperties[fieldId]?.set(value as Float)
            JdsFieldType.INT -> integerProperties[fieldId]?.set(value as Int)
            JdsFieldType.DOUBLE -> doubleProperties[fieldId]?.set(value as Double)
            JdsFieldType.LONG -> longProperties[fieldId]?.set(value as Long)
            JdsFieldType.TEXT -> stringProperties[fieldId]?.set(value as String)
            JdsFieldType.DATE_TIME -> localDateTimeProperties[fieldId]?.set((value as Timestamp).toLocalDateTime())
            JdsFieldType.ARRAY_DOUBLE -> doubleArrayProperties[fieldId]?.get()?.add(value as Double)
            JdsFieldType.ARRAY_FLOAT -> floatArrayProperties[fieldId]?.get()?.add(value as Float)
            JdsFieldType.ARRAY_INT -> integerArrayProperties[fieldId]?.get()?.add(value as Int)
            JdsFieldType.ARRAY_LONG -> longArrayProperties[fieldId]?.get()?.add(value as Long)
            JdsFieldType.ARRAY_TEXT -> stringArrayProperties[fieldId]?.get()?.add(value as String)
            JdsFieldType.ARRAY_DATE_TIME -> dateTimeArrayProperties[fieldId]?.get()?.add((value as Timestamp).toLocalDateTime())
            JdsFieldType.BOOLEAN -> booleanProperties[fieldId]?.set((value as Int) == 1)
            JdsFieldType.ZONED_DATE_TIME -> zonedDateTimeProperties[fieldId]?.set(ZonedDateTime.ofInstant(Instant.ofEpochMilli(value as Long), ZoneId.systemDefault()))
            JdsFieldType.DATE -> localDateProperties[fieldId]?.set((value as Timestamp).toLocalDateTime().toLocalDate())
            JdsFieldType.TIME -> localTimeProperties[fieldId]?.set(LocalTime.ofSecondOfDay((value as Int).toLong()))
            JdsFieldType.BLOB -> blobProperties[fieldId]?.set(value as ByteArray)
            JdsFieldType.DURATION -> durationProperties[fieldId]?.set(Duration.ofNanos(value as Long))
            JdsFieldType.MONTH_DAY -> monthDayProperties[fieldId]?.value = MonthDay.parse(value as String)
            JdsFieldType.YEAR_MONTH -> yearMonthProperties[fieldId]?.value = YearMonth.parse(value as String)
            JdsFieldType.PERIOD -> periodProperties[fieldId]?.value = Period.parse(value as String)
            JdsFieldType.ENUM -> enumProperties.filter { it.key.field.id == fieldId }.forEach { it.value?.set(it.key.valueOf(value as Int)) }
            JdsFieldType.ENUM_COLLECTION -> {
                enumCollectionProperties.filter { it.key.field.id == fieldId }.forEach {
                    val enumValues = it.key.enumType.enumConstants
                    val index = value as Int
                    if (index < enumValues.size) {
                        it.value.get().add(enumValues[index] as Enum<*>)
                    }
                }
            }
        }
    }

    internal fun populateObjects(jdsDb: JdsDb, fieldId: Long, entityId: Long, entityGuid: String, innerObjects: ConcurrentLinkedQueue<JdsEntity>, entityGuids: HashSet<String>) {
        try {
            val entityClass = jdsDb.getBoundClass(entityId)!!
            objectArrayProperties.filter {
                it.key.fieldEntity.id == fieldId && it.key.entityType.isAnnotationPresent(JdsEntityAnnotation::class.java) && it.key.entityType.getAnnotation(JdsEntityAnnotation::class.java).entityId == entityId
            }.forEach {
                val entity = entityClass.newInstance()
                entity.overview.entityGuid = entityGuid
                entityGuids.add(entityGuid)
                it.value.get().add(entity)
                innerObjects.add(entity)
            }
            objectProperties.filter {
                it.key.fieldEntity.id == fieldId && it.key.entityType.isAnnotationPresent(JdsEntityAnnotation::class.java) && it.key.entityType.getAnnotation(JdsEntityAnnotation::class.java).entityId == entityId
            }.forEach {
                val jdsEntity = entityClass.newInstance()
                jdsEntity.overview.entityGuid = entityGuid
                entityGuids.add(entityGuid)
                it.value.set(jdsEntity)
                innerObjects.add(jdsEntity)
            }
        } catch (ex: Exception) {
            ex.printStackTrace(System.err)
        }
    }

    /**
     * Binds all the fields attached to an entity, updates the fields dictionary
     *
     * @param connection the SQL connection to use for DB operations
     * @param entityId   the value representing the entity
     * @param fields     the values representing the entity's fields
     */
    internal fun mapClassFields(jdsDb: JdsDb, connection: Connection, entityId: Long) {
        try {
            (if (jdsDb.supportsStatements()) NamedCallableStatement(connection, jdsDb.mapClassFields()) else NamedPreparedStatement(connection, jdsDb.mapClassFields())).use { mapClassFields ->
                (if (jdsDb.supportsStatements()) NamedCallableStatement(connection, jdsDb.mapFieldNames()) else NamedPreparedStatement(connection, jdsDb.mapFieldNames())).use { mapFieldNames ->
                    fields.forEach {
                        //1. map this fieldEntity ID to the entity type
                        mapClassFields.setLong("entityId", entityId)
                        mapClassFields.setLong("fieldId", it.id)
                        mapClassFields.addBatch()
                        //2. map this fieldEntity to the fieldEntity dictionary
                        mapFieldNames.setLong("fieldId", it.id)
                        mapFieldNames.setString("fieldName", it.name)
                        mapFieldNames.setString("fieldDescription", it.description)
                        mapFieldNames.addBatch()
                    }
                    mapClassFields.executeBatch()
                    mapFieldNames.executeBatch()
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace(System.err)
        }
    }

    /**
     * Binds all the enumProperties attached to an entity
     *
     * @param entityId the value representing the entity
     * @param fields   the entity's enumProperties
     */
    @Synchronized
    internal fun mapClassEnums(jdsDb: JdsDb, connection: Connection, entityId: Long) {
        mapEnumValues(jdsDb, connection, enums)
        mapClassEnumsImplementation(jdsDb, connection, entityId, enums)
        if (jdsDb.isPrintingOutput)
            System.out.printf("Mapped Enums for Entity[%s]\n", entityId)
    }

    /**
     * Binds all the fieldEntity types and updates reference tables
     *
     * @param jdsDb the current database implementation
     * @param connection the SQL connection to use for DB operations
     * @param entityId   the value representing the entity
     */
    internal fun mapClassFieldTypes(jdsDb: JdsDb, connection: Connection, entityId: Long) {
        try {
            (if (jdsDb.supportsStatements()) NamedCallableStatement(connection, jdsDb.mapFieldTypes()) else NamedPreparedStatement(connection, jdsDb.mapFieldTypes())).use { mapFieldTypes ->
                fields.forEach {
                    mapFieldTypes.setLong("typeId", it.id)
                    mapFieldTypes.setString("typeName", it.type.toString())
                    mapFieldTypes.addBatch()
                }
                mapFieldTypes.executeBatch()
            }
        } catch (ex: Exception) {
            ex.printStackTrace(System.err)
        }
    }

    /**
     * Binds all the enumProperties attached to an entity
     *
     * @param connection the SQL connection to use for DB operations
     * @param entityId   the value representing the entity
     * @param fields     the entity's enumProperties
     */
    @Synchronized
    private fun mapClassEnumsImplementation(jdsDb: JdsDb, connection: Connection, entityId: Long, fields: Set<JdsFieldEnum<*>>) {
        try {
            (if (jdsDb.supportsStatements()) connection.prepareCall(jdsDb.mapClassEnumsImplementation()) else connection.prepareStatement(jdsDb.mapClassEnumsImplementation())).use { statement ->
                for (field in fields) {
                    for (index in 0 until field.sequenceValues.size) {
                        statement.setLong(1, entityId)
                        statement.setLong(2, field.field.id)
                        statement.addBatch()
                    }
                }
                statement.executeBatch()
            }
        } catch (ex: Exception) {
            ex.printStackTrace(System.err)
        }
    }

    /**
     * Binds all the values attached to an enum
     *
     * @param connection the SQL connection to use for DB operations
     * @param fieldEnums the fieldEntity enum
     */
    @Synchronized
    private fun mapEnumValues(jdsDb: JdsDb, connection: Connection, fieldEnums: Set<JdsFieldEnum<*>>) {
        try {
            (if (jdsDb.supportsStatements()) connection.prepareCall(jdsDb.mapEnumValues()) else connection.prepareStatement(jdsDb.mapEnumValues())).use { statement ->
                for (field in fieldEnums) {
                    for (index in 0 until field.sequenceValues.size) {
                        statement.setLong(1, field.field.id)
                        statement.setInt(2, index)
                        statement.setString(3, field.sequenceValues[index].toString())
                        statement.addBatch()
                    }
                }
                statement.executeBatch()
            }
        } catch (ex: Exception) {
            ex.printStackTrace(System.err)
        }
    }

}
