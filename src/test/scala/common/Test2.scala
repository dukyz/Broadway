package common



case object CassandraTable {
    
    class FROM {
        def from(a:String):WHERE = {
            new WHERE()
        }
    }
    
    class WHERE {
        def where(a:String) = {
        
        }
    }
    
    def select (a :String*):FROM = {
        new FROM()
    }
    
   
    
}
