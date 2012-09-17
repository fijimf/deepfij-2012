package com.fijimf.deepfij.workflow

sealed trait StartupMode

case object ColdStartup extends StartupMode

case object WarmStartup extends StartupMode

case object HotStartup extends StartupMode

