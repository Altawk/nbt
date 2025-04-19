@file:Suppress("NOTHING_TO_INLINE")

package cn.altawk.nbt.tag

public inline fun NbtCompound(map: MutableMap<String, NbtTag> = LinkedHashMap(), action: NbtCompound.() -> Unit): NbtCompound = NbtCompound(map).apply(action)

public inline fun NbtList(list: MutableList<NbtTag> = ArrayList(), action: NbtList.() -> Unit): NbtList = NbtList(list).apply(action)

//region NbtCompound put methods
public inline fun NbtCompound.put(key: String, value: Byte): NbtTag? = this.put(key, NbtByte(value))
public inline fun NbtCompound.put(key: String, value: Boolean): NbtTag? = this.put(key, NbtByte(value))
public inline fun NbtCompound.put(key: String, value: Double): NbtTag? = this.put(key, NbtDouble(value))
public inline fun NbtCompound.put(key: String, value: Float): NbtTag? = this.put(key, NbtFloat(value))
public inline fun NbtCompound.put(key: String, value: Int): NbtTag? = this.put(key, NbtInt(value))
public inline fun NbtCompound.put(key: String, value: Long): NbtTag? = this.put(key, NbtLong(value))
public inline fun NbtCompound.put(key: String, value: String): NbtTag? = this.put(key, NbtString(value))
public inline fun NbtCompound.put(key: String, value: Short): NbtTag? = this.put(key, NbtShort(value))
public inline fun NbtCompound.put(key: String, value: ByteArray): NbtTag? = this.put(key, NbtByteArray(value))
public inline fun NbtCompound.put(key: String, value: IntArray): NbtTag? = this.put(key, NbtIntArray(value))
public inline fun NbtCompound.put(key: String, value: LongArray): NbtTag? = this.put(key, NbtLongArray(value))

public inline fun NbtCompound.putList(
    key: String,
    list: MutableList<NbtTag> = ArrayList(),
    action: NbtList.() -> Unit
): NbtTag? = this.put(key, NbtList(list, action))

public inline fun NbtCompound.putCompound(
    key: String,
    map: MutableMap<String, NbtTag> = LinkedHashMap(),
    action: NbtCompound.() -> Unit
): NbtTag? = this.put(key, NbtCompound(map, action))
//endregion

//region NbtList add methods
public inline fun NbtList.add(value: Byte): Boolean = this.add(NbtByte(value))
public inline fun NbtList.add(value: Boolean): Boolean = this.add(NbtByte(value))
public inline fun NbtList.add(value: Double): Boolean = this.add(NbtDouble(value))
public inline fun NbtList.add(value: Float): Boolean = this.add(NbtFloat(value))
public inline fun NbtList.add(value: Int): Boolean = this.add(NbtInt(value))
public inline fun NbtList.add(value: Long): Boolean = this.add(NbtLong(value))
public inline fun NbtList.add(value: String): Boolean = this.add(NbtString(value))
public inline fun NbtList.add(value: Short): Boolean = this.add(NbtShort(value))
public inline fun NbtList.add(value: ByteArray): Boolean = this.add(NbtByteArray(value))
public inline fun NbtList.add(value: IntArray): Boolean = this.add(NbtIntArray(value))
public inline fun NbtList.add(value: LongArray): Boolean = this.add(NbtLongArray(value))

public inline fun NbtList.addList(
    list: MutableList<NbtTag> = ArrayList(),
    action: NbtList.() -> Unit
): Boolean = this.add(NbtList(list, action))

public inline fun NbtList.addCompound(
    map: MutableMap<String, NbtTag> = LinkedHashMap(),
    action: NbtCompound.() -> Unit
): Boolean = this.add(NbtCompound(map, action))
//endregion