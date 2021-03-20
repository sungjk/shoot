package io.sungjk.client

import com.softwaremill.sttp.HttpURLConnectionBackend
import org.scalatest.flatspec.AnyFlatSpec

/**
 * Created by jeremy on 2020/01/22.
 */
class GithubClientSpec extends AnyFlatSpec {
    implicit val backend = HttpURLConnectionBackend()
    val client = new GithubClient()

    "GithubClient" should "get user profile" in {
        val profile = client.getUserProfile("sungjk").right.get
        assert("sungjk" === profile.login)
        assert(9454446 === profile.id)
        assert(profile.avatarUrl.nonEmpty)
        assert(profile.name.isDefined)
        assert(profile.followers.isDefined)
        assert(profile.following.isDefined)
    }

    "GithubClient" should "get followers" in {
        val profile = client.getUserProfile("sungjk").right.get
        assert(profile.followers.get === client.getFollowers("sungjk").right.get.size)
        assert(42 === client.getFollowers("sungjk", reqLimitOpt = Some(42)).right.get.size)
    }

    "GithubClient" should "get followings" in {
        assert(42 === client.getFollowings("sungjk", reqLimitOpt = Some(42)).right.get.size)
        assert(42 === client.getFollowings("sungjk", reqLimitOpt = Some(42)).right.get.size)
    }
}
