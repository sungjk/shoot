package io.sungjk.errors

/**
 * Created by jeremy on 2020/01/22.
 */
abstract class Error(msg: String) extends Exception(msg) {
    def errorMsg: String = msg
}

case object JsonDeserializationError extends Error("Failed to deserialize JSON")
case class ForbiddenError(msg: String) extends Error(msg)
case class UnknownError(msg: String) extends Error(msg)
