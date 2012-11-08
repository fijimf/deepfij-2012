package com.fijimf.deepfij.util

import xml.{NodeSeq, Node}

object Util {
  def textKey(name: String): String = {
    name.replaceAll("[']", "").replaceAll("[^\\-a-zA-Z0-9 ]", " ").trim.toLowerCase.replaceAll(" +", "-")
  }

  def nameToKey(n: String): String = {
    textKey(n.replaceFirst(" Conference$", "").replaceFirst(" League$", "").replaceFirst("^The ", ""))
  }

  def nodesByTagClass(n: Node, tag: String, cls: String): NodeSeq = {
    (n \\ tag).filter(node => (node \ "@class").text == cls)
  }
}
