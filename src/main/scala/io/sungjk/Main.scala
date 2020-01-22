package io.sungjk

import sttp.client._

/**
 * Created by jeremy on 2020/01/22.
 */
object Main {
  def main(args: Array[String]): Unit = {
    val request = basicRequest.get(uri"https://api.github.com/users/sungjk")

    implicit val backend = HttpURLConnectionBackend()
    val response = request.send()
    println(response.body)
  }
}
