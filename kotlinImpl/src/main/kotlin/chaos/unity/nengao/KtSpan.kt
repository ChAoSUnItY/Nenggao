package chaos.unity.nengao

import chaos.unity.nenggao.AbstractPosition
import chaos.unity.nenggao.AbstractSpan
import chaos.unity.nenggao.Span

@Suppress("unused")
data class KtSpan(val startPosition: AbstractPosition, val endPosition: AbstractPosition) :
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
