package io.sungjk.client

import com.softwaremill.sttp._
import com.softwaremill.sttp.circe.asJson
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.sungjk.errors.Error

import scala.language.higherKinds

/**
 * Created by jeremy on 2020/01/22.
 */
/*
{
   "login":"sungjk",
   "id":9454446,
   "node_id":"MDQ6VXNlcjk0NTQ0NDY=",
   "avatar_url":"https://avatars0.githubusercontent.com/u/9454446?v=4",
   "gravatar_id":"",
   "url":"https://api.github.com/users/sungjk",
   "html_url":"https://github.com/sungjk",
   "followers_url":"https://api.github.com/users/sungjk/followers",
   "following_url":"https://api.github.com/users/sungjk/following{/other_user}",
   "gists_url":"https://api.github.com/users/sungjk/gists{/gist_id}",
   "starred_url":"https://api.github.com/users/sungjk/starred{/owner}{/repo}",
   "subscriptions_url":"https://api.github.com/users/sungjk/subscriptions",
   "organizations_url":"https://api.github.com/users/sungjk/orgs",
   "repos_url":"https://api.github.com/users/sungjk/repos",
   "events_url":"https://api.github.com/users/sungjk/events{/privacy}",
   "received_events_url":"https://api.github.com/users/sungjk/received_events",
   "type":"User",
   "site_admin":false,
   "name":"Jeremy Kim",
   "company":null,
   "blog":"https://sungjk.github.io/",
   "location":"Seoul. Korea",
   "email":null,
   "hireable":null,
   "bio":"FP(λ) fanatic :kr:",
   "public_repos":54,
   "public_gists":8,
   "followers":64,
   "following":142,
   "created_at":"2014-10-29T23:31:06Z",
   "updated_at":"2020-01-21T03:01:42Z"
}
 */
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

    def getUserFollowers(username: String, reqPageOpt: Option[Int] = None, reqLimitOpt: Option[Int] = None): R[Either[Error, List[UserProfile]]] = {
        val page = reqPageOpt.getOrElse(1)
        val limit = Math.min(maxLimit, reqLimitOpt.getOrElse(maxLimit))
        sttp.get(uri"$endpoint/users/$username/followers?per_page=$limit&page=$page")
            .response(asJson[List[UserProfile]])
            .send()
            .parseResponse
    }

    def getUserFollowings(username: String, reqPageOpt: Option[Int] = None, reqLimitOpt: Option[Int] = None): R[Either[Error, List[UserProfile]]] = {
        val page = reqPageOpt.getOrElse(1)
        val limit = Math.min(maxLimit, reqLimitOpt.getOrElse(maxLimit))
        sttp.get(uri"$endpoint/users/$username/following?per_page=$limit&page=$page")
            .response(asJson[List[UserProfile]])
            .send()
            .parseResponse
    }
}

