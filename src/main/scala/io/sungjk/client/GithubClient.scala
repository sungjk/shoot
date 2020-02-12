package io.sungjk.client

import com.softwaremill.sttp._
import com.softwaremill.sttp.circe.asJson
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.sungjk.errors.Error

import scala.language.higherKinds

/**
 * Created by jeremy on 2020/01/22.
 */
// https://developer.github.com/v3/users/
case class UserProfile(
    login: String,
    id: Long,
    avatarUrl: String,
    name: Option[String] = None,
    followers: Option[Int] = None,
    following: Option[Int] = None
)

object UserProfile {
    implicit val decoder: Decoder[UserProfile] = (c: HCursor) =>
        for {
            login <- c.downField("login").as[String].right
            id <- c.downField("id").as[Long].right
            avatarUrl <- c.downField("avatar_url").as[String].right
            name <- c.downField("name").as[Option[String]].right
            followers <- c.downField("followers").as[Option[Int]].right
            following <- c.downField("following").as[Option[Int]].right
        } yield UserProfile(login, id, avatarUrl, name, followers, following)

    implicit val encoder: Encoder[UserProfile] = (u: UserProfile) =>
        Json.obj(
            ("login", Json.fromString(u.login)),
            ("id", Json.fromLong(u.id)),
            ("avatarUrl", Json.fromString(u.avatarUrl)),
            ("name", u.name.fold(Json.Null)(Json.fromString)),
            ("followers", u.followers.fold(Json.Null)(Json.fromInt)),
            ("following", u.following.fold(Json.Null)(Json.fromInt))
        )
}

class GithubClient[R[_]](implicit sttpBackend: SttpBackend[R, Nothing]) extends Client[R] {
    override protected def backend: SttpBackend[R, Nothing] = sttpBackend

    private val maxLimit = 100

    def getUserProfile(username: String): R[Either[Error, UserProfile]] =
        sttp.get(uri"$endpoint/users/$username")
            .response(asJson[UserProfile])
            .send()
            .parseResponse

    def getFollowers(username: String, reqPageOpt: Option[Int] = None, reqLimitOpt: Option[Int] = None): R[Either[Error, List[UserProfile]]] = {
        val page = reqPageOpt.getOrElse(1)
        val limit = Math.min(maxLimit, reqLimitOpt.getOrElse(maxLimit))
        sttp.get(uri"$endpoint/users/$username/followers?per_page=$limit&page=$page")
            .response(asJson[List[UserProfile]])
            .send()
            .parseResponse
    }

    def getFollowings(username: String, reqPageOpt: Option[Int] = None, reqLimitOpt: Option[Int] = None): R[Either[Error, List[UserProfile]]] = {
        val page = reqPageOpt.getOrElse(1)
        val limit = Math.min(maxLimit, reqLimitOpt.getOrElse(maxLimit))
        sttp.get(uri"$endpoint/users/$username/following?per_page=$limit&page=$page")
            .response(asJson[List[UserProfile]])
            .send()
            .parseResponse
    }
}
