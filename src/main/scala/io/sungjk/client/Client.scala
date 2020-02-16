package io.sungjk.client

import com.softwaremill.sttp.{DeserializationError, HeaderNames, MonadError, Request, Response, StatusCodes, SttpBackend}
import io.circe.parser._
import io.sungjk.errors.{Error, ExceededLimitError, ForbiddenError, JsonDeserializationError, NotFoundError, ResponseError, UnknownError}

/**
 * Created by jeremy on 2020/01/22.
 */
trait Backend[R[_]] {
    protected lazy val rm: MonadError[R] = backend.responseMonad

    protected def backend: SttpBackend[R, Nothing]
    protected def endpoint: String = "https://api.github.com"
}

trait Client[R[_]] extends Backend[R] {
    case class ListResponse[Res](fin: Boolean = true, result: Res)

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

    implicit class RequestOps[T](r: Request[T, Nothing]) {
        def log(): Request[T, Nothing] = {
            println(s">>> ${r.method.m} ${r.uri}")
            r
        }
    }

    implicit class ResponseOps[T](r: R[Response[Either[DeserializationError[io.circe.Error], T]]]) {
        def parseResponse: R[Either[Error, T]] = rm.map(r) { response =>
            val (body, nextOpt) = (response.body, response.header(HeaderNames.Link))
            println(s"<<< $body")
            (body, nextOpt) match {
                case (Right(result), Some(_)) =>
                    // TODO next 처리
                    result.fold(_ => Left(JsonDeserializationError), body => Right(body))
                case (Right(result), None) =>
                    result.fold(_ => Left(JsonDeserializationError), body => Right(body))
                case (Left(error), _) =>
                    Left(parseError(response.code, error))
            }
        }
    }
}
