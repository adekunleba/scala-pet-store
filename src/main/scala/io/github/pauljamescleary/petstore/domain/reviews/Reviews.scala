package io.github.pauljamescleary.petstore.domain.reviews

case class Reviews(
    userId: Option[Long],
    petId: Long,
    review: Option[String],
    id: Option[Long] = None)
