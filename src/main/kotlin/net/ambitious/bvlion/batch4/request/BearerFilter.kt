package net.ambitious.bvlion.batch4.request

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.ambitious.bvlion.batch4.data.AppParams
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class BearerFilter(private val appParams: AppParams) : Filter {
  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    val servletRequest = request as HttpServletRequest
    if (servletRequest.requestURI == "/healthcheck") {
      chain.doFilter(request, response)
      return
    }
    val authorization = servletRequest.getHeader("Authorization")
    if (authorization.isNullOrEmpty()) {
      sendForbidden(response)
      return
    }
    if (!authorization.startsWith("Bearer ") || authorization.length <= 7) {
      sendForbidden(response)
      return
    }
    if (authorization.substring(7) != appParams.accessibleToken) {
      sendForbidden(response)
      return
    }
    chain.doFilter(request, response)
  }

  private fun sendForbidden(response: ServletResponse) {
    (response as HttpServletResponse).sendError(HttpStatus.FORBIDDEN.value())
  }
}