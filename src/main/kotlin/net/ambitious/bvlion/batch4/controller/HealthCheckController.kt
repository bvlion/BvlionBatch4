package net.ambitious.bvlion.batch4.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {
  @GetMapping("/healthcheck")
  fun healthCheck() = Unit
}