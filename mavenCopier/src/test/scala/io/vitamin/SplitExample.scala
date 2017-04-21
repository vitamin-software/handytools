package io.vitamin

import java.util.regex.Pattern

/**
  * Vitamin Software Consulting Ltd
  */
object SplitExample extends App{

  val matcher = Pattern.compile("(.*?)-(\\d+.*)").matcher("abc-de-11.0.0.jar")
  while(matcher.find()) {
    println(matcher.group(1))
    println(matcher.group(2))
  }

}
