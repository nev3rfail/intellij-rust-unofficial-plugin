/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.thir

import org.rust.lang.core.mir.schemas.MirSpan
import org.rust.lang.core.psi.RsSelfParameter
import org.rust.lang.core.types.ty.Ty

data class ThirParam(
    val pat: ThirPat?,
    val ty: Ty,
    val tySpan: MirSpan?,
    val selfKind: ImplicitSelfKind?,
)

enum class ImplicitSelfKind {
    /**
     *  Represents a `fn x(self);`
     */
    Immutable,

    /**
     *  Represents a `fn x(mut self);`
     */
    Mutable,

    /**
     * Represents a `fn x(&self);`
     */
    ImmutableReference,

    /**
     * Represents a `fn x(&mut self);`
     */
    MutableReference,

    /**
     * Represents when a function does not have a self argument or
     * when a function has a `self: X` argument.
     */
    None;

    companion object {
        fun from(self: RsSelfParameter): ImplicitSelfKind {
            TODO()
        }
    }
}
