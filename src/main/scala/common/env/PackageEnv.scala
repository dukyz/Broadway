package common.env

trait PackageEnv extends RunningEnv{
    
    private var _packageInited = false
    
    protected def init
    
    private def initialize = {
        
        if (! _packageInited){
            _packageInited = true
            init
        }
    }
    
    
    def run = {
        initialize
        
        
    }
    
}
