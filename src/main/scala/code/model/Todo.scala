package code.model

case class Todo(id: Int, content: String, timestamp: String, todoType: String)

object TodoStorage {

  var todos: List[Todo] = Nil
  var finishedTodos: List[Todo] = Nil
  
  def appendTodo(todo: Todo) = todos ::= todo

  def appendFinishedTodo(todo: Todo) = finishedTodos ::= todo

  def deleteTodo(todo: Todo) = todos = todos.filter(_ eq todo)
  
  def generateId() = todos.length
  
  def getTodo(id: Int): Option[Todo] = todos.find(_.id.equals(id))
}