package io.sungjk.errors

import io.circe.{Decoder, Encoder, HCursor, Json}

/**
 * Created by jeremy on 2020/01/22.
 */
//case class ResponseError(message: String, errors: Map[String, String] = Map())
//
//object ResponseError {
//    implicit val responseDecoder: Decoder[ResponseError] = deriveDecoder
//    implicit val responseEncoder: Encoder[ResponseError] = deriveEncoder
//}

case class ResponseError(
    message: String,
    documentationUrl: Option[String]
)

object ResponseError {
    implicit val responseDecoder: Decoder[ResponseError] = (c: HCursor) =>
        for {
            message <- c.downField("message").as[String].right
            documentationUrl <- c.downField("documentation_url").as[Option[String]].right
        } yield ResponseError(
            message,
            documentationUrl
        )

    implicit val responseEncoder: Encoder[ResponseError] = (e: ResponseError) =>
        Json.obj(
            ("message", Json.fromString(e.message)),
            ("documentationUrl", e.documentationUrl.fold(Json.Null)(Json.fromString))
        )
}
