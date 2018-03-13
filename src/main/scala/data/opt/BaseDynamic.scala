package data.opt

import scala.language.dynamics

trait BaseDynamic extends Dynamic {
    
    def selectDynamic(name: String):Any = {
        ???
    }
    
    def updateDynamic(name:String)(value: Any):Any = {
        ???
    }
    
    def applyDynamic(method: String)(args: Any*):Any = {
        ???
    }
    
    def applyDynamicNamed(method: String)(args:(String,Any)*):Any = {
        ???
    }
}
