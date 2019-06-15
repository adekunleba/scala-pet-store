package io.github.pauljamescleary.petstore.infrastructure.repository.doobie

import cats.data._
import cats.implicits._
import doobie._
import doobie.implicits._
import cats.effect.Bracket
import doobie.util.transactor.Transactor
import io.github.pauljamescleary.petstore.domain.reviews.{Reviews, ReviewsRepositoryAlgebra}
//To construct this I need the Algebra.

//Construct SQL Private Objects

private object ReviewsSQL {

  def insert(reviews: Reviews): Update0 =
    sql"""INSERT INTO REVIEWS (USER_ID, PET_ID, REVIEW) VALUES (${reviews.userId}, ${reviews.petId}, ${reviews.review})
    """.update

  def delete(reviewId: Long): Update0 =
    sql"""
          DELETE FROM REVIEWS WHERE ID = $reviewId
    """.update

  def select(reviewId: Long): Query0[Reviews] =
    sql"""
          SELECT USER_ID, PET_ID, REVIEW, ID FROM REVIEWS WHERE ID = $reviewId
    """.query[Reviews]

  def update(reviews: Reviews, id: Long): Update0 =
    sql"""UPDATE REVIEWS SET REVIEW = ${reviews.review}, USER_ID = ${reviews.userId},
          PET_ID=${reviews.petId}, ID=$id
    """.update
}

class DoobieReviewRepositoryInterpreter[F[_]: Bracket[?[_], Throwable]](xa: Transactor[F])
    extends ReviewsRepositoryAlgebra[F] {

  import ReviewsSQL._
  //Non empty list will be useful if your query should return a list of objects i.e more than one
  def get(reviewId: Long): F[Option[Reviews]] = select(reviewId).option.transact(xa)

  //Do you want to make this an option.
  def addReviews(review: Reviews): F[Option[Reviews]] =
    OptionT
      .liftF { //With the help of OptionT.LiftF i can lift an F[A] to Option
        insert(review)
          .withUniqueGeneratedKeys[Long]("ID")
          .map(id => review.copy(id = id.some))
      }
      .value //Takes care of what you want it to return here, if removed can return the Id.
      .transact(xa)

  def deleteReviews(reviewId: Long): F[Option[Reviews]] =
    get(reviewId).flatMap(review => delete(reviewId).run.transact(xa).as(review))

  def updateReviews(review: Reviews): F[Option[Reviews]] =
    OptionT
      .fromOption[F](review.id)
      .semiflatMap { id =>
        update(review, id).run.transact(xa).as(review)
      }
      .value
}

object DoobieReviewRepositoryInterpreter {
  def apply[F[_]: Bracket[?[_], Throwable]](
      xa: Transactor[F]): DoobieReviewRepositoryInterpreter[F] =
    new DoobieReviewRepositoryInterpreter(xa)
}
