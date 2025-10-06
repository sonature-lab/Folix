package com.folix.application.exception

/**
 * 애플리케이션 레이어 예외 계층.
 */
sealed class ApplicationException(message: String) : RuntimeException(message)

class EntityNotFoundException(entity: String, id: Any) :
    ApplicationException("$entity not found: $id")

class DuplicateEntityException(entity: String, field: String, value: Any) :
    ApplicationException("$entity with $field '$value' already exists")
