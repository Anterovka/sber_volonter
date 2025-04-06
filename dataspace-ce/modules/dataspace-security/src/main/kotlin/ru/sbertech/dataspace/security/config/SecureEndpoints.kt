package ru.sbertech.dataspace.security.config

import ru.sbertech.dataspace.security.requestProcessors.util.Endpoint

class SecureEndpoints : ArrayList<Endpoint> {
    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor() : super()
    constructor(c: MutableCollection<out Endpoint>) : super(c)
}
