package io.github.pauljamescleary.petstore.domain.reviews

trait ReviewsRepositoryAlgebra[F[_]] {

  //List of operations that happends on review

  def addReviews(review: Reviews): F[Option[Reviews]]

  def deleteReviews(reviewId: Long): F[Option[Reviews]]

//  def listReviews

  def updateReviews(review: Reviews): F[Option[Reviews]]
}
