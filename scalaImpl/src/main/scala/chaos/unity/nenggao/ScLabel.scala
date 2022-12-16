package chaos.unity.nenggao

import scala.annotation.unused

@unused
case class ScLabel(span: AbstractSpan, message: String) extends AbstractLabel(span, message)
