Class: cover {
	
	/// Number of bytes to allocate for a new instance of this class 
	size: SizeT

	/// Human readable representation of the name of this class
    name: String
	
	/// Pointer to instance of super-class
	super: const Class*
	
	/// Initializer: set default values for a new instance of this class
	initialize: Func (Object)
	
	/// Finalizer: cleans up any objects belonging to this instance
    destroy: Func (Object)
	
}

Object: class {

	class: const Class*
	
}

_Object_new: func (class: const Class*) -> Object {
    this = gc_malloc(class@ size) : Object
    if(this) {
        this class = class
        class@ initialize(this)
    }
    return this
}
