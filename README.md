# [Nenggao](https://en.wikipedia.org/wiki/Mount_Nenggao)
[![](https://jitpack.io/v/ChAoSUnItY/Nenggao.svg)](https://jitpack.io/#ChAoSUnItY/Nenggao)
> Makes diagnostic easier!
A diagnostic library for parser / interpreter usages.

![image](https://user-images.githubusercontent.com/43753315/173344211-41987a7a-e5cf-45ce-b89a-16c4c77e5e27.png)
![image](https://user-images.githubusercontent.com/43753315/173404304-c5df1349-dd65-43cf-990e-7ae837f444fd.png)

## About
Nenggao is a diagnostic library mainly designed for [Yakou Lang's compiler](https://github.com/CASC-Lang/CASC), 
though it's target for Yakou's development, you can still use it in your own project with its api, which has very
good extendibility for customization.

## Limitations
Yes, I'm about to say this, this library is still not powerful enough to handle several scenarios,
such as multi reference to same position. To avoid this, you should:
- Break up your error message, this can keep up readability

Here's a list of unworkable examples:
- Multiple Multiline labels refer to same line
- Multiple Single line label's span conflicted 

## License
This work is licensed under the MIT license.

Copyright (c) 2022 ChAoS-UnItY, all rights reserved.
