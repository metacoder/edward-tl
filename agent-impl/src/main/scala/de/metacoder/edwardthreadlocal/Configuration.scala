package de.metacoder.edwardthreadlocal

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.Paths
import java.util.Properties


object Configuration {

  private val EntryPointClassNameKey = "entryPointClassName"
  private val BeforeMethodNameKey = "beforeMethodName"
  private val AfterMethodNameKey = "afterMethodName"

  private def initializeDefaultConfigFile(location: File): Unit ={
    System.out.println(s"Initializing agent configuration in ${location.getAbsolutePath}")
    val initialProperties = new Properties()
    initialProperties.put(EntryPointClassNameKey, "de/metacoder/edwardthreadlocal/test/Main")
    initialProperties.put(BeforeMethodNameKey, "beforeBL")
    initialProperties.put(AfterMethodNameKey, "afterBL")

    initialProperties.store(new FileOutputStream(location), "auto generated agent configuration. you can change this file")
  }

  private def loadProperties(location: File): Properties = {
    val configurationProperties = new Properties()
    val fis = new FileInputStream(configFile)
    configurationProperties.load(fis)
    fis.close()
    configurationProperties
  }

  private val agentDirectory = new File(getClass.getProtectionDomain.getCodeSource.getLocation.toURI).getParentFile
  assert(agentDirectory.isDirectory)

  private val configFile = Paths.get(agentDirectory.getAbsolutePath, "agent.properties").toFile

  if(!configFile.exists){
    initializeDefaultConfigFile(configFile)
  }

  private val properties = loadProperties(configFile)


  def entryPointClassName: String = properties.get(EntryPointClassNameKey).toString
  def beforeMethodName: String = properties.get(BeforeMethodNameKey).toString
  def afterMethodName: String = properties.get(AfterMethodNameKey).toString


}
