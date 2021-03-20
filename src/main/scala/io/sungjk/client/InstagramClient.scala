package io.sungjk.client

import com.softwaremill.sttp._
import com.softwaremill.sttp.circe.asJson
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.sungjk.errors.Error

case class InstagramUser(
	id: String,
	username: String,
	profileImageUrl: String
)

object InstagramUser {
	implicit val decoder: Decoder[InstagramUser] = (c: HCursor) =>
		for {
			id <- c.downField("id").as[String].right
			username <- c.downField("username").as[String].right
			profileImageUrl <- c.downField("profile_pic_url").as[String].right
		} yield InstagramUser(id, username, profileImageUrl)

	implicit val encoder: Encoder[InstagramUser] = (u: InstagramUser) =>
		Json.obj(
			("id", Json.fromString(u.id)),
			("username", Json.fromString(u.username)),
			("profile_pic_url", Json.fromString(u.profileImageUrl))
		)
}

class InstagramClient[R[_]](implicit sttpBackend: SttpBackend[R, Nothing]) extends Client[R] {
	override protected def backend: SttpBackend[R, Nothing] = sttpBackend
	override protected def endpoint: String = "https://www.instagram.com"

	def getUserProfile(username: String): R[Either[Error, InstagramUser]] = {
		sttp.get(uri"$endpoint/$username/?__a=1")
    		.log()
    		.response(asJson[InstagramUser])
    		.send()
    		.parseResponse
	}
}
