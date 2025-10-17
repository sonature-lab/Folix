package com.folix.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Folix 애플리케이션 진입점.
 *
 * Spring Boot 애플리케이션 메인 클래스.
 * JPA 리포지토리와 엔티티는 infrastructure 모듈에 정의되어 있다.
 */
@SpringBootApplication(scanBasePackages = ["com.folix"])
@EnableJpaRepositories(basePackages = ["com.folix.infrastructure.persistence.repository"])
@EntityScan(basePackages = ["com.folix.infrastructure.persistence.entity"])
class FolixApplication

fun main(args: Array<String>) {
    runApplication<FolixApplication>(*args)
}
