package tanuki.tui

import tanuki.runner.*
import tanuki.config.*
import tanuki.data.*
import java.io.File
import scala.sys.exit

val green = foreground("green")
val default = foreground("default")
val yellow = foreground("yellow")

private def getList(l: List[String], txt: String = s"Choose an entry\n\n${green}${0}:${default} Exit\n\n", i: Int = 0): String =
  if i >= l.length then
    txt
  else
    val line = s"${green}${i+1}:${default} ${l(i)}\n"
    getList(l, txt + line, i+1)

private def readLoop(txt: String, maxval: Int): Int =
  val answer = answerToNumber(spawnAndRead(txt))
  if answer == 0 || (1 to maxval).contains(answer) then
    answer
  else
    readLoop(txt, maxval)

def tui_title() =
  val text = s"$yellow[Tanuki Launcher]$default\nVersion 0.1\n\n${green}0:$default Exit\n${green}1:$default Play\n${green}2:$default Manage screenshotsa\n${green}3:$default Configure launcher\n\n"
  while true do
    val answer = readLoop(text, 3)
    answer match
      case 0 =>
        exit()
      case 1 =>
        tui_play()
      case 2 =>
        tui_screenshots()
      case 3 =>


def tui_noentries() =
  val text = s"No entries have been found!\nWould you like to configure Tanuki now? $yellow(y/n)$default"
  val answer = spawnAndRead(text)
  if answer == "y" || answer == "yes" then
    val cfg = tui_configure()
    writeConfig(cfg, true)

def tui_configerror() =
  val text = s"There's an error in your config.txt!\nYou might have a setting that isn't configured properly, or a game entry with a path that does not lead to a file, or a data entry with a path that does not lead to a directory!\n\nWould you like to configure Tanuki now and delete the old configuration file? $yellow(y/n)$default"
  val answer = spawnAndRead(text)
  if answer != "yes" && answer != "y" then
    println("Quitting Tanuki...")
    exit()
  else
    val cfg = tui_configure()
    writeConfig(cfg, false)

def tui_configure(): List[String] =
  def addGame(): String =
    val name = readUserInput("Type the name of your game entry to add (for example: Touhou 10)")
    val path = readUserInput("Type the full path to your game's executable")
    s"game=$name:$path"

  def addData(): String =
    val name = readUserInput("Type the name of your game entry to add (for example: Touhou 10 replays)")
    val path = readUserInput("Type the full path to your game's executable")
    s"data=$name:$path"

  def menu(l: List[String] = List()): List[String] =
    val text = getList(List("Game", "Data"),s"Choose the entry type to add\n\n${green}${0}:${default} Done\n\n")
    spawnAndRead(text) match
      case "0" =>
        l
      case "1" =>
        menu(l :+ addGame())
      case "2" =>
        menu(l :+ addData())
      case _ =>
        menu(l)

  val cfg = menu()
  val command =
    val ans = readUserInput(s"Type the command/program to launch Touhou with or leave it blank to disable")
    if ans != "" then
      s"command=$ans"
    else
      ""
  val usesteamrun =
    val yn = readUserInput(s"It seems you are using NixOS\nRunning a custom wine build might not work out of the box\nWould you like to enable the use of steam-run to launch your command? $yellow(y/n)$default")
    if yn == "yes" || yn == "y" then
      true
    else
      false
  if usesteamrun then
    List(command, "use_steam-run=true") ++ cfg
  else
    command :: cfg


def tui_play() =
  val games = getGames(readConfig())
  if games.length == 0 then
    tui_noentries()
  else
    val names = games.map(x => parseEntry(x)(0))
    val paths = games.map(x => parseEntry(x)(1))
    val text = getList(names, s"Choose a game to play\n\n${green}${0}:${default} Exit\n\n")

    val answer = readLoop(text, names.length)
    if answer != 0 then
      println(s"Launching ${names(answer-1)}\nGirls are now praying, please wait warmly...")
      launchGame(paths(answer-1))

def tui_screenshots() =
  def chooseDir(path: String) =
    val dirs = getScreenshotDirs(path)
    val text = getList(dirs, s"The following screenshot folders in $path were found\nChoose a screenshot folder\n\n${green}${0}:${default} Exit\n\n")

    val answer = readLoop(text, dirs.length)
    if answer != 0 then
      chooseScreenshot(s"$path/${dirs(answer-1)}")

  def chooseScreenshot(path: String) =
    val images = File(path)
      .list()
      .toList
      .filter(x => File(s"$path/$x").isFile && (x.contains(".png") || x.contains(".bmp")))
    val text = getList(images, s"Choose a screenshot\n\n${green}${0}:${default} Exit\n\n")
    val answer = readLoop(text, images.length)
    if answer != 0 then
      screenshot_view(s"$path/${images(answer-1)}")

  val data = getDatas(readConfig())
  if data.length == 0 then
    tui_noentries()
  else
    val names = data.map(x => parseEntry(x)(0))
    val paths = data.map(x => parseEntry(x)(1))
    val text = getList(names)

    val answer = readLoop(text, names.length)
    if answer != 0 then
      chooseDir(paths(answer-1))

// def tui_data() =
//   val data = getDatas(readConfig())
//   val names = data.map(x => parseEntry(x)(0))
//   val paths = data.map(x => parseEntry(x)(1))
//   val text = getList(names)
//
//   var done = false
//   while done == false do
//     val answer = answerToNumber(spawnAndRead(text))
//
//     if answer == 0 then
//       done = true
//     else if (1 to names.length).contains(answer) then
//       done = true
//       println(s"Launching ${names(answer-1)}\nGirls are now praying, please wait warmly...")
//       launchGame(paths(answer-1))
//
//
// def tui_opendata() =
