package tanuki.recorder

import tanuki.misc.similarInList

import java.io.File
import java.io.FileOutputStream
import scala.io.Source

def rec_getCaptureArgs(config: Seq[String] = List()): List[String] =
  val cfg =
    if config.length == 0 then rec_readConfig()
    else config

  val vcapture = rec_getvcapture(cfg)
  val acapture = rec_getacapture(cfg)
  val vcapture_args = capture_x11(vcapture(1).toInt, vcapture(2).toInt, vcapture(3).toInt)
  val acapture_args = capture_pulse(acapture(1))

  vcapture_args ++ acapture_args

def rec_getEncodeArgs(config: Seq[String] = List()): List[String] =
  val cfg =
    if config.length == 0 then rec_readConfig()
    else config
  val vcodec = rec_getvcodec(cfg)
  val acodec = rec_getacodec(cfg)

  val v_args =
    vcodec(0) match
    case "x264" =>
      video_setx264(vcodec(1), vcodec(2).toByte, vcodec(3))
    case "x264rgb" =>
      video_setx264rgb(vcodec(1), vcodec(2).toByte)
    case _ => List[String]()
  val a_args =
    acodec(0) match
      case "pcm" =>
        audio_setPCM(acodec(1).toByte)
      case "opus" =>
        audio_setOpus(acodec(1).toInt)
      case "mp3" =>
        audio_setmp3(acodec(1).toInt)
      case _ => List[String]()

  v_args ++ a_args

def rec_getFilterArgs(config: Seq[String] = List()): List[String] =
  val cfg =
    if config.length == 0 then rec_readConfig()
    else config
  val f_crop = rec_getCrop(cfg)
  val f_scale = rec_getScale(cfg)

  val c_args = tanukifilter_crop(f_crop(0).toInt, f_crop(1).toInt)
  val s_args = tanukifilter_scale(f_scale(0).toInt, f_scale(1).toInt)

  c_args ++ s_args
