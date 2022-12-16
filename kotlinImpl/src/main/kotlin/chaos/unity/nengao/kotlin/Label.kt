package chaos.unity.nengao.kotlin

import chaos.unity.nenggao.AbstractLabel
import chaos.unity.nenggao.AbstractSpan

data class Label(val span: AbstractSpan, val message: String) : AbstractLabel(span, message)
