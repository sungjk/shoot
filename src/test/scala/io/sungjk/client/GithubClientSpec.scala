package io.sungjk.client

import com.softwaremill.sttp.HttpURLConnectionBackend
import org.scalatest.flatspec.AnyFlatSpec

/**
 * Created by jeremy on 2020/01/22.
 */
class GithubClientSpec extends AnyFlatSpec {
    "GithubClient" should "get user profile" in {
        implicit val backend = HttpURLConnectionBackend()
        val client = new GithubClient()
        val profile = client.getUserProfile("sungjk")
        assert(profile === Right(UserProfile("sungjk", 9454446)))
    }
}
