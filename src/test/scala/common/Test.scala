package common
import scala.language.dynamics

class Test extends CassandraObject with Dynamic {
    
    def selectDynamic(name: String) = name
    
    override def insert: Unit = {
    
    }
}
