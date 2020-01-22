package io.sungjk.client

import com.softwaremill.sttp.{DeserializationError, Response, StatusCodes, SttpBackend}
import io.circe.parser._
import io.sungjk.errors.{Error, ForbiddenError, JsonDeserializationError, ResponseError, UnknownError}

/**
 * Created by jeremy on 2020/01/22.
 */
trait Backend[R[_]] {
    protected lazy val rm = backend.responseMonad

    protected def backend: SttpBackend[R, Nothing]
}

trait Client[R[_]] extends Backend[R] {
    private def parseError(status: Int, errorMsg: String): Error = {
        val responseErrorOpt = parse(errorMsg).right.toOption
            .flatMap { _.as[ResponseError].right.toOption }
        val errorMessage = responseErrorOpt.flatMap { _.errorMsgs.headOption }
            .orElse(responseErrorOpt.flatMap { _.errors.headOption.map { _._2 } })

        status match {
            case StatusCodes.Forbidden =>
                errorMessage.fold(ForbiddenError("Forbidden"))(ForbiddenError)
            case _ =>
                errorMessage.fold(UnknownError("Unknown Error!"))(UnknownError)
        }
    }

    implicit class _Response[T](r: R[Response[Either[DeserializationError[io.circe.Error], T]]]) {
        def parseResponse: R[Either[Error, T]] =
            rm.map(r) { res =>
                res.body match {
                    case Right(result) =>
                        result.fold(
                            _ => Left(JsonDeserializationError),
                            body => Right(body)
                        )
                    case Left(error) =>
                        Left(parseError(res.code, error))
                }
            }
    }
}
