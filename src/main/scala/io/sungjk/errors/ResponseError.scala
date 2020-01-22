package io.sungjk.errors

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * Created by jeremy on 2020/01/22.
 */
case class ResponseError(errorMsgs: Seq[String], errors: Map[String, String] = Map())

object ResponseError {
    implicit val responseDecoder: Decoder[ResponseError] = deriveDecoder
    implicit val responseEncoder: Encoder[ResponseError] = deriveEncoder
}
