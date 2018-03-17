package common.env

trait PackageEnv extends RunningEnv{
    
    private var _packageInited = false
    
    protected def init
    
    private def initialize = {
        
        if (! _packageInited){
            init
            _packageInited = true
        }
    }
    
    def run = {
        initialize
    }
    
}
