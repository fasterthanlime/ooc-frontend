
#include <stdio.h>
#include <mango/mangoobject.h>
#include <lightning.h>
#include <gc/gc.h>

/*
MangoObject *mango_object_new(const MangoClass *class)
{
    MangoObject *this = malloc(class->size);
    if(this) {
        this->class = class;
        class->initialize(this);
    }

    return this;
}
*/

void (*memb_func_ptr(const MangoObject *this, void (*func)()))()
{
	jit_insn *codeBuffer = GC_malloc(32);
	
	char *start, *end;           /* a couple of labels */

	// myFunction is a pointer to the generated code
	void (*myFunction)() = (void (*)()) (jit_set_ip(codeBuffer).vptr);
	start = jit_get_ip().ptr;
	
	jit_prolog(1);
	jit_movi_p(JIT_R0, this); // fill the first register with our object
	jit_prepare(1); // we're gonna pass one argument
		jit_pusharg_p(JIT_R0); // pass the first register as the argument
	jit_finish(func); // call our member function
	
	jit_ret();
	end = jit_get_ip().ptr;

	/* call the generated code, passing its size as argument */
	jit_flush_code(start, end);
	//printf("Wrapper function, took %d bytes\n", end - start);
	return myFunction;	
}
