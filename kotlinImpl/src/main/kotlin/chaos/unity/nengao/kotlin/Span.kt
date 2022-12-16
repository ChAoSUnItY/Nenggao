package chaos.unity.nengao.kotlin

import chaos.unity.nenggao.AbstractPosition
import chaos.unity.nenggao.AbstractSpan
import chaos.unity.nenggao.Span

data class Span(val startPosition: AbstractPosition, val endPosition: AbstractPosition) :
    AbstractSpan(startPosition, endPosition) {
    override fun expand(endSpan: AbstractSpan?): AbstractSpan =
        endSpan?.let {
            if (endSpan.endPosition.line < startPosition.line) return copy()
            else if (endSpan.endPosition.line == startPosition.line && endSpan.endPosition.pos < startPosition.pos) return copy()

            return Span(startPosition, endSpan.endPosition)
        } ?: copy()

    override fun copy(): AbstractSpan =
        copy(startPosition = startPosition, endPosition = endPosition)
}
