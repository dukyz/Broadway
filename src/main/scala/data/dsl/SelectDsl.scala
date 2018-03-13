package data.dsl

trait SelectDsl extends BaseDsl {
    
    def select(fields:String*):SelectPart = {
        new SelectPart()
    }
    
    private[this] class SelectPart extends ActionPart {
        
        def from(conditions:String*):FromPart = {
            new FromPart()
        }
    }
    
    private[this] class FromPart extends ResourcePart {
        def where:WherePart = {
            new WherePart()
        }
    }
    
    private[this] class WherePart extends ConditionPart {
    
    }
    
    
    
}

