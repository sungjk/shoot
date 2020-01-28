package io.sungjk.errors

/**
 * Created by jeremy on 2020/01/22.
 */
abstract class Error(msg: String) extends Exception(msg) {
    def message: String = msg
}

case object JsonDeserializationError extends Error("Failed to deserialize JSON")
case class ForbiddenError(msg: String) extends Error(msg)
case object ExceededLimitError extends Error("Exceeded API rate limit")
case class NotFoundError(msg: String) extends Error(msg)
case class UnknownError(msg: String) extends Error(msg)
