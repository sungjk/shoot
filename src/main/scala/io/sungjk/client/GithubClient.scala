package io.sungjk.client

import com.softwaremill.sttp._
import com.softwaremill.sttp.circe.asJson
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
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
    id: Long
)

object UserProfile {
    implicit val decoder: Decoder[UserProfile] = deriveDecoder
    implicit val encoder: Encoder[UserProfile] = deriveEncoder
}

class GithubClient[R[_]](implicit sttpBackend: SttpBackend[R, Nothing]) extends Client[R] {
    override protected def backend: SttpBackend[R, Nothing] = sttpBackend

    def getUserProfile(username: String): R[Either[Error, UserProfile]] =
        sttp.get(uri"https://api.github.com/users/$username")
            .response(asJson[UserProfile])
            .send()
            .parseResponse
}

