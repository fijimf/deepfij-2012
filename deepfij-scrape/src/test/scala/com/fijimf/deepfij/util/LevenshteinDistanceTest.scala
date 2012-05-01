package com.fijimf.deepfij.util


import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}

@RunWith(classOf[JUnitRunner])
class LevenshteinDistanceTest extends FunSuite {


  test("should work on empty strings") {
    assert(LevenshteinDistance("", "") == 0)
    assert(LevenshteinDistance("a", "") == 1)
    assert(LevenshteinDistance("", "a") == 1)
    assert(LevenshteinDistance("abc", "") == 3)
    assert(LevenshteinDistance("", "abc") == 3)
  }

  test("should work on equal strings") {
    assert(LevenshteinDistance("", "") == 0)
    assert(LevenshteinDistance("a", "a") == 0)
    assert(LevenshteinDistance("abc", "abc") == 0)
  }

  test("should work where only inserts are needed") {
    assert(LevenshteinDistance("", "a") == 1)
    assert(LevenshteinDistance("a", "ab") == 1)
    assert(LevenshteinDistance("b", "ab") == 1)
    assert(LevenshteinDistance("ac", "abc") == 1)
    assert(LevenshteinDistance("abcdefg", "xabxcdxxefxgx") == 6)
  }

  test("should work where only deletes are needed") {
    assert(LevenshteinDistance("a", "") == 1)
    assert(LevenshteinDistance("ab", "a") == 1)
    assert(LevenshteinDistance("ab", "b") == 1)
    assert(LevenshteinDistance("abc", "ac") == 1)
    assert(LevenshteinDistance("xabxcdxxefxgx", "abcdefg") == 6)
  }

  test("should work where only substitutions are needed") {
    assert(LevenshteinDistance("a", "b") == 1)
    assert(LevenshteinDistance("ab", "ac") == 1)
    assert(LevenshteinDistance("ac", "bc") == 1)
    assert(LevenshteinDistance("abc", "axc") == 1)
    assert(LevenshteinDistance("xabxcdxxefxgx", "1ab2cd34ef5g6") == 6)
  }

  test("should work where many operations are needed") {
    assert(LevenshteinDistance("example", "samples") == 3)
    assert(LevenshteinDistance("sturgeon", "urgently") == 6)
    assert(LevenshteinDistance("levenshtein", "frankenstein") == 6)
    assert(LevenshteinDistance("distance", "difference") == 5)
    assert(LevenshteinDistance("java was neat", "scala is great") == 7)
  }

}