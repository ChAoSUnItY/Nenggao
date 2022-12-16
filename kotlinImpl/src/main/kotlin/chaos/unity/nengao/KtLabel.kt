package chaos.unity.nengao

import chaos.unity.nenggao.AbstractLabel
import chaos.unity.nenggao.AbstractSpan

@Suppress("unused")
data class KtLabel(val span: AbstractSpan, val message: String) : AbstractLabel(span, message)
