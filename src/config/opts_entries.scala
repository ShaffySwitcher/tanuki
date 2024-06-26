package tanuki.config

import tanuki.misc.similarInList
import java.io.File
import java.io.FileOutputStream
import scala.io.Source

def getGames(cfg: Seq[String]): List[String] = getValues(cfg, "game=")
def getGames_cmd(cfg: Seq[String]): List[String] = getValues(cfg, "game_cmd=")
//def getNativeGames(cfg: Seq[String]): List[String] = getValues(cfg, "native-game=")
def getDatas(cfg: Seq[String]): List[String] = getValues(cfg, "data=")


def gamecmd_getname(entry: String, name: String = "", i: Int = 0): String =
  if i >= entry.length || entry(i) == ':' then name
  else gamecmd_getname(entry, name + entry(i), i+1)

def gamecmd_getcmd(entry: String, arg: String = "", cmd: Vector[String] = Vector(), i: Int = 0): Vector[String] =
  if i >= entry.length then
    if arg == "" then cmd else cmd :+ arg
  else if entry(i) == ':' then
    gamecmd_getcmd(entry, "", cmd :+ arg, i+1)
  else
    gamecmd_getcmd(entry, arg + entry(i), cmd, i+1)
