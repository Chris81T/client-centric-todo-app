package code.api

import code.model._
import code.comet._

import net.liftweb._
import common._
import http._
import rest._
import json._
import util.Helpers
import util.Helpers._

object TodoHelper extends RestHelper with TodoResource{
	serve{
	  case "api" :: "done" :: id :: Nil Post _ => done(id)
	  case "api" :: "remove" :: id :: Nil Post _ => remove(id)
	}
}

trait TodoResource {
  
	/**
	 * variant one
	 */
	def done(idStr: String): LiftResponse = {		
	  (for {
	    id <- Helpers.asInt(idStr)
	    todo <- Box(TodoStorage.getTodo(id))
	  } yield {
	    TodoStorage.deleteTodo(todo)
	    TodoStorage.appendFinishedTodo(todo)
	    TodoServer ! FinishedTodo(todo)
	    OkResponse()
	  }).getOrElse {
	    BadResponse()
	  }
	}	
	
	/**
	 * variant two
	 */
	def remove(idStr: String): LiftResponse = {
	  val id = Helpers.asInt(idStr)
	  id match {
	    case Full(id) => 
	      TodoStorage.getTodo(id) match {
	        case Some(todo) =>
	          	      TodoStorage.deleteTodo(todo)
	          	      TodoServer ! RemovedTodo(todo)
	          	      OkResponse()
	        case None => BadResponse()
	      }
	    case _ => BadResponse()
	  }
	}
}