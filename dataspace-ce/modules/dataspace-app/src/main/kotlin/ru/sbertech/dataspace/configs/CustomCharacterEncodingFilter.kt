package ru.sbertech.dataspace.configs

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.FilterConfig
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse

class CustomCharacterEncodingFilter : Filter {
    override fun init(filterConfig: FilterConfig?) {
    }

    override fun doFilter(
        request: ServletRequest?,
        response: ServletResponse,
        chain: FilterChain,
    ) {
        try {
            response.characterEncoding = "UTF-8"
        } finally {
            chain.doFilter(request, response)
        }
    }

    override fun destroy() {}
}
