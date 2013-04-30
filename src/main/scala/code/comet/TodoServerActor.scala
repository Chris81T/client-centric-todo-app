package code.comet

import net.liftweb._
import http._
import util._
import Helpers._
import http._
import actor._
import util.Helpers._
import js._
import JsCmds._
import JE._
import json._

import code.model._

// actor messages inside the todo app
case object HelloNewActor
case class IncomingTodo(todo: Todo)
case class FinishedTodo(todo: Todo)
case class RemovedTodo(todo: Todo)

object TodoServer extends LiftActor with ListenerManager {

  def createUpdate = HelloNewActor
  
  override def lowPriority = {
    case todo: IncomingTodo => 	  
      TodoStorage.appendTodo(todo.todo)
      updateListeners(todo)
    case todo: FinishedTodo => 
      TodoStorage.appendFinishedTodo(todo.todo)
      updateListeners(todo)
  }  
}

class TodoActor extends CometActor with CometListener {

  def registerWith = TodoServer
  
  /**
   * this is still a playground project. Please use a logger mechanism instead
   * of stdout println's in more productive projects!
   */
  private def printRef = print("[ actor :: " + toString + " ] ")
  
  private implicit val format = DefaultFormats  
  
  private def jsonTodo(todo: Todo) : JValue = Extraction.decompose(todo) 
  private def jsonTodos(todos: List[Todo]) : JValue = Extraction.decompose(todos) 
  
  override def lowPriority = {
    case HelloNewActor => printRef; println("[ ACTOR ] is still alive...")
    case IncomingTodo(todo) => 
      printRef; println("[ ACTOR ] incoming todo...")
      partialUpdate(JE.Call("incomingTodo", jsonTodo(todo)).cmd) 
    case FinishedTodo(todo) => 
      printRef; println("[ ACTOR ] incoming finished todo...")
      partialUpdate(JE.Call("incomingFinishedTodo", jsonTodo(todo)).cmd)
  }
  
  def render = {
	printRef; println("[ render ]")
    partialUpdate(JE.Call("appendTodos", jsonTodos(TodoStorage.todos)).cmd & 
      JE.Call("appendFinishedTodos", jsonTodos(TodoStorage.finishedTodos)).cmd)
    ClearNodes
  } 
    
  
}