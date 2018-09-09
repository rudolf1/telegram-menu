package ua.rudolf.telega.menu

import kotlin.reflect.KMutableProperty0

interface MutableProperty<T> {
    fun set(v:T)
    fun get():T

    companion object {
        fun <X> create(prop: KMutableProperty0<X>): MutableProperty<X> {
            return object: MutableProperty<X> {
                override fun get(): X  = prop.get()
                override fun set(v: X) = prop.set(v)
            }
        }

        fun <X> create(localGet:() -> X, localSet:(x:X) -> Unit): MutableProperty<X> {
            return object: MutableProperty<X> {
                override fun get(): X  = localGet()
                override fun set(v: X) = localSet(v)
            }
        }
    }
}