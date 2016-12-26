package de.randombyte.kosp

import com.google.common.reflect.TypeToken
import kotlin.reflect.KClass

val <T : Any> KClass<T>.typeToken: TypeToken<T>
    get() = TypeToken.of(this.java)