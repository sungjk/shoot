package io.sungjk

import com.softwaremill.sttp.HttpURLConnectionBackend
import io.sungjk.client.{GithubClient, UserProfile}

/**
 * Created by jeremy on 2020/01/22.
 */
object Main {
  implicit val backend = HttpURLConnectionBackend()
  val client = new GithubClient()

  def getFollowers(name: String, total: Int, page: Int): List[UserProfile] = {
    if (total <= 0) List.empty else {
      val followers = client.getFollowers(name, Some(page)).right.get
      followers ++ getFollowers(name, total - followers.size, page + 1)
    }
  }

  def getFollowing(name: String, total: Int, page: Int): List[UserProfile] = {
    if (total <= 0) List.empty else {
      val following = client.getFollowings(name, Some(page)).right.get
      following ++ getFollowing(name, total - following.size, page + 1)
    }
  }

  def main(args: Array[String]): Unit = {
    val profile = client.getUserProfile("sungjk").right.get
    val followersCount = profile.followers.getOrElse(0)
    val followingCount = profile.following.getOrElse(0)
    val followers = getFollowers("sungjk", followersCount, 1)
    val following = getFollowing("sungjk", followingCount, 1)
    println(s"followers(${followers.size}): ${followers.map(_.login)}")
    println(s"following(${following.size}): ${following.map(_.login)}")
  }
}
