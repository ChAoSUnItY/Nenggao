package chaos.unity.nenggao

import scala.annotation.unused

@unused
case class ScLabel(labelSpan: AbstractSpan, msg: String) extends AbstractLabel(labelSpan, msg)
