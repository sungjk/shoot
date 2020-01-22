package io.sungjk.client

import com.softwaremill.sttp.SttpBackend

/**
 * Created by jeremy on 2020/01/22.
 */
trait 42Backend[R[_]] {
    protected lazy val rm = backend.responseMonad

    protected def backend: SttpBackend[R, Nothing]
}

trait KorailClient

class KorailClient {

}
