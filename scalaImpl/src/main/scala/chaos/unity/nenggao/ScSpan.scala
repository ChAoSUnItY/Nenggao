package chaos.unity.nenggao

case class ScSpan(startPosition: AbstractPosition, endPosition: AbstractPosition) extends AbstractSpan(startPosition, endPosition) {
  /**
   * expand copy current instance of {@link AbstractSpan} and attempt to extend end position to {@code endSpan}.
   * If {@code endSpan} is null, returns copy of current instance; if {@code endSpan}'s {@link AbstractSpan#   endPosition}
   * is in front of the current instance's {@link AbstractSpan#   startPosition}, returns copy of current instance, otherwise,
   * returns a copy of current instance, which its {@link AbstractSpan#   endPosition} is replaced by {@code endSpan}'s
   * {@link AbstractSpan#   endPosition}.
   *
   * @param endSpan the span to expand with
   * @return a copy of current instance, field data would be different based on {@code endSpan}'s data
   */
  override def expand(endSpan: AbstractSpan): AbstractSpan = {
    val copied = copy()
    
    if (endSpan == null)
      return copied

    if (endSpan.endPosition.line < startPosition.line) copied
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
