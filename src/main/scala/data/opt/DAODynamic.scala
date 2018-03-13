package data.opt

import scala.collection.mutable

object DAODynamic {
    def apply(): DAODynamic = new DAODynamic()
    def select:DAODynamic = new DAODynamic()
}

class DAODynamic extends BaseDynamic{
    
    private var locked:Boolean = false
    private var executeDsl:DAODsl = null
    private val queryFields = mutable.Set[String]()
    private val whereCondition = Seq[String]()
    
    override def selectDynamic(name: String) = {
        queryFields += name
        name
    }
    
    override def updateDynamic(name: String)(value: Any) = {
    
    }
    
    def as(dsl:DAODsl):DAODsl = {
        executeDsl = dsl
        executeDsl
    }
    
    
    
    
    
}
