package io.sungjk.client

import com.softwaremill.sttp.{DeserializationError, Response, StatusCodes, SttpBackend}
import io.circe.{Json, ParsingFailure}
import io.circe.parser._
import io.sungjk.errors.{Error, ExceededLimitError, ForbiddenError, JsonDeserializationError, NotFoundError, ResponseError, UnknownError}

/**
 * Created by jeremy on 2020/01/22.
 */
trait Backend[R[_]] {
    protected lazy val rm = backend.responseMonad

    protected def backend: SttpBackend[R, Nothing]
    protected def endpoint: String = "https://api.github.com"
}

trait Client[R[_]] extends Backend[R] {
    private def parseError(status: Int, errorMessage: String): Error = {
        val responseErrorOpt = parse(errorMessage).right.toOption
            .flatMap { _.as[ResponseError].right.toOption }
        val errorMessageOpt = responseErrorOpt.map { _.message }

        status match {
            case StatusCodes.Forbidden if errorMessageOpt.forall(_.contains("API rate limit exceeded")) =>
                ExceededLimitError
            case StatusCodes.Forbidden =>
                errorMessageOpt.fold(ForbiddenError("Forbidden"))(ForbiddenError)
            case StatusCodes.NotFound =>
                errorMessageOpt.fold(NotFoundError("Not Found"))(NotFoundError)
            case _ =>
                errorMessageOpt.fold(UnknownError("Unknown Error!"))(UnknownError)
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
