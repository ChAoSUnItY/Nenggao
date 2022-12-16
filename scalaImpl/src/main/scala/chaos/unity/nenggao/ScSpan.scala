package chaos.unity.nenggao

case class ScSpan(startPosition: AbstractPosition, endPosition: AbstractPosition) extends AbstractSpan(startPosition, endPosition) {
  override def expand(endSpan: AbstractSpan): AbstractSpan = {
    val copied = copy()
    
    if (endSpan == null) copied
    else if (endSpan.endPosition.line < startPosition.line) copied
    else if (endSpan.endPosition.line == startPosition.line && endSpan.endPosition.pos < startPosition.pos) copied
    else {
      val startPosition = startPosition
      val endPosition = endSpan.endPosition

      ScSpan(startPosition, endPosition) 
    }
  }

  override def copy(): AbstractSpan =
    ScSpan(startPosition, endPosition)
}
