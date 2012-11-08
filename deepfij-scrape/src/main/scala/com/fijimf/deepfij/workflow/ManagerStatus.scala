package com.fijimf.deepfij.workflow

sealed trait ManagerStatus

case object NotInitialized extends ManagerStatus

case object Running extends ManagerStatus




