package code.snippet

import net.liftweb._
import http._
import common._
import util._
import util.Helpers._
import js._
import JsCmds._
import JE._
import json._
import org.joda.time._

import java.util._

import code.model._
import code.comet._

case class CreateTodoData(content: String)

object CreateTodo {

  private implicit val format = DefaultFormats  
  
  def render = { 
 
    def handleJson(json: JValue, todoType: String) : JsCmd = {
	  val data = json.extract[CreateTodoData]
	  val newTodo = Todo(TodoStorage.generateId, 
	      data.content, 
	      new DateTime().toDate.toString,
	      todoType)
	  TodoServer ! IncomingTodo(newTodo)
	  JE.Call("informUser")
    }
    
	def doIt(json: JValue) : JsCmd = handleJson(json, "normal")
	def doItQuickly(json: JValue) : JsCmd = handleJson(json, "important")
	    
    "@do-it [onclick]" #> SHtml.jsonCall(JE.Call("createTodoData"), doIt _) &
      "#do-it-quickly [onclick]" #> SHtml.jsonCall(JE.Call("createTodoData"), doItQuickly _)
  }
    
}